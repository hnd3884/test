package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;
import java.io.DataOutputStream;

public class IINC extends LocalVariableInstruction
{
    private boolean wide;
    private int c;
    
    IINC() {
    }
    
    public IINC(final int n, final int c) {
        this.opcode = 132;
        this.length = 3;
        this.setIndex(n);
        this.setIncrement(c);
    }
    
    @Override
    public void dump(final DataOutputStream out) throws IOException {
        if (this.wide) {
            out.writeByte(196);
        }
        out.writeByte(this.opcode);
        if (this.wide) {
            out.writeShort(this.n);
            out.writeShort(this.c);
        }
        else {
            out.writeByte(this.n);
            out.writeByte(this.c);
        }
    }
    
    private final void setWide() {
        final boolean wide = this.n > 65535 || Math.abs(this.c) > 127;
        this.wide = wide;
        if (wide) {
            this.length = 6;
        }
        else {
            this.length = 3;
        }
    }
    
    @Override
    protected void initFromFile(final ByteSequence bytes, final boolean wide) throws IOException {
        this.wide = wide;
        if (wide) {
            this.length = 6;
            this.n = bytes.readUnsignedShort();
            this.c = bytes.readShort();
        }
        else {
            this.length = 3;
            this.n = bytes.readUnsignedByte();
            this.c = bytes.readByte();
        }
    }
    
    @Override
    public String toString(final boolean verbose) {
        return super.toString(verbose) + " " + this.c;
    }
    
    @Override
    public final void setIndex(final int n) {
        if (n < 0) {
            throw new ClassGenException("Negative index value: " + n);
        }
        this.n = n;
        this.setWide();
    }
    
    public final int getIncrement() {
        return this.c;
    }
    
    public final void setIncrement(final int c) {
        this.c = c;
        this.setWide();
    }
    
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return Type.INT;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitLocalVariableInstruction(this);
        v.visitIINC(this);
    }
}
