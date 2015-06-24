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

package com.windhot.hotreplace.core;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.windhot.hotreplace.api.ChangedClass;
import com.windhot.hotreplace.api.ClassChangeAware;
import com.windhot.hotreplace.api.Attachments;
import com.windhot.hotreplace.classloading.ClassIdentifier;
import org.fakereplace.com.google.common.collect.MapMaker;

public class ClassChangeNotifier {

    private static final ClassChangeNotifier INSTANCE = new ClassChangeNotifier();

    private static final ThreadLocal<Boolean> NOTIFICATION_IN_PROGRESS = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };


    private final Map<ClassLoader, Set<ClassChangeAware>> classChangeAwares = new MapMaker().weakKeys().makeMap();

    public void add(ClassChangeAware aware) {
        if (!classChangeAwares.containsKey(aware.getClass().getClassLoader())) {
            classChangeAwares.put(aware.getClass().getClassLoader(), new HashSet<ClassChangeAware>());
        }
        classChangeAwares.get(aware.getClass().getClassLoader()).add(aware);
    }

     public void afterChange(List<ChangedClass> changed, List<ClassIdentifier> newClasses, final Attachments attachments) {
        if (!NOTIFICATION_IN_PROGRESS.get()) {
            NOTIFICATION_IN_PROGRESS.set(true);
            try {
                Class<?>[] a = new Class[0];
                for (Set<ClassChangeAware> c : classChangeAwares.values()) {
                    for (ClassChangeAware i : c) {
                        try {
                            i.afterChange(changed, newClasses, attachments);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } finally {
                NOTIFICATION_IN_PROGRESS.set(false);
            }
        }
    }

    public void beforeChange(List<Class<?>> changed, List<ClassIdentifier> newClasses, final Attachments attachments) {
        Class<?>[] a = new Class[0];
        for (Set<ClassChangeAware> c : classChangeAwares.values()) {
            for (ClassChangeAware i : c) {
                try {
                    i.beforeChange(changed, newClasses, attachments);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    public static ClassChangeNotifier instance() {
        return INSTANCE;
    }
}
