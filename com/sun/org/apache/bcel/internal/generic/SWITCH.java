package com.sun.org.apache.bcel.internal.generic;

public final class SWITCH implements CompoundInstruction
{
    private int[] match;
    private InstructionHandle[] targets;
    private Select instruction;
    private int match_length;
    
    public SWITCH(final int[] match, final InstructionHandle[] targets, final InstructionHandle target, final int max_gap) {
        this.match = match.clone();
        this.targets = targets.clone();
        final int length = match.length;
        this.match_length = length;
        if (length < 2) {
            this.instruction = new TABLESWITCH(match, targets, target);
        }
        else {
            this.sort(0, this.match_length - 1);
            if (this.matchIsOrdered(max_gap)) {
                this.fillup(max_gap, target);
                this.instruction = new TABLESWITCH(this.match, this.targets, target);
            }
            else {
                this.instruction = new LOOKUPSWITCH(this.match, this.targets, target);
            }
        }
    }
    
    public SWITCH(final int[] match, final InstructionHandle[] targets, final InstructionHandle target) {
        this(match, targets, target, 1);
    }
    
    private final void fillup(final int max_gap, final InstructionHandle target) {
        final int max_size = this.match_length + this.match_length * max_gap;
        final int[] m_vec = new int[max_size];
        final InstructionHandle[] t_vec = new InstructionHandle[max_size];
        int count = 1;
        m_vec[0] = this.match[0];
        t_vec[0] = this.targets[0];
        for (int i = 1; i < this.match_length; ++i) {
            final int prev = this.match[i - 1];
            for (int gap = this.match[i] - prev, j = 1; j < gap; ++j) {
                m_vec[count] = prev + j;
                t_vec[count] = target;
                ++count;
            }
            m_vec[count] = this.match[i];
            t_vec[count] = this.targets[i];
            ++count;
        }
        this.match = new int[count];
        this.targets = new InstructionHandle[count];
        System.arraycopy(m_vec, 0, this.match, 0, count);
        System.arraycopy(t_vec, 0, this.targets, 0, count);
    }
    
    private final void sort(final int l, final int r) {
        int i = l;
        int j = r;
        final int m = this.match[(l + r) / 2];
        while (true) {
            if (this.match[i] < m) {
                ++i;
            }
            else {
                while (m < this.match[j]) {
                    --j;
                }
                if (i <= j) {
                    final int h = this.match[i];
                    this.match[i] = this.match[j];
                    this.match[j] = h;
                    final InstructionHandle h2 = this.targets[i];
                    this.targets[i] = this.targets[j];
                    this.targets[j] = h2;
                    ++i;
                    --j;
                }
                if (i > j) {
                    break;
                }
                continue;
            }
        }
        if (l < j) {
            this.sort(l, j);
        }
        if (i < r) {
            this.sort(i, r);
        }
    }
    
    private final boolean matchIsOrdered(final int max_gap) {
        for (int i = 1; i < this.match_length; ++i) {
            if (this.match[i] - this.match[i - 1] > max_gap) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public final InstructionList getInstructionList() {
        return new InstructionList(this.instruction);
    }
    
    public final Instruction getInstruction() {
        return this.instruction;
    }
}
