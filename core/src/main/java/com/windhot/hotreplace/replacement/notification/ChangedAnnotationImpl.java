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

import com.windhot.hotreplace.api.AnnotationTarget;
import com.windhot.hotreplace.api.ChangeType;
import com.windhot.hotreplace.api.ChangedAnnotation;

import java.lang.annotation.Annotation;

/**
 * A modified annotation
 *
 * @author GanHaitian
 */
public class ChangedAnnotationImpl extends ChangedImpl<Annotation> implements ChangedAnnotation {

    private final Class<? extends  Annotation> annotationType;
    private final AnnotationTarget annotationTarget;

    public ChangedAnnotationImpl(Annotation modified, Annotation existing, ChangeType type, AnnotationTarget annotationTarget, final Class<? extends Annotation> annotationType) {
        super(modified, existing, type);
        this.annotationTarget = annotationTarget;
        this.annotationType = annotationType;
    }

    @Override
    public AnnotationTarget getAnnotationTarget() {
        return annotationTarget;
    }

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return annotationType;
    }
}
