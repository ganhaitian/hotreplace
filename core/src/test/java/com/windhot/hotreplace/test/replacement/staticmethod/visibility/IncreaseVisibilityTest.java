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

package com.windhot.hotreplace.test.replacement.staticmethod.visibility;

import com.windhot.hotreplace.test.replacement.staticmethod.visibility.otherpackage.StaticMethodVisibilityClass;
import com.windhot.hotreplace.test.replacement.staticmethod.visibility.otherpackage.StaticMethodVisibilityClass1;
import com.windhot.hotreplace.test.replacement.staticmethod.visibility.otherpackage.UnchangedStaticMethodCallingClass;
import com.windhot.hotreplace.test.util.ClassReplacer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class IncreaseVisibilityTest {
    @BeforeClass
    public static void setup() {
        ClassReplacer r = new ClassReplacer();
        r.queueClassForReplacement(StaticMethodVisibilityCallingClass.class, StaticMethodVisibilityCallingClass1.class);
        r.queueClassForReplacement(StaticMethodVisibilityClass.class, StaticMethodVisibilityClass1.class);
        r.replaceQueuedClasses();
    }

    @Test
    public void testExistingMethod() {
        Assert.assertEquals("helo world", StaticMethodVisibilityClass.callingMethod());
    }

    @Test
    public void testNewExternalMethod() {
        Assert.assertEquals("helo world", StaticMethodVisibilityCallingClass.callingClass());
    }

    @Test
    public void testUnchangedClassCallingExternalMethod() {
        Assert.assertEquals("helo world", UnchangedStaticMethodCallingClass.callingClass());
    }
}
