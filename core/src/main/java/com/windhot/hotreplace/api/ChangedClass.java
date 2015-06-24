package com.windhot.hotreplace.api;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author GanHaitian
 */
public interface ChangedClass extends AnnotationTarget {
    Set<ChangedAnnotation> getChangedClassAnnotations();

    Set<ChangedAnnotation> getChangedAnnotationsByType(Class<? extends Annotation> annotationType);

    Set<Changed<ChangedField>> getFields();

    Set<Changed<ChangedMethod>> getMethods();

    Class<?> getChangedClass();
}
