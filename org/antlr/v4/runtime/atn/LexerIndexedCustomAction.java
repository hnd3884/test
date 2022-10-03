package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.Lexer;

public final class LexerIndexedCustomAction implements LexerAction
{
    private final int offset;
    private final LexerAction action;
    
    public LexerIndexedCustomAction(final int offset, final LexerAction action) {
        this.offset = offset;
        this.action = action;
    }
    
    public int getOffset() {
        return this.offset;
    }
    
    public LexerAction getAction() {
        return this.action;
    }
    
    @Override
    public LexerActionType getActionType() {
        return this.action.getActionType();
    }
    
    @Override
    public boolean isPositionDependent() {
        return true;
    }
    
    @Override
    public void execute(final Lexer lexer) {
        this.action.execute(lexer);
    }
    
    @Override
    public int hashCode() {
        int hash = MurmurHash.initialize();
        hash = MurmurHash.update(hash, this.offset);
        hash = MurmurHash.update(hash, this.action);
        return MurmurHash.finish(hash, 2);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LexerIndexedCustomAction)) {
            return false;
        }
        final LexerIndexedCustomAction other = (LexerIndexedCustomAction)obj;
        return this.offset == other.offset && this.action.equals(other.action);
    }
}
