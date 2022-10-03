package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.ObjectEqualityComparator;
import org.antlr.v4.runtime.misc.MurmurHash;

public class LexerATNConfig extends ATNConfig
{
    private final LexerActionExecutor lexerActionExecutor;
    private final boolean passedThroughNonGreedyDecision;
    
    public LexerATNConfig(final ATNState state, final int alt, final PredictionContext context) {
        super(state, alt, context, SemanticContext.NONE);
        this.passedThroughNonGreedyDecision = false;
        this.lexerActionExecutor = null;
    }
    
    public LexerATNConfig(final ATNState state, final int alt, final PredictionContext context, final LexerActionExecutor lexerActionExecutor) {
        super(state, alt, context, SemanticContext.NONE);
        this.lexerActionExecutor = lexerActionExecutor;
        this.passedThroughNonGreedyDecision = false;
    }
    
    public LexerATNConfig(final LexerATNConfig c, final ATNState state) {
        super(c, state, c.context, c.semanticContext);
        this.lexerActionExecutor = c.lexerActionExecutor;
        this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
    }
    
    public LexerATNConfig(final LexerATNConfig c, final ATNState state, final LexerActionExecutor lexerActionExecutor) {
        super(c, state, c.context, c.semanticContext);
        this.lexerActionExecutor = lexerActionExecutor;
        this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
    }
    
    public LexerATNConfig(final LexerATNConfig c, final ATNState state, final PredictionContext context) {
        super(c, state, context, c.semanticContext);
        this.lexerActionExecutor = c.lexerActionExecutor;
        this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
    }
    
    public final LexerActionExecutor getLexerActionExecutor() {
        return this.lexerActionExecutor;
    }
    
    public final boolean hasPassedThroughNonGreedyDecision() {
        return this.passedThroughNonGreedyDecision;
    }
    
    @Override
    public int hashCode() {
        int hashCode = MurmurHash.initialize(7);
        hashCode = MurmurHash.update(hashCode, this.state.stateNumber);
        hashCode = MurmurHash.update(hashCode, this.alt);
        hashCode = MurmurHash.update(hashCode, this.context);
        hashCode = MurmurHash.update(hashCode, this.semanticContext);
        hashCode = MurmurHash.update(hashCode, this.passedThroughNonGreedyDecision ? 1 : 0);
        hashCode = MurmurHash.update(hashCode, this.lexerActionExecutor);
        hashCode = MurmurHash.finish(hashCode, 6);
        return hashCode;
    }
    
    @Override
    public boolean equals(final ATNConfig other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LexerATNConfig)) {
            return false;
        }
        final LexerATNConfig lexerOther = (LexerATNConfig)other;
        return this.passedThroughNonGreedyDecision == lexerOther.passedThroughNonGreedyDecision && ObjectEqualityComparator.INSTANCE.equals(this.lexerActionExecutor, lexerOther.lexerActionExecutor) && super.equals(other);
    }
    
    private static boolean checkNonGreedyDecision(final LexerATNConfig source, final ATNState target) {
        return source.passedThroughNonGreedyDecision || (target instanceof DecisionState && ((DecisionState)target).nonGreedy);
    }
}
