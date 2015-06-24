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

package com.windhot.hotreplace.manip.util;

import javassist.bytecode.Bytecode;

/**
 * This class is responsible for generating bytecode fragments to box/unbox
 * whatever happens to be on the top of the stack.
 * <p/>
 * It is the calling codes responsibility to make sure that the correct type is
 * on the stack
 *
 * @author Gan
 */
public class Boxing {

    public static void box(Bytecode b, char type) {
        switch (type) {
            case 'I':
                boxInt(b);
                break;
            case 'J':
                boxLong(b);
                break;
            case 'S':
                boxShort(b);
                break;
            case 'F':
                boxFloat(b);
                break;
            case 'D':
                boxDouble(b);
                break;
            case 'B':
                boxByte(b);
                break;
            case 'C':
                boxChar(b);
                break;
            case 'Z':
                boxBoolean(b);
                break;
            default:
                throw new RuntimeException("Cannot box unkown primitive type: " + type);
        }

    }

    public static Bytecode unbox(Bytecode b, char type) {
        switch (type) {
            case 'I':
                return unboxInt(b);
            case 'J':
                return unboxLong(b);
            case 'S':
                return unboxShort(b);
            case 'F':
                return unboxFloat(b);
            case 'D':
                return unboxDouble(b);
            case 'B':
                return unboxByte(b);
            case 'C':
                return unboxChar(b);
            case 'Z':
                return unboxBoolean(b);
        }
        throw new RuntimeException("Cannot unbox unkown primitive type: " + type);
    }

    public static void boxInt(Bytecode bc) {
        bc.addInvokestatic("java.lang.Integer", "valueOf", "(I)Ljava/lang/Integer;");
    }

    public static void boxLong(Bytecode bc) {
        bc.addInvokestatic("java.lang.Long", "valueOf", "(J)Ljava/lang/Long;");
    }

    public static void boxShort(Bytecode bc) {
        bc.addInvokestatic("java.lang.Short", "valueOf", "(S)Ljava/lang/Short;");
    }

    public static void boxByte(Bytecode bc) {
        bc.addInvokestatic("java.lang.Byte", "valueOf", "(B)Ljava/lang/Byte;");
    }

    public static void boxFloat(Bytecode bc) {
        bc.addInvokestatic("java.lang.Float", "valueOf", "(F)Ljava/lang/Float;");
    }

    public static void boxDouble(Bytecode bc) {
        bc.addInvokestatic("java.lang.Double", "valueOf", "(D)Ljava/lang/Double;");
    }

    public static void boxChar(Bytecode bc) {
        bc.addInvokestatic("java.lang.Character", "valueOf", "(C)Ljava/lang/Character;");
    }

    public static void boxBoolean(Bytecode bc) {
        bc.addInvokestatic("java.lang.Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
    }

    // unboxing

    public static Bytecode unboxInt(Bytecode bc) {
        bc.addCheckcast("java.lang.Number");
        bc.addInvokevirtual("java.lang.Number", "intValue", "()I");
        return bc;
    }

    public static Bytecode unboxLong(Bytecode bc) {
        bc.addCheckcast("java.lang.Number");
        bc.addInvokevirtual("java.lang.Number", "longValue", "()J");
        return bc;
    }

    public static Bytecode unboxShort(Bytecode bc) {
        bc.addCheckcast("java.lang.Number");
        bc.addInvokevirtual("java.lang.Number", "shortValue", "()S");
        return bc;
    }

    public static Bytecode unboxByte(Bytecode bc) {
        bc.addCheckcast("java.lang.Number");
        bc.addInvokevirtual("java.lang.Number", "byteValue", "()B");
        return bc;
    }

    public static Bytecode unboxFloat(Bytecode bc) {
        bc.addCheckcast("java.lang.Number");
        bc.addInvokevirtual("java.lang.Number", "floatValue", "()F");
        return bc;
    }

    public static Bytecode unboxDouble(Bytecode bc) {
        bc.addCheckcast("java.lang.Number");
        bc.addInvokevirtual("java.lang.Number", "doubleValue", "()D");
        return bc;
    }

    public static Bytecode unboxChar(Bytecode bc) {
        bc.addCheckcast("java.lang.Character");
        bc.addInvokevirtual("java.lang.Character", "charValue", "()C");
        return bc;
    }

    public static Bytecode unboxBoolean(Bytecode bc) {
        bc.addCheckcast("java.lang.Boolean");
        bc.addInvokevirtual("java.lang.Boolean", "booleanValue", "()Z");
        return bc;
    }

}
