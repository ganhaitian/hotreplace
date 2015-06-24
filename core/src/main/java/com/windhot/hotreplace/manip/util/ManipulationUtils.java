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

import com.windhot.hotreplace.util.DescriptorUtils;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;

/**
 * Class that holds various static helper methods that manipulate the bytecode
 *
 * @author Gan
 */
public class ManipulationUtils {

    private ManipulationUtils() {
    }

    /**
     * This class changes a block of code to return either a boxed version of a
     * Primitive type or null if the method is void
     *
     * @author Gan
     */
    public static class MethodReturnRewriter {

        public static void rewriteFakeMethod(CodeIterator methodBody, String methodDescriptor) {
            String ret = DescriptorUtils.getReturnType(methodDescriptor);
            // if the return type is larger than one then it is not a primitive
            // so it does not need to be boxed
            if (ret.length() != 1) {
                return;
            }
            // void methods are special
            if (ret.equals("V")) {

                while (methodBody.hasNext()) {
                    try {
                        int index = methodBody.next();
                        int opcode = methodBody.byteAt(index);
                        // replace a RETURN opcode with
                        // ACONST_NULL
                        // ARETURN
                        // to return a null value
                        if (opcode == Opcode.RETURN) {
                            Bytecode code = new Bytecode(methodBody.get().getConstPool());
                            methodBody.writeByte(Opcode.ARETURN, index);
                            code.add(Opcode.ACONST_NULL);
                            methodBody.insertAt(index, code.get());

                        }
                    } catch (BadBytecode e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                while (methodBody.hasNext()) {
                    try {
                        int index = methodBody.next();
                        int opcode = methodBody.byteAt(index);

                        switch (opcode) {
                            case Opcode.IRETURN:
                            case Opcode.LRETURN:
                            case Opcode.DRETURN:
                            case Opcode.FRETURN:
                                // write a NOP over the old return instruction
                                // insert the boxing code to get an object on the stack
                                methodBody.writeByte(Opcode.ARETURN, index);
                                Bytecode b = new Bytecode(methodBody.get().getConstPool());
                                Boxing.box(b, ret.charAt(0));
                                methodBody.insertAt(index, b.get());

                        }
                    } catch (BadBytecode e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        /**
         * Gets the correct return instruction for a proxy method
         *
         * @param methodDescriptor
         */
        public static void addReturnProxyMethod(String methodDescriptor, Bytecode b) {
            String ret = DescriptorUtils.getReturnType(methodDescriptor);
            // if the return type is larger than one then it is not a primitive
            // so just do an ARETURN
            if (ret.length() != 1) {
                b.addCheckcast(DescriptorUtils.getReturnTypeInJvmFormat(methodDescriptor));
                b.add(Opcode.ARETURN);
                return;
            }
            // void methods are special
            if (ret.equals("V")) {
                b.add(Opcode.RETURN);
                return;
            } else {
                // unbox the primitive type

                char tp = ret.charAt(0);
                Boxing.unbox(b, tp);
                if (tp == 'F') {
                    b.add(Opcode.FRETURN);
                } else if (tp == 'D') {
                    b.add(Opcode.DRETURN);
                } else if (tp == 'J') {
                    b.add(Opcode.LRETURN);
                } else {
                    b.add(Opcode.IRETURN);
                }
                return;
            }
        }
    }

    /**
     * add a bogus constructor call to a bytecode sequence so a constructor can
     * pass bytecode validation
     *
     */
    public static boolean addBogusConstructorCall(ClassFile file, Bytecode code) {
        MethodInfo constructorToCall = null;
        for (Object meth : file.getMethods()) {
            MethodInfo m = (MethodInfo) meth;
            if (m.getName().equals("<init>")) {
                constructorToCall = m;
                break;
            }
        }
        if (constructorToCall == null) {
            return false;
        }
        // push this onto the stack
        code.add(Bytecode.ALOAD_0);

        String[] params = DescriptorUtils.descriptorStringToParameterArray(constructorToCall.getDescriptor());
        for (String p : params) {
            // int char short boolean byte
            if (p.equals("I") || p.equals("C") || p.equals("S") || p.equals("Z") || p.equals("B")) {
                // push integer 0
                code.add(Opcode.ICONST_0);
            }
            // long
            else if (p.equals("J")) {
                code.add(Opcode.LCONST_0);
            }
            // double
            else if (p.equals("D")) {
                code.add(Opcode.DCONST_0);
            }
            // float
            else if (p.equals("F")) {
                code.add(Opcode.FCONST_0);
            }
            // arrays and reference types
            else {
                code.add(Opcode.ACONST_NULL);
            }
        }
        // all our args should be pushed onto the stack, call the constructor
        code.addInvokespecial(file.getName(), "<init>", constructorToCall.getDescriptor());
        code.add(Opcode.RETURN);
        return true;
    }

    /**
     * inserts a 16 bit offset into the bytecode
     *
     * @param b
     * @param value
     */
    public static void add16bit(Bytecode b, int value) {
        value = value % 65536;
        b.add(value >> 8);
        b.add(value % 256);
    }

    public static void pushParametersIntoArray(Bytecode bc, String methodDescriptor) {
        String[] params = DescriptorUtils.descriptorStringToParameterArray(methodDescriptor);
        // now we need an array:
        bc.addIconst(params.length);
        bc.addAnewarray("java.lang.Object");
        // now we have our array sitting on top of the stack
        // we need to stick our parameters into it. We do this is reverse
        // as we can't pull them from the bottom of the stack
        for (int i = params.length - 1; i >= 0; --i) {

            if (DescriptorUtils.isWide(params[i])) {
                // dup the array below the wide
                bc.add(Opcode.DUP_X2);
                // now do it again so we have two copies
                bc.add(Opcode.DUP_X2);
                // now pop it, the is the equivalent of a wide swap
                bc.add(Opcode.POP);
            } else {
                // duplicate the array to place 3
                bc.add(Opcode.DUP_X1);
                // now swap
                bc.add(Opcode.SWAP);
            }
            // now the parameter is above the array
            // box it if nessesary
            if (DescriptorUtils.isPrimitive(params[i])) {
                Boxing.box(bc, params[i].charAt(0));
            }
            // add the array index
            bc.addIconst(i);
            bc.add(Opcode.SWAP);
            bc.add(Opcode.AASTORE);
            // we still have the array on the top of the stack becuase we
            // duplicated it earlier
        }
    }
}
