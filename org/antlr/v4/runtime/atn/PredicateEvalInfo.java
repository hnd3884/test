package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.TokenStream;

public class PredicateEvalInfo extends DecisionEventInfo
{
    public final SemanticContext semctx;
    public final int predictedAlt;
    public final boolean evalResult;
    
    public PredicateEvalInfo(final int decision, final TokenStream input, final int startIndex, final int stopIndex, final SemanticContext semctx, final boolean evalResult, final int predictedAlt, final boolean fullCtx) {
        super(decision, new ATNConfigSet(), input, startIndex, stopIndex, fullCtx);
        this.semctx = semctx;
        this.evalResult = evalResult;
        this.predictedAlt = predictedAlt;
    }
}
