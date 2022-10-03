package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.Lexer;

public class LexerTypeAction implements LexerAction
{
    private final int type;
    
    public LexerTypeAction(final int type) {
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
    
    @Override
    public LexerActionType getActionType() {
        return LexerActionType.TYPE;
    }
    
    @Override
    public boolean isPositionDependent() {
        return false;
    }
    
    @Override
    public void execute(final Lexer lexer) {
        lexer.setType(this.type);
    }
    
    @Override
    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.getActionType().ordinal());
        hash = MurmurHash.update(hash, this.type);
        return MurmurHash.finish(hash, 2);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof LexerTypeAction && this.type == ((LexerTypeAction)obj).type);
    }
    
    @Override
    public String toString() {
        return String.format("type(%d)", this.type);
    }
}
