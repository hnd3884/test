package org.antlr.v4.runtime.atn;

public final class ActionTransition extends Transition
{
    public final int ruleIndex;
    public final int actionIndex;
    public final boolean isCtxDependent;
    
    public ActionTransition(final ATNState target, final int ruleIndex) {
        this(target, ruleIndex, -1, false);
    }
    
    public ActionTransition(final ATNState target, final int ruleIndex, final int actionIndex, final boolean isCtxDependent) {
        super(target);
        this.ruleIndex = ruleIndex;
        this.actionIndex = actionIndex;
        this.isCtxDependent = isCtxDependent;
    }
    
    @Override
    public int getSerializationType() {
        return 6;
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
        return "action_" + this.ruleIndex + ":" + this.actionIndex;
    }
}
