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

package com.windhot.hotreplace.manip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.windhot.hotreplace.logging.Logger;
import com.windhot.hotreplace.manip.data.AddedFieldData;
import com.windhot.hotreplace.runtime.FieldDataStore;
import com.windhot.hotreplace.util.DescriptorUtils;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import com.windhot.hotreplace.manip.util.Boxing;
import com.windhot.hotreplace.manip.util.ManipulationDataStore;

public class InstanceFieldManipulator implements ClassManipulator {

    private static final String FIELD_DATA_STORE_CLASS = FieldDataStore.class.getName();

    private static final Logger log = Logger.getLogger(InstanceFieldManipulator.class);

    /**
     * added field information by class
     */
    private final ManipulationDataStore<AddedFieldData> data = new ManipulationDataStore<AddedFieldData>();

    public void addField(AddedFieldData dt) {
        data.add(dt.getClassName(), dt);
    }

    public boolean transformClass(ClassFile file, ClassLoader loader, boolean modifiableClass, final Set<MethodInfo> modifiedMethods) {

        Map<String, Set<AddedFieldData>> addedFieldData = data.getManipulationData(loader);
        if (addedFieldData.isEmpty()) {
            return false;
        }
        Map<Integer, AddedFieldData> fieldAccessLocations = new HashMap<Integer, AddedFieldData>();
        // first we need to scan the constant pool looking for
        // CONST_Fieldref structures
        ConstPool pool = file.getConstPool();
        for (int i = 1; i < pool.getSize(); ++i) {
            // we have a field reference
            if (pool.getTag(i) == ConstPool.CONST_Fieldref) {
                if (addedFieldData.containsKey(pool.getFieldrefClassName(i))) {
                    for (AddedFieldData data : addedFieldData.get(pool.getFieldrefClassName(i))) {
                        if (pool.getFieldrefName(i).equals(data.getName())) {
                            // store the location in the const pool of the method ref
                            fieldAccessLocations.put(i, data);
                            break;
                        }

                    }
                }
            }
        }

        // this means we found an instance of the call, now we have to iterate
        // through the methods and replace instances of the call
        if (!fieldAccessLocations.isEmpty()) {
            List<MethodInfo> methods = file.getMethods();
            for (MethodInfo m : methods) {
                try {
                    // ignore abstract methods
                    if (m.getCodeAttribute() == null) {
                        continue;
                    }
                    CodeIterator it = m.getCodeAttribute().iterator();
                    while (it.hasNext()) {
                        // loop through the bytecode
                        int index = it.next();
                        int op = it.byteAt(index);
                        // if the bytecode is a field access
                        if (op == Opcode.PUTFIELD || op == Opcode.GETFIELD) {
                            int val = it.s16bitAt(index + 1);
                            // if the field access is for an added field
                            if (fieldAccessLocations.containsKey(val)) {
                                AddedFieldData data = fieldAccessLocations.get(val);
                                int arrayPos = file.getConstPool().addIntegerInfo(data.getArrayIndex());
                                // write over the field access with nop
                                it.writeByte(Opcode.NOP, index);
                                it.writeByte(Opcode.NOP, index + 1);
                                it.writeByte(Opcode.NOP, index + 2);

                                if (op == Opcode.PUTFIELD) {
                                    Bytecode b = new Bytecode(file.getConstPool());
                                    if (data.getDescriptor().charAt(0) != 'L' && data.getDescriptor().charAt(0) != '[') {
                                        Boxing.box(b, data.getDescriptor().charAt(0));
                                    }
                                    b.addLdc(arrayPos);
                                    b.addInvokestatic(FIELD_DATA_STORE_CLASS, "setValue", "(Ljava/lang/Object;Ljava/lang/Object;I)V");
                                    it.insertEx(b.get());
                                } else if (op == Opcode.GETFIELD) {
                                    Bytecode b = new Bytecode(file.getConstPool());
                                    b.addLdc(arrayPos);
                                    b.addInvokestatic(FIELD_DATA_STORE_CLASS, "getValue", "(Ljava/lang/Object;I)Ljava/lang/Object;");

                                    if (DescriptorUtils.isPrimitive(data.getDescriptor())) {
                                        Boxing.unbox(b, data.getDescriptor().charAt(0));
                                    } else {
                                        b.addCheckcast(DescriptorUtils.getTypeStringFromDescriptorFormat(data.getDescriptor()));
                                    }
                                    it.insertEx(b.get());
                                }

                            }
                        }
                    }
                    modifiedMethods.add(m);
                    m.getCodeAttribute().computeMaxStack();
                } catch (Exception e) {
                    log.error("Bad byte code transforming " + file.getName(), e);
                    e.printStackTrace();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void clearRewrites(String className, ClassLoader loader) {
        data.remove(className, loader);
    }

}
