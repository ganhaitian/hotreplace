package com.windhot.hotreplace.api;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * @author GanHaitian
 */
public interface ChangedMethod {
    Set<ChangedAnnotation> getChangedAnnotations();

    String getName();

    Class<?> getReturnType();

    Type getGenericReturnType();

    List<Class<?>> getParameterTypes();

    List<Type> getGenericTypes();

    int getModifiers();
}
