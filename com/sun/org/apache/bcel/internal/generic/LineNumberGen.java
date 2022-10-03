package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.LineNumber;
import java.io.Serializable;

public class LineNumberGen implements InstructionTargeter, Cloneable, Serializable
{
    private InstructionHandle ih;
    private int src_line;
    
    public LineNumberGen(final InstructionHandle ih, final int src_line) {
        this.setInstruction(ih);
        this.setSourceLine(src_line);
    }
    
    @Override
    public boolean containsTarget(final InstructionHandle ih) {
        return this.ih == ih;
    }
    
    @Override
    public void updateTarget(final InstructionHandle old_ih, final InstructionHandle new_ih) {
        if (old_ih != this.ih) {
            throw new ClassGenException("Not targeting " + old_ih + ", but " + this.ih + "}");
        }
        this.setInstruction(new_ih);
    }
    
    public LineNumber getLineNumber() {
        return new LineNumber(this.ih.getPosition(), this.src_line);
    }
    
    public final void setInstruction(final InstructionHandle ih) {
        BranchInstruction.notifyTargetChanging(this.ih, this);
        BranchInstruction.notifyTargetChanged(this.ih = ih, this);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException e) {
            System.err.println(e);
            return null;
        }
    }
    
    public InstructionHandle getInstruction() {
        return this.ih;
    }
    
    public void setSourceLine(final int src_line) {
        this.src_line = src_line;
    }
    
    public int getSourceLine() {
        return this.src_line;
    }
}
