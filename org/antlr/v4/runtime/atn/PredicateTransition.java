package org.antlr.v4.runtime.atn;

public final class PredicateTransition extends AbstractPredicateTransition
{
    public final int ruleIndex;
    public final int predIndex;
    public final boolean isCtxDependent;
    
    public PredicateTransition(final ATNState target, final int ruleIndex, final int predIndex, final boolean isCtxDependent) {
        super(target);
        this.ruleIndex = ruleIndex;
        this.predIndex = predIndex;
        this.isCtxDependent = isCtxDependent;
    }
    
    @Override
    public int getSerializationType() {
        return 4;
    }
    
    @Override
    public boolean isEpsilon() {
        return true;
    }
    
    @Override
    public boolean matches(final int symbol, final int minVocabSymbol, final int maxVocabSymbol) {
        return false;
    }
    
    public SemanticContext.Predicate getPredicate() {
        return new SemanticContext.Predicate(this.ruleIndex, this.predIndex, this.isCtxDependent);
    }
    
    @Override
    public String toString() {
        return "pred_" + this.ruleIndex + ":" + this.predIndex;
    }
}
