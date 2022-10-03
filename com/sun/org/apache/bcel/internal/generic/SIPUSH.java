package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;
import java.io.DataOutputStream;

public class SIPUSH extends Instruction implements ConstantPushInstruction
{
    private short b;
    
    SIPUSH() {
    }
    
    public SIPUSH(final short b) {
        super((short)17, (short)3);
        this.b = b;
    }
    
    @Override
    public void dump(final DataOutputStream out) throws IOException {
        super.dump(out);
        out.writeShort(this.b);
    }
    
    @Override
    public String toString(final boolean verbose) {
        return super.toString(verbose) + " " + this.b;
    }
    
    @Override
    protected void initFromFile(final ByteSequence bytes, final boolean wide) throws IOException {
        this.length = 3;
        this.b = bytes.readShort();
    }
    
    @Override
    public Number getValue() {
        return new Integer(this.b);
    }
    
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return Type.SHORT;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitPushInstruction(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitConstantPushInstruction(this);
        v.visitSIPUSH(this);
    }
}
