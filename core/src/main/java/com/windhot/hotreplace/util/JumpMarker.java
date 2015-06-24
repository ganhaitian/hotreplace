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

package com.windhot.hotreplace.util;

import javassist.bytecode.Bytecode;

/**
 * Interface that represents a conditional jump or goto in bytecode.
 * <p/>
 * When the mark method is called the jump site is updated to point to the end
 * of the bytecode stream.
 *
 * @author GanHaitian
 * @see JumpUtils
 */
public interface JumpMarker {
    /**
     * Changes the jump instructions target to the next bytecode to be added to
     * the {@link Bytecode}
     */
    void mark();
}
