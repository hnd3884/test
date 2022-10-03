package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.Lexer;

public final class LexerModeAction implements LexerAction
{
    private final int mode;
    
    public LexerModeAction(final int mode) {
        this.mode = mode;
    }
    
    public int getMode() {
        return this.mode;
    }
    
    @Override
    public LexerActionType getActionType() {
        return LexerActionType.MODE;
    }
    
    @Override
    public boolean isPositionDependent() {
        return false;
    }
    
    @Override
    public void execute(final Lexer lexer) {
        lexer.mode(this.mode);
    }
    
    @Override
    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.getActionType().ordinal());
        hash = MurmurHash.update(hash, this.mode);
        return MurmurHash.finish(hash, 2);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof LexerModeAction && this.mode == ((LexerModeAction)obj).mode);
    }
    
    @Override
    public String toString() {
        return String.format("mode(%d)", this.mode);
    }
}
