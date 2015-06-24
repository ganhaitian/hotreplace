/*
 * Copyright 2012, GanHaitian, and individual contributors as indicated
 * by the @authors tag.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.windhot.hotreplace.core;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.windhot.hotreplace.api.Extension;
import com.windhot.hotreplace.data.BaseClassData;
import com.windhot.hotreplace.data.ClassDataStore;
import com.windhot.hotreplace.reflection.ReflectionInstrumentationSetup;
import com.windhot.hotreplace.transformation.HotreplaceTransformer;
import com.windhot.hotreplace.util.NoInstrument;
import javassist.ClassPool;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import com.windhot.hotreplace.api.environment.CurrentEnvironment;
import com.windhot.hotreplace.data.InstanceTracker;
import com.windhot.hotreplace.manip.Manipulator;
import com.windhot.hotreplace.manip.util.ManipulationUtils;

/**
 * This file is the transformer that instruments classes as they are added to
 * the system.
 *
 * @author Gan
 */
public class Transformer implements HotreplaceTransformer {

    private static final Manipulator manipulator = new Manipulator();

    private final Set<String> trackedInstances = new HashSet<String>();

    private final List<HotreplaceTransformer> integrationTransformers = new CopyOnWriteArrayList<HotreplaceTransformer>();

    Transformer(Set<Extension> extension) {
        ReflectionInstrumentationSetup.setup(manipulator);
        for (Extension i : extension) {
            trackedInstances.addAll(i.getTrackedInstanceClassNames());
            List<HotreplaceTransformer> t = i.getTransformers();
            if (t != null) {
                integrationTransformers.addAll(t);
            }
        }
    }

