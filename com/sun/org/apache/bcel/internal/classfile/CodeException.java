package com.sun.org.apache.bcel.internal.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.Serializable;
import com.sun.org.apache.bcel.internal.Constants;

public final class CodeException implements Cloneable, Constants, Node, Serializable
{
    private int start_pc;
    private int end_pc;
    private int handler_pc;
    private int catch_type;
    
    public CodeException(final CodeException c) {
        this(c.getStartPC(), c.getEndPC(), c.getHandlerPC(), c.getCatchType());
    }
    
    CodeException(final DataInputStream file) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort());
    }
    
    public CodeException(final int start_pc, final int end_pc, final int handler_pc, final int catch_type) {
        this.start_pc = start_pc;
        this.end_pc = end_pc;
        this.handler_pc = handler_pc;
        this.catch_type = catch_type;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitCodeException(this);
    }
    
    public final void dump(final DataOutputStream file) throws IOException {
        file.writeShort(this.start_pc);
        file.writeShort(this.end_pc);
        file.writeShort(this.handler_pc);
        file.writeShort(this.catch_type);
    }
    
    public final int getCatchType() {
        return this.catch_type;
    }
    
    public final int getEndPC() {
        return this.end_pc;
    }
    
    public final int getHandlerPC() {
        return this.handler_pc;
    }
    
    public final int getStartPC() {
        return this.start_pc;
    }
    
    public final void setCatchType(final int catch_type) {
        this.catch_type = catch_type;
    }
    
    public final void setEndPC(final int end_pc) {
        this.end_pc = end_pc;
    }
    
    public final void setHandlerPC(final int handler_pc) {
        this.handler_pc = handler_pc;
    }
    
    public final void setStartPC(final int start_pc) {
        this.start_pc = start_pc;
    }
    
    @Override
    public final String toString() {
        return "CodeException(start_pc = " + this.start_pc + ", end_pc = " + this.end_pc + ", handler_pc = " + this.handler_pc + ", catch_type = " + this.catch_type + ")";
    }
    
    public final String toString(final ConstantPool cp, final boolean verbose) {
        String str;
        if (this.catch_type == 0) {
            str = "<Any exception>(0)";
        }
        else {
            str = Utility.compactClassName(cp.getConstantString(this.catch_type, (byte)7), false) + (verbose ? ("(" + this.catch_type + ")") : "");
        }
        return this.start_pc + "\t" + this.end_pc + "\t" + this.handler_pc + "\t" + str;
    }
    
    public final String toString(final ConstantPool cp) {
        return this.toString(cp, true);
    }
    
    public CodeException copy() {
        try {
            return (CodeException)this.clone();
        }
        catch (final CloneNotSupportedException ex) {
            return null;
        }
    }
}
