package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

public class SetTransition extends Transition
{
    public final IntervalSet set;
    
    public SetTransition(final ATNState target, IntervalSet set) {
        super(target);
        if (set == null) {
            set = IntervalSet.of(0);
        }
        this.set = set;
    }
    
    @Override
    public int getSerializationType() {
        return 7;
    }
    
    @Override
    public IntervalSet label() {
        return this.set;
    }
    
    @Override
    public boolean matches(final int symbol, final int minVocabSymbol, final int maxVocabSymbol) {
        return this.set.contains(symbol);
    }
    
    @Override
    public String toString() {
        return this.set.toString();
    }
}
