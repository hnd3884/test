package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;
import java.io.DataOutputStream;

public abstract class Select extends BranchInstruction implements VariableLengthInstruction, StackProducer
{
    protected int[] match;
    protected int[] indices;
    protected InstructionHandle[] targets;
    protected int fixed_length;
    protected int match_length;
    protected int padding;
    
    Select() {
        this.padding = 0;
    }
    
    Select(final short opcode, final int[] match, final InstructionHandle[] targets, final InstructionHandle target) {
        super(opcode, null);
        this.padding = 0;
        this.match = match;
        this.targets = targets;
        this.setTarget(target);
        for (int i = 0; i < targets.length; ++i) {
            BranchInstruction.notifyTargetChanged(targets[i], this);
        }
        if ((this.match_length = match.length) != targets.length) {
            throw new ClassGenException("Match and target array have not the same length");
        }
        this.indices = new int[this.match_length];
    }
    
    @Override
    protected int updatePosition(final int offset, final int max_offset) {
        this.position += offset;
        final short old_length = this.length;
        this.padding = (4 - (this.position + 1) % 4) % 4;
        this.length = (short)(this.fixed_length + this.padding);
        return this.length - old_length;
    }
    
    @Override
    public void dump(final DataOutputStream out) throws IOException {
        out.writeByte(this.opcode);
        for (int i = 0; i < this.padding; ++i) {
            out.writeByte(0);
        }
        out.writeInt(this.index = this.getTargetOffset());
    }
    
    @Override
    protected void initFromFile(final ByteSequence bytes, final boolean wide) throws IOException {
        this.padding = (4 - bytes.getIndex() % 4) % 4;
        for (int i = 0; i < this.padding; ++i) {
            bytes.readByte();
        }
        this.index = bytes.readInt();
    }
    
    @Override
    public String toString(final boolean verbose) {
        final StringBuilder buf = new StringBuilder(super.toString(verbose));
        if (verbose) {
            for (int i = 0; i < this.match_length; ++i) {
                String s = "null";
                if (this.targets[i] != null) {
                    s = this.targets[i].getInstruction().toString();
                }
                buf.append("(").append(this.match[i]).append(", ").append(s).append(" = {").append(this.indices[i]).append("})");
            }
        }
        else {
            buf.append(" ...");
        }
        return buf.toString();
    }
    
    public final void setTarget(final int i, final InstructionHandle target) {
        BranchInstruction.notifyTargetChanging(this.targets[i], this);
        BranchInstruction.notifyTargetChanged(this.targets[i] = target, this);
    }
    
    @Override
    public void updateTarget(final InstructionHandle old_ih, final InstructionHandle new_ih) {
        boolean targeted = false;
        if (this.target == old_ih) {
            targeted = true;
            this.setTarget(new_ih);
        }
        for (int i = 0; i < this.targets.length; ++i) {
            if (this.targets[i] == old_ih) {
                targeted = true;
                this.setTarget(i, new_ih);
            }
        }
        if (!targeted) {
            throw new ClassGenException("Not targeting " + old_ih);
        }
    }
    
    @Override
    public boolean containsTarget(final InstructionHandle ih) {
        if (this.target == ih) {
            return true;
        }
        for (int i = 0; i < this.targets.length; ++i) {
            if (this.targets[i] == ih) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    void dispose() {
        super.dispose();
        for (int i = 0; i < this.targets.length; ++i) {
            this.targets[i].removeTargeter(this);
        }
    }
    
    public int[] getMatchs() {
        return this.match;
    }
    
    public int[] getIndices() {
        return this.indices;
    }
    
    public InstructionHandle[] getTargets() {
        if (this.targets == null) {
            return new InstructionHandle[0];
        }
        return this.targets;
    }
}
