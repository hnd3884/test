package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;
import java.io.DataOutputStream;

public class RET extends Instruction implements IndexedInstruction, TypedInstruction
{
    private boolean wide;
    private int index;
    
    RET() {
    }
    
    public RET(final int index) {
        super((short)169, (short)2);
        this.setIndex(index);
    }
    
    @Override
    public void dump(final DataOutputStream out) throws IOException {
        if (this.wide) {
            out.writeByte(196);
        }
        out.writeByte(this.opcode);
        if (this.wide) {
            out.writeShort(this.index);
        }
        else {
            out.writeByte(this.index);
        }
    }
    
    private final void setWide() {
        final boolean wide = this.index > 255;
        this.wide = wide;
        if (wide) {
            this.length = 4;
        }
        else {
            this.length = 2;
        }
    }
    
    @Override
    protected void initFromFile(final ByteSequence bytes, final boolean wide) throws IOException {
        this.wide = wide;
        if (wide) {
            this.index = bytes.readUnsignedShort();
            this.length = 4;
        }
        else {
            this.index = bytes.readUnsignedByte();
            this.length = 2;
        }
    }
    
    @Override
    public final int getIndex() {
        return this.index;
    }
    
    @Override
    public final void setIndex(final int n) {
        if (n < 0) {
            throw new ClassGenException("Negative index value: " + n);
        }
        this.index = n;
        this.setWide();
    }
    
    @Override
    public String toString(final boolean verbose) {
        return super.toString(verbose) + " " + this.index;
    }
    
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return ReturnaddressType.NO_TARGET;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitRET(this);
    }
}
