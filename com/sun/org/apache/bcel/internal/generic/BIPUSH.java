package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;
import java.io.DataOutputStream;

public class BIPUSH extends Instruction implements ConstantPushInstruction
{
    private byte b;
    
    BIPUSH() {
    }
    
    public BIPUSH(final byte b) {
        super((short)16, (short)2);
        this.b = b;
    }
    
    @Override
    public void dump(final DataOutputStream out) throws IOException {
        super.dump(out);
        out.writeByte(this.b);
    }
    
    @Override
    public String toString(final boolean verbose) {
        return super.toString(verbose) + " " + this.b;
    }
    
    @Override
    protected void initFromFile(final ByteSequence bytes, final boolean wide) throws IOException {
        this.length = 2;
        this.b = bytes.readByte();
    }
    
    @Override
    public Number getValue() {
        return new Integer(this.b);
    }
    
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return Type.BYTE;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitPushInstruction(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitConstantPushInstruction(this);
        v.visitBIPUSH(this);
    }
}
