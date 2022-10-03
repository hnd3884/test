package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;
import java.io.DataOutputStream;

public class LOOKUPSWITCH extends Select
{
    LOOKUPSWITCH() {
    }
    
    public LOOKUPSWITCH(final int[] match, final InstructionHandle[] targets, final InstructionHandle target) {
        super((short)171, match, targets, target);
        this.length = (short)(9 + this.match_length * 8);
        this.fixed_length = this.length;
    }
    
    @Override
    public void dump(final DataOutputStream out) throws IOException {
        super.dump(out);
        out.writeInt(this.match_length);
        for (int i = 0; i < this.match_length; ++i) {
            out.writeInt(this.match[i]);
            out.writeInt(this.indices[i] = this.getTargetOffset(this.targets[i]));
        }
    }
    
    @Override
    protected void initFromFile(final ByteSequence bytes, final boolean wide) throws IOException {
        super.initFromFile(bytes, wide);
        this.match_length = bytes.readInt();
        this.fixed_length = (short)(9 + this.match_length * 8);
        this.length = (short)(this.fixed_length + this.padding);
        this.match = new int[this.match_length];
        this.indices = new int[this.match_length];
        this.targets = new InstructionHandle[this.match_length];
        for (int i = 0; i < this.match_length; ++i) {
            this.match[i] = bytes.readInt();
            this.indices[i] = bytes.readInt();
        }
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitVariableLengthInstruction(this);
        v.visitStackProducer(this);
        v.visitBranchInstruction(this);
        v.visitSelect(this);
        v.visitLOOKUPSWITCH(this);
    }
}
