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

package com.windhot.hotreplace.test.replacement.finalmethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.windhot.hotreplace.test.util.ClassReplacer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class FinalMethodReplacementTest {

    @BeforeClass
    public static void setup() {
        ClassReplacer cr = new ClassReplacer();
        cr.queueClassForReplacement(FinalMethodClass.class, FinalMethodClass1.class);
        cr.replaceQueuedClasses();
    }

    @Ignore
    @Test
    public void testNonFinalMethodIsNonFinal() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        FinalMethodClass cl = new FinalMethodClass();
        Method method = cl.getClass().getMethod("finalMethod-replaced");
        Assert.assertTrue(Modifier.isFinal(method.getModifiers()));
        Assert.assertEquals("finalMethod-replaced", method.invoke(cl));
    }

    @Ignore
    @Test
    public void testFinalMethodIsFinal() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        FinalMethodClass cl = new FinalMethodClass();
        Method method = cl.getClass().getMethod("nonFinalMethod-replaced");
        Assert.assertFalse(Modifier.isFinal(method.getModifiers()));
        Assert.assertEquals("nonFinalMethod-replaced", method.invoke(cl));
    }
}
