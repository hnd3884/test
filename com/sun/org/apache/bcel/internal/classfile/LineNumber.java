package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.Serializable;

public final class LineNumber implements Cloneable, Node, Serializable
{
    private int start_pc;
    private int line_number;
    
    public LineNumber(final LineNumber c) {
        this(c.getStartPC(), c.getLineNumber());
    }
    
    LineNumber(final DataInputStream file) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort());
    }
    
    public LineNumber(final int start_pc, final int line_number) {
        this.start_pc = start_pc;
        this.line_number = line_number;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitLineNumber(this);
    }
    
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeShort(this.start_pc);
        file.writeShort(this.line_number);
    }
    
    public final int getLineNumber() {
        return this.line_number;
    }
    
    public final int getStartPC() {
        return this.start_pc;
    }
    
    public final void setLineNumber(final int line_number) {
        this.line_number = line_number;
    }
    
    public final void setStartPC(final int start_pc) {
        this.start_pc = start_pc;
    }
    
    @Override
    public final String toString() {
        return "LineNumber(" + this.start_pc + ", " + this.line_number + ")";
    }
    
    public LineNumber copy() {
        try {
            return (LineNumber)this.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
}
