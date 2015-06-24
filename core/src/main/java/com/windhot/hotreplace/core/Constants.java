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

public class Constants {
    public static final String GENERATED_CLASS_PACKAGE = "org.fakereplace.proxies";

    public static final String ADDED_METHOD_NAME = "______REDEFINED_METHOD_DELEGATOR_$";

    public static final String ADDED_METHOD_DESCRIPTOR = "(I[Ljava/lang/Object;)Ljava/lang/Object;";

    public static final String ADDED_STATIC_METHOD_NAME = "______REDEFINED_STATIC_METHOD_DELEGATOR_$";

    public static final String ADDED_STATIC_METHOD_DESCRIPTOR = "(I[Ljava/lang/Object;)Ljava/lang/Object;";

    public static final String ADDED_CONSTRUCTOR_DESCRIPTOR = "(I[Ljava/lang/Object;Lorg/fakereplace/core/ConstructorArgument;)V";

    public static final String FINAL_METHOD_ATTRIBUTE = "org.fakereplace.final";

}
