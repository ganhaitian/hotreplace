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

/**
 *
 */
package com.windhot.hotreplace.manip.data;

import com.windhot.hotreplace.manip.util.ClassLoaderFiltered;

public class VirtualToStaticData implements ClassLoaderFiltered<VirtualToStaticData> {
    private final String oldClass;
    private final String newClass;
    private final String methodName;
    private final String newMethodName;
    private final String methodDesc;
    private final String newStaticMethodDesc;
    private final ClassLoader classLoader;

    public VirtualToStaticData(String oldClass, String newClass, String methodName, String methodDesc, String newStaticMethodDesc, String newMethodName, ClassLoader classLoader) {
        this.oldClass = oldClass;
        this.newClass = newClass;
        this.methodName = methodName;
        if (newMethodName == null) {
            this.newMethodName = methodName;
        } else {
            this.newMethodName = newMethodName;
        }
        this.methodDesc = methodDesc;
        this.newStaticMethodDesc = newStaticMethodDesc;
        this.classLoader = classLoader;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(oldClass);
        sb.append(" ");
        sb.append(newClass);
        sb.append(" ");
        sb.append(methodName);
        sb.append(" ");
        sb.append(methodDesc);
        sb.append(" ");
        sb.append(newStaticMethodDesc);

        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o.getClass().isAssignableFrom(VirtualToStaticData.class)) {
            VirtualToStaticData i = (VirtualToStaticData) o;
            return oldClass.equals(i.oldClass) && newClass.equals(i.newClass) && methodName.equals(i.methodName) && methodDesc.equals(i.methodDesc) && newStaticMethodDesc.equals(i.newStaticMethodDesc);
        }
        return false;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public String getOldClass() {
        return oldClass;
    }

    public String getNewClass() {
        return newClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getNewMethodName() {
        return newMethodName;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public String getNewStaticMethodDesc() {
        return newStaticMethodDesc;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public VirtualToStaticData getInstance() {
        return this;
    }
}
