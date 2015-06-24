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

package com.windhot.hotreplace.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import com.windhot.hotreplace.core.Constants;
import com.windhot.hotreplace.util.DescriptorUtils;

/**
 * This class holds everything there is to know about a class that has been seen
 * by the transformer. This stores the information about the original class, not
 * about any modifications
 *
 * @author Gan
 */
public class BaseClassData {

    private final String className;
    private final String internalName;
    private final Set<MethodData> methods;
    private final Set<FieldData> fields;
    private final ClassLoader loader;
    private final String superClassName;
    private final boolean replaceable;

    public BaseClassData(ClassFile file, ClassLoader loader, boolean replaceable) {
        className = file.getName();
        this.replaceable = replaceable;
        internalName = Descriptor.toJvmName(file.getName());
        this.loader = loader;
        superClassName = file.getSuperclass();
        boolean finalMethod = false;
        Set<MethodData> meths = new HashSet<MethodData>();
        for (Object o : file.getMethods()) {
            String methodClassName = className;
            MethodInfo m = (MethodInfo) o;
            MemberType type = MemberType.NORMAL;
            if ((m.getDescriptor().equals(Constants.ADDED_METHOD_DESCRIPTOR) && m.getName().equals(Constants.ADDED_METHOD_NAME))
                    || (m.getDescriptor().equals(Constants.ADDED_STATIC_METHOD_DESCRIPTOR) && m.getName().equals(Constants.ADDED_STATIC_METHOD_NAME))
                    || (m.getDescriptor().equals(Constants.ADDED_CONSTRUCTOR_DESCRIPTOR))) {
                type = MemberType.ADDED_SYSTEM;
            } else if (m.getAttribute(Constants.FINAL_METHOD_ATTRIBUTE) != null) {
                finalMethod = true;
            }

            MethodData md = new MethodData(m.getName(), m.getDescriptor(), methodClassName, type, m.getAccessFlags(), finalMethod);
            meths.add(md);
        }
        this.methods = Collections.unmodifiableSet(meths);
        Set<FieldData> fieldData = new HashSet<FieldData>();
        for (Object o : file.getFields()) {
            FieldInfo m = (FieldInfo) o;
            MemberType mt = MemberType.NORMAL;
            fieldData.add(new FieldData(m, mt, className, m.getAccessFlags()));
        }
        this.fields = Collections.unmodifiableSet(fieldData);
    }

    public BaseClassData(Class<?> cls) {
        className = cls.getName();
        internalName = Descriptor.toJvmName(cls.getName());
        this.loader = cls.getClassLoader();
        replaceable = false;
        if (cls.getSuperclass() != null) {
            superClassName = cls.getSuperclass().getName();
        } else {
            superClassName = null;
        }
        Set<MethodData> meths = new HashSet<MethodData>();
        for (Method m : cls.getDeclaredMethods()) {
            MemberType type = MemberType.NORMAL;
            final String descriptor = DescriptorUtils.getDescriptor(m);
            if ((descriptor.equals(Constants.ADDED_METHOD_DESCRIPTOR) && m.getName().equals(Constants.ADDED_METHOD_NAME))
                    || (descriptor.equals(Constants.ADDED_STATIC_METHOD_DESCRIPTOR) && m.getName().equals(Constants.ADDED_STATIC_METHOD_NAME))) {
                type = MemberType.ADDED_SYSTEM;
            }
            MethodData md = new MethodData(m.getName(), descriptor, cls.getName(), type, m.getModifiers(), false);
            meths.add(md);
        }
        for (Constructor<?> c : cls.getDeclaredConstructors()) {
            MemberType type = MemberType.NORMAL;
            final String descriptor = DescriptorUtils.getDescriptor(c);
            if (descriptor.equals(Constants.ADDED_CONSTRUCTOR_DESCRIPTOR)) {
                type = MemberType.ADDED_SYSTEM;
            }
            MethodData md = new MethodData("<init>", descriptor, cls.getName(), type, c.getModifiers(), false);
            meths.add(md);
        }

        this.methods = Collections.unmodifiableSet(meths);
        Set<FieldData> fieldData = new HashSet<FieldData>();
        for (Field m : cls.getDeclaredFields()) {
            fieldData.add(new FieldData(m));
        }
        this.fields = Collections.unmodifiableSet(fieldData);
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public String getClassName() {
        return className;
    }

    public String getInternalName() {
        return internalName;
    }

    public Collection<MethodData> getMethods() {
        return methods;
    }

    public Collection<FieldData> getFields() {
        return fields;
    }

    public boolean isReplaceable() {
        return replaceable;
    }

}
