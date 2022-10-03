package org.antlr.v4.runtime.atn;

public final class EpsilonTransition extends Transition
{
    private final int outermostPrecedenceReturn;
    
    public EpsilonTransition(final ATNState target) {
        this(target, -1);
    }
    
    public EpsilonTransition(final ATNState target, final int outermostPrecedenceReturn) {
        super(target);
        this.outermostPrecedenceReturn = outermostPrecedenceReturn;
    }
    
    public int outermostPrecedenceReturn() {
        return this.outermostPrecedenceReturn;
    }
    
    @Override
    public int getSerializationType() {
        return 1;
    }
    
    @Override
    public boolean isEpsilon() {
        return true;
    }
    
    @Override
    public boolean matches(final int symbol, final int minVocabSymbol, final int maxVocabSymbol) {
        return false;
    }
    
    @Override
    public String toString() {
        return "epsilon";
    }
}
