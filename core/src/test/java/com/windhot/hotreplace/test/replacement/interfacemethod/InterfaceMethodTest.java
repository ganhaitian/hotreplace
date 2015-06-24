/*
 * Copyright 2011, GanHaitian
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.windhot.hotreplace.test.replacement.interfacemethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.windhot.hotreplace.test.util.ClassReplacer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class InterfaceMethodTest {
    @BeforeClass
    public static void setup() {
        ClassReplacer rep = new ClassReplacer();
        rep.queueClassForReplacement(InterfaceCallingClass.class, InterfaceCallingClass1.class);
        rep.queueClassForReplacement(SomeInterface.class, SomeInterface1.class);
        rep.queueClassForReplacement(ImplementingClass.class, ImplementingClass1.class);
        rep.replaceQueuedClasses();
    }

    @Test
    public void testAddingInterfaceMethod() {
        SomeInterface iface = new ImplementingClass();
        InterfaceCallingClass caller = new InterfaceCallingClass();
        Assert.assertEquals("added", caller.call(iface));
    }

    @Test
    public void testAddingInterfaceMethodByReflection() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method method = SomeInterface.class.getDeclaredMethod("added");
        ImplementingClass cls = new ImplementingClass();
        Assert.assertEquals("added", method.invoke(cls));
    }
}
