package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.Instruction;

class OutlineableChunkEnd extends MarkerInstruction
{
    public static final Instruction OUTLINEABLECHUNKEND;
    
    private OutlineableChunkEnd() {
    }
    
    @Override
    public String getName() {
        return OutlineableChunkEnd.class.getName();
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    @Override
    public String toString(final boolean verbose) {
        return this.getName();
    }
    
    static {
        OUTLINEABLECHUNKEND = new OutlineableChunkEnd();
    }
}
