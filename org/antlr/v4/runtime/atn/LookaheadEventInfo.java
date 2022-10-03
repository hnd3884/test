package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.TokenStream;

public class LookaheadEventInfo extends DecisionEventInfo
{
    public int predictedAlt;
    
    public LookaheadEventInfo(final int decision, final ATNConfigSet configs, final int predictedAlt, final TokenStream input, final int startIndex, final int stopIndex, final boolean fullCtx) {
        super(decision, configs, input, startIndex, stopIndex, fullCtx);
        this.predictedAlt = predictedAlt;
    }
}
