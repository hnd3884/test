package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.Instruction;

class OutlineableChunkStart extends MarkerInstruction
{
    public static final Instruction OUTLINEABLECHUNKSTART;
    
    private OutlineableChunkStart() {
    }
    
    @Override
    public String getName() {
        return OutlineableChunkStart.class.getName();
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
        OUTLINEABLECHUNKSTART = new OutlineableChunkStart();
    }
}
