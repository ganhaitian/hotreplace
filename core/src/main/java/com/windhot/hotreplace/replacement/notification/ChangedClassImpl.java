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

package com.windhot.hotreplace.replacement.notification;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.windhot.hotreplace.api.ChangedAnnotation;
import com.windhot.hotreplace.api.ChangedClass;
import com.windhot.hotreplace.api.Changed;
import com.windhot.hotreplace.api.ChangedField;
import com.windhot.hotreplace.api.ChangedMethod;

/**
 * @author GanHaitian
 */
public class ChangedClassImpl implements ChangedClass {

    private final Set<ChangedAnnotation> changedClassAnnotations = new HashSet<ChangedAnnotation>();
    private final Map<Class<? extends Annotation>, Set<ChangedAnnotation>> changedAnnotationsByType = new HashMap<Class<? extends Annotation>, Set<ChangedAnnotation>>();
    private final Set<Changed<ChangedField>> fields = new HashSet<Changed<ChangedField>>();
    private final Set<Changed<ChangedMethod>> methods = new HashSet<Changed<ChangedMethod>>();
    private final Class<?> changedClass;

    public ChangedClassImpl(final Class<?> changedClass) {
        this.changedClass = changedClass;
    }

    /**
     *
     * @return The changed class level annotations
     */
    @Override
    public Set<ChangedAnnotation> getChangedClassAnnotations() {
        return Collections.unmodifiableSet(changedClassAnnotations);
    }

    /**
     * Get all changed annotations of a certain type.
     * This includes field and method level annotations.
     * @param annotationType The type of annotation
     * @return All changed annotations of a specific type
     */
    @Override
    public Set<ChangedAnnotation> getChangedAnnotationsByType(final Class<? extends Annotation> annotationType) {
        final Set<ChangedAnnotation> changedAnnotations = changedAnnotationsByType.get(annotationType);
        if(changedAnnotations == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(changedAnnotations);
    }

    @Override
    public Set<Changed<ChangedField>> getFields() {
        return Collections.unmodifiableSet(fields);
    }

    @Override
    public Set<Changed<ChangedMethod>> getMethods() {
        return Collections.unmodifiableSet(methods);
    }

    @Override
    public Class<?> getChangedClass() {
        return changedClass;
    }

    public void changeClassAnnotation(final ChangedAnnotation annotation) {
        changedClassAnnotations.add(annotation);
        changedAnnotation(annotation);
    }


    private void changedAnnotation(final ChangedAnnotation annotation) {
        Set<ChangedAnnotation> set = changedAnnotationsByType.get(annotation.getAnnotationType());
        if(set == null) {
            changedAnnotationsByType.put(annotation.getAnnotationType(), set = new HashSet<ChangedAnnotation>());
        }
        set.add(annotation);
    }

}
