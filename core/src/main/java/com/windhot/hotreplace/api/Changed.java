package com.windhot.hotreplace.api;

/**
 * @author GanHaitian
 */
public interface Changed<T> {
    ChangeType getType();

    T getModified();

    T getExisting();
}
