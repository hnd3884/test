package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

public final class AtomTransition extends Transition
{
    public final int label;
    
    public AtomTransition(final ATNState target, final int label) {
        super(target);
        this.label = label;
    }
    
    @Override
    public int getSerializationType() {
        return 5;
    }
    
    @Override
    public IntervalSet label() {
        return IntervalSet.of(this.label);
    }
    
    @Override
    public boolean matches(final int symbol, final int minVocabSymbol, final int maxVocabSymbol) {
        return this.label == symbol;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.label);
    }
}
