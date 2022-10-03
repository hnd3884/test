package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import com.sun.org.apache.bcel.internal.Constants;
import java.io.IOException;
import java.io.DataOutputStream;

public class NEWARRAY extends Instruction implements AllocationInstruction, ExceptionThrower, StackProducer
{
    private byte type;
    
    NEWARRAY() {
    }
    
    public NEWARRAY(final byte type) {
        super((short)188, (short)2);
        this.type = type;
    }
    
    public NEWARRAY(final BasicType type) {
        this(type.getType());
    }
    
    @Override
    public void dump(final DataOutputStream out) throws IOException {
        out.writeByte(this.opcode);
        out.writeByte(this.type);
    }
    
    public final byte getTypecode() {
        return this.type;
    }
    
    public final Type getType() {
        return new ArrayType(BasicType.getType(this.type), 1);
    }
    
    @Override
    public String toString(final boolean verbose) {
        return super.toString(verbose) + " " + Constants.TYPE_NAMES[this.type];
    }
    
    @Override
    protected void initFromFile(final ByteSequence bytes, final boolean wide) throws IOException {
        this.type = bytes.readByte();
        this.length = 2;
    }
    
    @Override
    public Class[] getExceptions() {
        return new Class[] { ExceptionConstants.NEGATIVE_ARRAY_SIZE_EXCEPTION };
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitAllocationInstruction(this);
        v.visitExceptionThrower(this);
        v.visitStackProducer(this);
        v.visitNEWARRAY(this);
    }
}
