package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

public final class NotSetTransition extends SetTransition
{
    public NotSetTransition(final ATNState target, final IntervalSet set) {
        super(target, set);
    }
    
    @Override
    public int getSerializationType() {
        return 8;
    }
    
    @Override
    public boolean matches(final int symbol, final int minVocabSymbol, final int maxVocabSymbol) {
        return symbol >= minVocabSymbol && symbol <= maxVocabSymbol && !super.matches(symbol, minVocabSymbol, maxVocabSymbol);
    }
    
    @Override
    public String toString() {
        return '~' + super.toString();
    }
}
