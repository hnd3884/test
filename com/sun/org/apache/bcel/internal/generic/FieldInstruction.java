package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;

public abstract class FieldInstruction extends FieldOrMethod implements TypedInstruction
{
    FieldInstruction() {
    }
    
    protected FieldInstruction(final short opcode, final int index) {
        super(opcode, index);
    }
    
    @Override
    public String toString(final ConstantPool cp) {
        return Constants.OPCODE_NAMES[this.opcode] + " " + cp.constantToString(this.index, (byte)9);
    }
    
    protected int getFieldSize(final ConstantPoolGen cpg) {
        return this.getType(cpg).getSize();
    }
    
    @Override
    public Type getType(final ConstantPoolGen cpg) {
        return this.getFieldType(cpg);
    }
    
    public Type getFieldType(final ConstantPoolGen cpg) {
        return Type.getType(this.getSignature(cpg));
    }
    
    public String getFieldName(final ConstantPoolGen cpg) {
        return this.getName(cpg);
    }
}
