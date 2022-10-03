package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

public final class RangeTransition extends Transition
{
    public final int from;
    public final int to;
    
    public RangeTransition(final ATNState target, final int from, final int to) {
        super(target);
        this.from = from;
        this.to = to;
    }
    
    @Override
    public int getSerializationType() {
        return 2;
    }
    
    @Override
    public IntervalSet label() {
        return IntervalSet.of(this.from, this.to);
    }
    
    @Override
    public boolean matches(final int symbol, final int minVocabSymbol, final int maxVocabSymbol) {
        return symbol >= this.from && symbol <= this.to;
    }
    
    @Override
    public String toString() {
        return "'" + (char)this.from + "'..'" + (char)this.to + "'";
    }
}
