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

package com.windhot.hotreplace.transformation;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.windhot.hotreplace.core.ClassLoaderInstrumentation;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;

/**
 * transformer that instruments class loaders to load FakeReplace classes
 *
 * @author Gan
 */
public class ClassLoaderTransformer implements HotreplaceTransformer {


    public ClassLoaderTransformer() {

    }

    @Override
    public boolean transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final ClassFile file) throws IllegalClassFormatException, BadBytecode {
        if (classBeingRedefined != null && ClassLoader.class.isAssignableFrom(classBeingRedefined)) {
            return ClassLoaderInstrumentation.redefineClassLoader(file);
        } else if (classBeingRedefined == null && className.endsWith("ClassLoader")) {
            return ClassLoaderInstrumentation.redefineClassLoader(file);
        }
        return false;
    }
}
