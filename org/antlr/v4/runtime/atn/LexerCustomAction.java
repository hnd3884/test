package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Lexer;

public final class LexerCustomAction implements LexerAction
{
    private final int ruleIndex;
    private final int actionIndex;
    
    public LexerCustomAction(final int ruleIndex, final int actionIndex) {
        this.ruleIndex = ruleIndex;
        this.actionIndex = actionIndex;
    }
    
    public int getRuleIndex() {
        return this.ruleIndex;
    }
    
    public int getActionIndex() {
        return this.actionIndex;
    }
    
    @Override
    public LexerActionType getActionType() {
        return LexerActionType.CUSTOM;
    }
    
    @Override
    public boolean isPositionDependent() {
        return true;
    }
    
    @Override
    public void execute(final Lexer lexer) {
        lexer.action(null, this.ruleIndex, this.actionIndex);
    }
    
    @Override
    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.getActionType().ordinal());
        hash = MurmurHash.update(hash, this.ruleIndex);
        hash = MurmurHash.update(hash, this.actionIndex);
        return MurmurHash.finish(hash, 3);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LexerCustomAction)) {
            return false;
        }
        final LexerCustomAction other = (LexerCustomAction)obj;
        return this.ruleIndex == other.ruleIndex && this.actionIndex == other.actionIndex;
    }
}
