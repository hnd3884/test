package com.sun.org.apache.bcel.internal.generic;

public class IFLE extends IfInstruction
{
    IFLE() {
    }
    
    public IFLE(final InstructionHandle target) {
        super((short)158, target);
    }
    
    @Override
    public IfInstruction negate() {
        return new IFGT(this.target);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFLE(this);
    }
}
