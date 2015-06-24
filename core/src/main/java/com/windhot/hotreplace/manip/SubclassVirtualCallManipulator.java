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

package com.windhot.hotreplace.manip;

import java.util.Map;
import java.util.Set;

import com.windhot.hotreplace.manip.data.SubclassVirtualCallData;
import com.windhot.hotreplace.manip.util.ManipulationDataStore;
import com.windhot.hotreplace.runtime.VirtualDelegator;
import com.windhot.hotreplace.util.DescriptorUtils;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import com.windhot.hotreplace.manip.util.ManipulationUtils;

/**
 * this manipulator adds code that looks like:
 * <p/>
 * if(!this.getClass().getName().equals("CurrentClass")) //if this is a subclass
 * {
 * if(VirtualDelegator.contains(this,methodName,
 * methodDescriptor))
 * {
 * return VirtualDelegator.run(this,methodName,
 * methodDescriptor));
 * }
 * }
 * <p/>
 * to a class
 *
 * @author Gan
 */
public class SubclassVirtualCallManipulator implements ClassManipulator {

    private final ManipulationDataStore<SubclassVirtualCallData> data = new ManipulationDataStore<SubclassVirtualCallData>();

    public void addClassData(String className, ClassLoader classLoader, String parentClassName, ClassLoader parentClassLoader, String methodName, String methodDesc) {
        data.add(parentClassName, new SubclassVirtualCallData(parentClassLoader, parentClassName, methodName, methodDesc));
        VirtualDelegator.add(classLoader, className, methodName, methodDesc);
    }

    public void clearRewrites(String className, ClassLoader classLoader) {
        // we don't need to clear them. This is handled by clearing the data in
        // VirtualDelegator
        VirtualDelegator.clear(classLoader, className);
    }

    public boolean transformClass(ClassFile file, ClassLoader loader, boolean modifiableClass, final Set<MethodInfo> modifiedMethods) {
        boolean modified = false;
        Map<String, Set<SubclassVirtualCallData>> loaderData = data.getManipulationData(loader);
        if (loaderData.containsKey(file.getName())) {
            Set<SubclassVirtualCallData> d = loaderData.get(file.getName());
            for (SubclassVirtualCallData s : d) {
                for (Object m : file.getMethods()) {
                    MethodInfo method = (MethodInfo) m;
                    if (method.getName().equals(s.getMethodName()) && method.getDescriptor().equals(s.getMethodDesc())) {

                        modified = true;

                        // we have the method
                        // lets append our code to the top
                        // first create the stuff inside the coditionals

                        Bytecode run = new Bytecode(file.getConstPool());
                        run.add(Opcode.ALOAD_0);
                        run.addLdc(method.getName());
                        run.addLdc(method.getDescriptor());
                        String[] params = DescriptorUtils.descriptorStringToParameterArray(method.getDescriptor());
                        int count = 1;
                        for (int i = 0; i < params.length; ++i) {
                            if (params[i].length() > 1) {
                                run.addAload(count);
                            } else if (params[i].equals("I") || params[i].equals("Z") || params[i].equals("S") || params[i].equals("B")) {
                                run.addIload(count);
                            } else if (params[i].equals("F")) {
                                run.addFload(count);
                            } else if (params[i].equals("J")) {
                                run.addLload(count);
                                count++;
                            } else if (params[i].equals("D")) {
                                run.addDload(count);
                                count++;
                            }
                            count++;
                        }
                        ManipulationUtils.pushParametersIntoArray(run, method.getDescriptor());
                        run.addInvokestatic(VirtualDelegator.class.getName(), "run", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;");
                        ManipulationUtils.MethodReturnRewriter.addReturnProxyMethod(method.getDescriptor(), run);

                        Bytecode cd = new Bytecode(file.getConstPool());
                        cd.add(Opcode.ALOAD_0);
                        cd.addLdc(file.getName());
                        cd.addLdc(method.getName());
                        cd.addLdc(method.getDescriptor());
                        cd.addInvokestatic(VirtualDelegator.class.getName(), "contains", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z");
                        cd.add(Opcode.IFEQ); // if contains is true
                        ManipulationUtils.add16bit(cd, run.getSize() + 3);

                        Bytecode b = new Bytecode(file.getConstPool());
                        // this.getClass()
                        b.add(Opcode.ALOAD_0);
                        b.addInvokevirtual("java.lang.Object", "getClass", "()Ljava/lang/Class;");
                        b.addInvokevirtual("java.lang.Class", "getName", "()Ljava/lang/String;");
                        // now we have the class name on the stack
                        // push the class being manipulateds name onto the stack
                        b.addLdc(file.getName());
                        b.addInvokevirtual("java.lang.Object", "equals", "(Ljava/lang/Object;)Z");
                        // now we have a boolean on top of the stack
                        b.add(Opcode.IFNE); // if true jump
                        ManipulationUtils.add16bit(b, run.getSize() + cd.getSize() + 3);

                        try {
                            method.getCodeAttribute().iterator().insert(run.get());
                            method.getCodeAttribute().iterator().insert(cd.get());
                            method.getCodeAttribute().iterator().insert(b.get());
                            method.getCodeAttribute().computeMaxStack();
                        } catch (BadBytecode e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return modified;
    }

}