    public boolean transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, ClassFile file) throws IllegalClassFormatException, BadBytecode {
        boolean modified = false;
        try {

            if (classBeingRedefined != null) {
                ClassDataStore.instance().markClassReplaced(classBeingRedefined);
            }
            for (HotreplaceTransformer i : integrationTransformers) {
                if (i.transform(loader, className, classBeingRedefined, protectionDomain, file)) {
                    modified = true;
                }
            }
            // we do not instrument any classes from fakereplace
            // if we did we get an endless loop
            // we also avoid instrumenting much of the java/lang and
            // java/io namespace except for java/lang/reflect/Proxy
            if (BuiltinClassData.skipInstrumentation(className)) {
                if (classBeingRedefined != null && manipulator.transformClass(file, loader, false)) {
                    modified = true;
                }
                return modified;
            }


            if (classBeingRedefined == null) {
                AnnotationsAttribute at = (AnnotationsAttribute) file.getAttribute(AnnotationsAttribute.invisibleTag);
                if (at != null) {
                    // NoInstrument is used for testing or by integration modules
                    Object an = at.getAnnotation(NoInstrument.class.getName());
                    if (an != null) {
                        return modified;
                    }
                }
            }

            if (trackedInstances.contains(file.getName())) {
                makeTrackedInstance(file);
                modified = true;
            }

            final boolean replaceable = CurrentEnvironment.getEnvironment().isClassReplaceable(className, loader);
            if (manipulator.transformClass(file, loader, replaceable)) {
                modified = true;
            }

            if (replaceable) {
                if ((AccessFlag.ENUM & file.getAccessFlags()) == 0 && (AccessFlag.ANNOTATION & file.getAccessFlags()) == 0) {
                    modified = true;

                    CurrentEnvironment.getEnvironment().recordTimestamp(className, loader);
                    if (file.isInterface()) {
                        addAbstractMethodForInstrumentation(file);
                    } else {
                        addMethodForInstrumentation(file);
                        addConstructorForInstrumentation(file);
                        addStaticConstructorForInstrumentation(file);
                    }
                }

                BaseClassData baseData = new BaseClassData(file, loader, replaceable);
                ClassDataStore.instance().saveClassData(loader, baseData.getInternalName(), baseData);
            }
            // SerialVersionUIDChecker.testReflectionInfo(loader, file.getName(),
            // file.getSuperclass(), classfileBuffer);
            return modified;
        } finally {
            if (modified) {
                try {
                    for (MethodInfo method : (List<MethodInfo>) file.getMethods()) {
                        method.rebuildStackMap(ClassPool.getDefault());
                    }
                } catch (BadBytecode e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    /**
     * Adds a method to a class that re can redefine when the class is reloaded
     *
     * @param file
     * @throws DuplicateMemberException
     */
    public void addMethodForInstrumentation(ClassFile file) {
        try {
            MethodInfo m = new MethodInfo(file.getConstPool(), Constants.ADDED_METHOD_NAME, Constants.ADDED_METHOD_DESCRIPTOR);
            m.setAccessFlags(0 | AccessFlag.PUBLIC | AccessFlag.SYNTHETIC);

            Bytecode b = new Bytecode(file.getConstPool(), 5, 3);
            if (BuiltinClassData.skipInstrumentation(file.getSuperclass())) {
                b.add(Bytecode.ACONST_NULL);
                b.add(Bytecode.ARETURN);
            } else {
                // delegate to the parent class
                b.add(Bytecode.ALOAD_0);
                b.add(Bytecode.ILOAD_1);
                b.add(Bytecode.ALOAD_2);
                b.addInvokespecial(file.getSuperclass(), Constants.ADDED_METHOD_NAME, Constants.ADDED_METHOD_DESCRIPTOR);
                b.add(Bytecode.ARETURN);

            }
            CodeAttribute ca = b.toCodeAttribute();
            m.setCodeAttribute(ca);
            file.addMethod(m);
        } catch (DuplicateMemberException e) {
            // e.printStackTrace();
        }
        try {
            MethodInfo m = new MethodInfo(file.getConstPool(), Constants.ADDED_STATIC_METHOD_NAME, Constants.ADDED_STATIC_METHOD_DESCRIPTOR);
            m.setAccessFlags(AccessFlag.PUBLIC | AccessFlag.STATIC | AccessFlag.SYNTHETIC);
            Bytecode b = new Bytecode(file.getConstPool(), 5, 3);
            b.add(Bytecode.ACONST_NULL);
            b.add(Bytecode.ARETURN);
            CodeAttribute ca = b.toCodeAttribute();
            m.setCodeAttribute(ca);
            file.addMethod(m);

        } catch (DuplicateMemberException e) {
            // e.printStackTrace();
        }
    }

    public static void addStaticConstructorForInstrumentation(ClassFile file) {
        try {
            MethodInfo m = new MethodInfo(file.getConstPool(), "<clinit>", "()V");
            m.setAccessFlags(AccessFlag.PUBLIC | AccessFlag.STATIC);
            Bytecode b = new Bytecode(file.getConstPool());
            b.add(Opcode.RETURN);
            m.setCodeAttribute(b.toCodeAttribute());
            file.addMethod(m);
        } catch (DuplicateMemberException e) {
            // e.printStackTrace();
        }
    }

    /**
     * Adds a method to a class that re can redefine when the class is reloaded
     *
     * @param file
     * @throws DuplicateMemberException
     */
    public void addAbstractMethodForInstrumentation(ClassFile file) {
        try {
            MethodInfo m = new MethodInfo(file.getConstPool(), Constants.ADDED_METHOD_NAME, Constants.ADDED_METHOD_DESCRIPTOR);
            m.setAccessFlags(AccessFlag.PUBLIC | AccessFlag.ABSTRACT | AccessFlag.SYNTHETIC);
            file.addMethod(m);
        } catch (DuplicateMemberException e) {
            // e.printStackTrace();
        }
    }

    void addConstructorForInstrumentation(ClassFile file) {

        MethodInfo ret = new MethodInfo(file.getConstPool(), "<init>", Constants.ADDED_CONSTRUCTOR_DESCRIPTOR);
        Bytecode code = new Bytecode(file.getConstPool());
        // if the class does not have a constructor return
        if (!ManipulationUtils.addBogusConstructorCall(file, code)) {
            return;
        }
        CodeAttribute ca = code.toCodeAttribute();
        ca.setMaxLocals(4);
        ret.setCodeAttribute(ca);
        ret.setAccessFlags(AccessFlag.PUBLIC | AccessFlag.SYNTHETIC);
        try {
            ca.computeMaxStack();
            file.addMethod(ret);
        } catch (DuplicateMemberException e) {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static Manipulator getManipulator() {
        return manipulator;
    }

    /**
     * modifies a class so that all created instances are registered with
     * InstanceTracker
     *
     * @param file
     * @throws BadBytecode
     */
    public void makeTrackedInstance(ClassFile file) throws BadBytecode {
        for (MethodInfo m : (List<MethodInfo>) file.getMethods()) {
            if (m.getName().equals("<init>")) {
                Bytecode code = new Bytecode(file.getConstPool());
                code.addLdc(file.getName());
                code.addAload(0);
                code.addInvokestatic(InstanceTracker.class.getName(), "add", "(Ljava/lang/String;Ljava/lang/Object;)V");
                CodeIterator it = m.getCodeAttribute().iterator();
                it.skipConstructor();
                it.insert(code.get());
                m.getCodeAttribute().computeMaxStack();
            }
        }
    }
}
