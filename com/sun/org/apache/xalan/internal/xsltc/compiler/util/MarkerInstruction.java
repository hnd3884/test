package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.io.IOException;
import java.io.DataOutputStream;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Visitor;
import com.sun.org.apache.bcel.internal.generic.Instruction;

abstract class MarkerInstruction extends Instruction
{
    public MarkerInstruction() {
        super((short)(-1), (short)0);
    }
    
    @Override
    public void accept(final Visitor v) {
    }
    
    @Override
    public final int consumeStack(final ConstantPoolGen cpg) {
        return 0;
    }
    
    @Override
    public final int produceStack(final ConstantPoolGen cpg) {
        return 0;
    }
    
    @Override
    public Instruction copy() {
        return this;
    }
    
    @Override
    public final void dump(final DataOutputStream out) throws IOException {
    }
}
