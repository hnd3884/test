package org.antlr.v4.runtime.atn;

public final class PrecedencePredicateTransition extends AbstractPredicateTransition
{
    public final int precedence;
    
    public PrecedencePredicateTransition(final ATNState target, final int precedence) {
        super(target);
        this.precedence = precedence;
    }
    
    @Override
    public int getSerializationType() {
        return 10;
    }
    
    @Override
    public boolean isEpsilon() {
        return true;
    }
    
    @Override
    public boolean matches(final int symbol, final int minVocabSymbol, final int maxVocabSymbol) {
        return false;
    }
    
    public SemanticContext.PrecedencePredicate getPredicate() {
        return new SemanticContext.PrecedencePredicate(this.precedence);
    }
    
    @Override
    public String toString() {
        return this.precedence + " >= _p";
    }
}
