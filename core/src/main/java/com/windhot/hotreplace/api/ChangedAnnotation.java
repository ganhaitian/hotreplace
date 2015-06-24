package com.windhot.hotreplace.api;

import java.lang.annotation.Annotation;

/**
 * @author GanHaitian
 */
public interface ChangedAnnotation extends Changed<Annotation> {
    AnnotationTarget getAnnotationTarget();

    Class<? extends Annotation> getAnnotationType();
}
