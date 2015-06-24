package com.windhot.hotreplace.api;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author GanHaitian
 */
public interface ChangedField {
    String getName();

    Class<?> getFieldType();

    Type getGenericType();

    int getModifiers();

    Set<ChangedAnnotation> getChangedAnnotations();
}
