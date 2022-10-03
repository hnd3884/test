package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.MurmurHash;

public class ATNConfig
{
    private static final int SUPPRESS_PRECEDENCE_FILTER = 1073741824;
    public final ATNState state;
    public final int alt;
    public PredictionContext context;
    public int reachesIntoOuterContext;
    public final SemanticContext semanticContext;
    
    public ATNConfig(final ATNConfig old) {
        this.state = old.state;
        this.alt = old.alt;
        this.context = old.context;
        this.semanticContext = old.semanticContext;
        this.reachesIntoOuterContext = old.reachesIntoOuterContext;
    }
    
    public ATNConfig(final ATNState state, final int alt, final PredictionContext context) {
        this(state, alt, context, SemanticContext.NONE);
    }
    
    public ATNConfig(final ATNState state, final int alt, final PredictionContext context, final SemanticContext semanticContext) {
        this.state = state;
        this.alt = alt;
        this.context = context;
        this.semanticContext = semanticContext;
    }
    
    public ATNConfig(final ATNConfig c, final ATNState state) {
        this(c, state, c.context, c.semanticContext);
    }
    
    public ATNConfig(final ATNConfig c, final ATNState state, final SemanticContext semanticContext) {
        this(c, state, c.context, semanticContext);
    }
    
    public ATNConfig(final ATNConfig c, final SemanticContext semanticContext) {
        this(c, c.state, c.context, semanticContext);
    }
    
    public ATNConfig(final ATNConfig c, final ATNState state, final PredictionContext context) {
        this(c, state, context, c.semanticContext);
    }
    
    public ATNConfig(final ATNConfig c, final ATNState state, final PredictionContext context, final SemanticContext semanticContext) {
        this.state = state;
        this.alt = c.alt;
        this.context = context;
        this.semanticContext = semanticContext;
        this.reachesIntoOuterContext = c.reachesIntoOuterContext;
    }
    
    public final int getOuterContextDepth() {
        return this.reachesIntoOuterContext & 0xBFFFFFFF;
    }
    
    public final boolean isPrecedenceFilterSuppressed() {
        return (this.reachesIntoOuterContext & 0x40000000) != 0x0;
    }
    
    public final void setPrecedenceFilterSuppressed(final boolean value) {
        if (value) {
            this.reachesIntoOuterContext |= 0x40000000;
        }
        else {
            this.reachesIntoOuterContext &= 0xBFFFFFFF;
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ATNConfig && this.equals((ATNConfig)o);
    }
    
    public boolean equals(final ATNConfig other) {
        return this == other || (other != null && this.state.stateNumber == other.state.stateNumber && this.alt == other.alt && (this.context == other.context || (this.context != null && this.context.equals(other.context))) && this.semanticContext.equals(other.semanticContext) && this.isPrecedenceFilterSuppressed() == other.isPrecedenceFilterSuppressed());
    }
    
    @Override
    public int hashCode() {
        int hashCode = MurmurHash.initialize(7);
        hashCode = MurmurHash.update(hashCode, this.state.stateNumber);
        hashCode = MurmurHash.update(hashCode, this.alt);
        hashCode = MurmurHash.update(hashCode, this.context);
        hashCode = MurmurHash.update(hashCode, this.semanticContext);
        hashCode = MurmurHash.finish(hashCode, 4);
        return hashCode;
    }
    
    @Override
    public String toString() {
        return this.toString(null, true);
    }
    
    public String toString(final Recognizer<?, ?> recog, final boolean showAlt) {
        final StringBuilder buf = new StringBuilder();
        buf.append('(');
        buf.append(this.state);
        if (showAlt) {
            buf.append(",");
            buf.append(this.alt);
        }
        if (this.context != null) {
            buf.append(",[");
            buf.append(this.context.toString());
            buf.append("]");
        }
        if (this.semanticContext != null && this.semanticContext != SemanticContext.NONE) {
            buf.append(",");
            buf.append(this.semanticContext);
        }
        if (this.getOuterContextDepth() > 0) {
            buf.append(",up=").append(this.getOuterContextDepth());
        }
        buf.append(')');
        return buf.toString();
    }
}
