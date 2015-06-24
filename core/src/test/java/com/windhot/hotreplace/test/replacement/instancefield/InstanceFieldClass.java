/*
 * Copyright 2011, GanHaitian
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.windhot.hotreplace.test.replacement.instancefield;

public class InstanceFieldClass {

    int afield;
    int bfield;

    @SomeAnnotation
    int cfield;

    Object otherField;

    public void inc() {

    }

    public int get() {
        return 0;
    }

    public void inclong() {

    }

    public long getlong() {
        return 0;
    }

    public String getSv() {
        return null;
    }

    public Object getFa2() {
        return null;
    }

    public void setFa2(Object fa2) {
    }
}
