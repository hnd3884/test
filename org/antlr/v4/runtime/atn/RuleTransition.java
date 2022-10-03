package org.antlr.v4.runtime.atn;

public final class RuleTransition extends Transition
{
    public final int ruleIndex;
    public final int precedence;
    public ATNState followState;
    
    @Deprecated
    public RuleTransition(final RuleStartState ruleStart, final int ruleIndex, final ATNState followState) {
        this(ruleStart, ruleIndex, 0, followState);
    }
    
    public RuleTransition(final RuleStartState ruleStart, final int ruleIndex, final int precedence, final ATNState followState) {
        super(ruleStart);
        this.ruleIndex = ruleIndex;
        this.precedence = precedence;
        this.followState = followState;
    }
    
    @Override
    public int getSerializationType() {
        return 3;
    }
    
    @Override
    public boolean isEpsilon() {
        return true;
    }
    
    @Override
    public boolean matches(final int symbol, final int minVocabSymbol, final int maxVocabSymbol) {
        return false;
    }
}
