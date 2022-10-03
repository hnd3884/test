package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;
import java.io.DataOutputStream;

public abstract class BranchInstruction extends Instruction implements InstructionTargeter
{
    protected int index;
    protected InstructionHandle target;
    protected int position;
    
    BranchInstruction() {
    }
    
    protected BranchInstruction(final short opcode, final InstructionHandle target) {
        super(opcode, (short)3);
        this.setTarget(target);
    }
    
    @Override
    public void dump(final DataOutputStream out) throws IOException {
        out.writeByte(this.opcode);
        this.index = this.getTargetOffset();
        if (Math.abs(this.index) >= 32767) {
            throw new ClassGenException("Branch target offset too large for short");
        }
        out.writeShort(this.index);
    }
    
    protected int getTargetOffset(final InstructionHandle target) {
        if (target == null) {
            throw new ClassGenException("Target of " + super.toString(true) + " is invalid null handle");
        }
        final int t = target.getPosition();
        if (t < 0) {
            throw new ClassGenException("Invalid branch target position offset for " + super.toString(true) + ":" + t + ":" + target);
        }
        return t - this.position;
    }
    
    protected int getTargetOffset() {
        return this.getTargetOffset(this.target);
    }
    
    protected int updatePosition(final int offset, final int max_offset) {
        this.position += offset;
        return 0;
    }
    
    @Override
    public String toString(final boolean verbose) {
        final String s = super.toString(verbose);
        String t = "null";
        if (verbose) {
            if (this.target != null) {
                if (this.target.getInstruction() == this) {
                    t = "<points to itself>";
                }
                else if (this.target.getInstruction() == null) {
                    t = "<null instruction!!!?>";
                }
                else {
                    t = this.target.getInstruction().toString(false);
                }
            }
        }
        else if (this.target != null) {
            this.index = this.getTargetOffset();
            t = "" + (this.index + this.position);
        }
        return s + " -> " + t;
    }
    
    @Override
    protected void initFromFile(final ByteSequence bytes, final boolean wide) throws IOException {
        this.length = 3;
        this.index = bytes.readShort();
    }
    
    public final int getIndex() {
        return this.index;
    }
    
    public InstructionHandle getTarget() {
        return this.target;
    }
    
    public final void setTarget(final InstructionHandle target) {
        notifyTargetChanging(this.target, this);
        notifyTargetChanged(this.target = target, this);
    }
    
    static void notifyTargetChanging(final InstructionHandle old_ih, final InstructionTargeter t) {
        if (old_ih != null) {
            old_ih.removeTargeter(t);
        }
    }
    
    static void notifyTargetChanged(final InstructionHandle new_ih, final InstructionTargeter t) {
        if (new_ih != null) {
            new_ih.addTargeter(t);
        }
    }
    
    @Override
    public void updateTarget(final InstructionHandle old_ih, final InstructionHandle new_ih) {
        if (this.target == old_ih) {
            this.setTarget(new_ih);
            return;
        }
        throw new ClassGenException("Not targeting " + old_ih + ", but " + this.target);
    }
    
    @Override
    public boolean containsTarget(final InstructionHandle ih) {
        return this.target == ih;
    }
    
    @Override
    void dispose() {
        this.setTarget(null);
        this.index = -1;
        this.position = -1;
    }
}
