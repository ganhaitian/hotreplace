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

package com.windhot.hotreplace.test.replacement.instancefield;

import java.lang.reflect.InvocationTargetException;

import com.windhot.hotreplace.test.util.ClassReplacer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class InstanceFieldTest {
    @BeforeClass
    public static void setup() {
        ClassReplacer rep = new ClassReplacer();
        rep.queueClassForReplacement(InstanceFieldClass.class, InstanceFieldClass1.class);
        rep.replaceQueuedClasses();
    }

    @Test
    public void testAddingInstanceField() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        InstanceFieldClass ns = new InstanceFieldClass();
        ns.inc();
        Assert.assertEquals(1, ns.get());
        ns.inclong();
        Assert.assertEquals(2, ns.getlong());
        Assert.assertEquals("aa", ns.getSv());
    }

    @Test
    public void testChangingInstanceFieldType() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        ClassReplacer rep = new ClassReplacer();
        rep.queueClassForReplacement(ChangeFieldType.class, ChangeFieldType1.class);
        rep.replaceQueuedClasses();
        ChangeFieldType type = new ChangeFieldType();
        Assert.assertEquals(20, type.getValue());
    }

    @Test
    public void testSettingObjectInstanceField() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        InstanceFieldClass ns = new InstanceFieldClass();
        ns.setFa2(this);
        Assert.assertEquals(this, ns.getFa2());
    }
}
