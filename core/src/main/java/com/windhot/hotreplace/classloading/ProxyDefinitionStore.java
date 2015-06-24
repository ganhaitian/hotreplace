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

package com.windhot.hotreplace.classloading;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.windhot.hotreplace.manip.util.MapFunction;
import com.windhot.hotreplace.core.Constants;
import org.fakereplace.com.google.common.collect.MapMaker;

/**
 * This class holds proxy definitions, that are later loaded by the relevant ClassLoaders
 *
 * @author Gan
 */
public class ProxyDefinitionStore {
    private static Map<ClassLoader, Map<String, byte[]>> proxyDefinitions = new MapMaker().weakKeys().makeComputingMap(new MapFunction(false));

    private static AtomicLong proxyNo = new AtomicLong();

    public static byte[] getProxyDefinition(ClassLoader classLoader, String name) {
        Map<String, byte[]> def = proxyDefinitions.get(classLoader);
        return def.get(name);
    }

    public static void saveProxyDefinition(ClassLoader classLoader, String className, byte[] data) {
        Map<String, byte[]> def = proxyDefinitions.get(classLoader);
        def.put(className, data);
    }

    /**
     * Returns a unique proxy name
     *
     * @return
     */
    public static String getProxyName() {
        return Constants.GENERATED_CLASS_PACKAGE + ".ProxyClass" + proxyNo.incrementAndGet();
    }

}
