package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.Lexer;

public final class LexerPushModeAction implements LexerAction
{
    private final int mode;
    
    public LexerPushModeAction(final int mode) {
        this.mode = mode;
    }
    
    public int getMode() {
        return this.mode;
    }
    
    @Override
    public LexerActionType getActionType() {
        return LexerActionType.PUSH_MODE;
    }
    
    @Override
    public boolean isPositionDependent() {
        return false;
    }
    
    @Override
    public void execute(final Lexer lexer) {
        lexer.pushMode(this.mode);
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
        return obj == this || (obj instanceof LexerPushModeAction && this.mode == ((LexerPushModeAction)obj).mode);
    }
    
    @Override
    public String toString() {
        return String.format("pushMode(%d)", this.mode);
    }
}
