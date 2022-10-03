package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.ConstantClass;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import java.io.IOException;
import java.io.DataOutputStream;

public abstract class CPInstruction extends Instruction implements TypedInstruction, IndexedInstruction
{
    protected int index;
    
    CPInstruction() {
    }
    
    protected CPInstruction(final short opcode, final int index) {
        super(opcode, (short)3);
        this.setIndex(index);
    }
    
    @Override
    public void dump(final DataOutputStream out) throws IOException {
        out.writeByte(this.opcode);
        out.writeShort(this.index);
    }
    
    @Override
    public String toString(final boolean verbose) {
        return super.toString(verbose) + " " + this.index;
    }
    
    @Override
    public String toString(final ConstantPool cp) {
        final Constant c = cp.getConstant(this.index);
        String str = cp.constantToString(c);
        if (c instanceof ConstantClass) {
            str = str.replace('.', '/');
        }
        return Constants.OPCODE_NAMES[this.opcode] + " " + str;
    }
    
    @Override
    protected void initFromFile(final ByteSequence bytes, final boolean wide) throws IOException {
        this.setIndex(bytes.readUnsignedShort());
        this.length = 3;
    }
    
    @Override
    public final int getIndex() {
        return this.index;
    }
    
    @Override
    public void setIndex(final int index) {
        if (index < 0) {
            throw new ClassGenException("Negative index value: " + index);
        }
        this.index = index;
    }
    
    @Override
    public Type getType(final ConstantPoolGen cpg) {
        final ConstantPool cp = cpg.getConstantPool();
        String name = cp.getConstantString(this.index, (byte)7);
        if (!name.startsWith("[")) {
            name = "L" + name + ";";
        }
        return Type.getType(name);
    }
}
