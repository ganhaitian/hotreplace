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

package com.windhot.hotreplace.test.replacement.staticfield;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

import com.windhot.hotreplace.test.util.ClassReplacer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class StaticFieldReplacementTest {

    @BeforeClass
    public static void setup() {
        ClassReplacer r = new ClassReplacer();
        r.queueClassForReplacement(StaticFieldClass.class, StaticFieldClass1.class);
        r.replaceQueuedClasses(true);
    }

    @Test
    public void testStaticFieldReplacement() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        Long v = StaticFieldClass.incAndGet();
        Assert.assertEquals((Object)1L, v);
        v = StaticFieldClass.incAndGet();
        Assert.assertEquals((Object)2L, v);
    }

    @Test
    public void testAddedStaticFieldGetDeclaredFields() {
        Field[] fields = StaticFieldClass.class.getDeclaredFields();
        boolean removedField = false;
        boolean longField = false;
        boolean list = false;
        for (Field f : fields) {
            if (f.getName().equals("removedField")) {
                removedField = true;
            }
            if (f.getName().equals("longField")) {
                longField = true;
            }
            if (f.getName().equals("list")) {
                list = true;
            }
        }
        Assert.assertTrue( list);
        Assert.assertTrue( longField);
        Assert.assertTrue( !removedField);
    }

    @Test
    public void testAddedStaticFieldGetFields() {
        Field[] fields = StaticFieldClass.class.getFields();
        boolean removedField = false;
        boolean longField = false;
        boolean list = false;
        for (Field f : fields) {
            if (f.getName().equals("removedField")) {
                removedField = true;
            }
            if (f.getName().equals("longField")) {
                longField = true;
            }
            if (f.getName().equals("list")) {
                list = true;
            }
        }
        Assert.assertTrue( !list);
        Assert.assertTrue( longField);
        Assert.assertTrue( !removedField);
    }

    @Test
    public void testStaticFieldGenericType() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Field f = StaticFieldClass.class.getDeclaredField("list");
        Assert.assertEquals(String.class,  ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0]);
    }

    @Test(expected = NoSuchFieldException.class)
    public void testStaticFieldGetFieldNonPublicFieldsNotAccessible() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Field f = StaticFieldClass.class.getField("list");
    }

}
