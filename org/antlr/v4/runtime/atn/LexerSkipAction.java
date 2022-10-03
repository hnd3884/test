package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.Lexer;

public final class LexerSkipAction implements LexerAction
{
    public static final LexerSkipAction INSTANCE;
    
    private LexerSkipAction() {
    }
    
    @Override
    public LexerActionType getActionType() {
        return LexerActionType.SKIP;
    }
    
    @Override
    public boolean isPositionDependent() {
        return false;
    }
    
    @Override
    public void execute(final Lexer lexer) {
        lexer.skip();
    }
    
    @Override
    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.getActionType().ordinal());
        return MurmurHash.finish(hash, 1);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this;
    }
    
    @Override
    public String toString() {
        return "skip";
    }
    
    static {
        INSTANCE = new LexerSkipAction();
    }
}
