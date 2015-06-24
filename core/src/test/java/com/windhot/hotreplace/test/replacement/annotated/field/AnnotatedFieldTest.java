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

package com.windhot.hotreplace.test.replacement.annotated.field;

import java.lang.reflect.Field;

import com.windhot.hotreplace.test.util.ClassReplacer;
import org.junit.Assert;
import org.junit.Test;

public class AnnotatedFieldTest {

    @Test
    public void testFieldAnnotations() throws SecurityException, NoSuchFieldException {
        ClassReplacer r = new ClassReplacer();
        r.queueClassForReplacement(FieldAnnotated.class, FieldAnnotated1.class);
        r.replaceQueuedClasses();

        Field m1 = FieldAnnotated.class.getField("field1");
        Field m2 = FieldAnnotated.class.getField("field2");
        Field m3 = FieldAnnotated.class.getField("field3");
        Assert.assertEquals("1", m1.getAnnotation(FieldAnnotation.class).value());
        Assert.assertFalse(m2.isAnnotationPresent(FieldAnnotation.class));
        Assert.assertEquals("3", m3.getAnnotation(FieldAnnotation.class).value());

    }

}
