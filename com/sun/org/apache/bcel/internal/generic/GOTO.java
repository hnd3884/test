package com.sun.org.apache.bcel.internal.generic;

import java.io.IOException;
import java.io.DataOutputStream;

public class GOTO extends GotoInstruction implements VariableLengthInstruction
{
    GOTO() {
    }
    
    public GOTO(final InstructionHandle target) {
        super((short)167, target);
    }
    
    @Override
    public void dump(final DataOutputStream out) throws IOException {
        this.index = this.getTargetOffset();
        if (this.opcode == 167) {
            super.dump(out);
        }
        else {
            this.index = this.getTargetOffset();
            out.writeByte(this.opcode);
            out.writeInt(this.index);
        }
    }
    
    @Override
    protected int updatePosition(final int offset, final int max_offset) {
        final int i = this.getTargetOffset();
        this.position += offset;
        if (Math.abs(i) >= 32767 - max_offset) {
            this.opcode = 200;
            this.length = 5;
            return 2;
        }
        return 0;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitVariableLengthInstruction(this);
        v.visitUnconditionalBranch(this);
        v.visitBranchInstruction(this);
        v.visitGotoInstruction(this);
        v.visitGOTO(this);
    }
}
