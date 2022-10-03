package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import java.util.Arrays;
import org.antlr.v4.runtime.misc.MurmurHash;

public class LexerActionExecutor
{
    private final LexerAction[] lexerActions;
    private final int hashCode;
    
    public LexerActionExecutor(final LexerAction[] lexerActions) {
        this.lexerActions = lexerActions;
        int hash = MurmurHash.initialize();
        for (final LexerAction lexerAction : lexerActions) {
            hash = MurmurHash.update(hash, lexerAction);
        }
        this.hashCode = MurmurHash.finish(hash, lexerActions.length);
    }
    
    public static LexerActionExecutor append(final LexerActionExecutor lexerActionExecutor, final LexerAction lexerAction) {
        if (lexerActionExecutor == null) {
            return new LexerActionExecutor(new LexerAction[] { lexerAction });
        }
        final LexerAction[] lexerActions = Arrays.copyOf(lexerActionExecutor.lexerActions, lexerActionExecutor.lexerActions.length + 1);
        lexerActions[lexerActions.length - 1] = lexerAction;
        return new LexerActionExecutor(lexerActions);
    }
    
    public LexerActionExecutor fixOffsetBeforeMatch(final int offset) {
        LexerAction[] updatedLexerActions = null;
        for (int i = 0; i < this.lexerActions.length; ++i) {
            if (this.lexerActions[i].isPositionDependent() && !(this.lexerActions[i] instanceof LexerIndexedCustomAction)) {
                if (updatedLexerActions == null) {
                    updatedLexerActions = this.lexerActions.clone();
                }
                updatedLexerActions[i] = new LexerIndexedCustomAction(offset, this.lexerActions[i]);
            }
        }
        if (updatedLexerActions == null) {
            return this;
        }
        return new LexerActionExecutor(updatedLexerActions);
    }
    
    public LexerAction[] getLexerActions() {
        return this.lexerActions;
    }
    
    public void execute(final Lexer lexer, final CharStream input, final int startIndex) {
        boolean requiresSeek = false;
        final int stopIndex = input.index();
        try {
            for (LexerAction lexerAction : this.lexerActions) {
                if (lexerAction instanceof LexerIndexedCustomAction) {
                    final int offset = ((LexerIndexedCustomAction)lexerAction).getOffset();
                    input.seek(startIndex + offset);
                    lexerAction = ((LexerIndexedCustomAction)lexerAction).getAction();
                    requiresSeek = (startIndex + offset != stopIndex);
                }
                else if (lexerAction.isPositionDependent()) {
                    input.seek(stopIndex);
                    requiresSeek = false;
                }
                lexerAction.execute(lexer);
            }
        }
        finally {
            if (requiresSeek) {
                input.seek(stopIndex);
            }
        }
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LexerActionExecutor)) {
            return false;
        }
        final LexerActionExecutor other = (LexerActionExecutor)obj;
        return this.hashCode == other.hashCode && Arrays.equals(this.lexerActions, other.lexerActions);
    }
}
