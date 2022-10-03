package org.antlr.v4.runtime.atn;

public final class WildcardTransition extends Transition
{
    public WildcardTransition(final ATNState target) {
        super(target);
    }
    
    @Override
    public int getSerializationType() {
        return 9;
    }
    
    @Override
    public boolean matches(final int symbol, final int minVocabSymbol, final int maxVocabSymbol) {
        return symbol >= minVocabSymbol && symbol <= maxVocabSymbol;
    }
    
    @Override
    public String toString() {
        return ".";
    }
}
