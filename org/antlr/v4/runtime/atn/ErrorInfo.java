package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.TokenStream;

public class ErrorInfo extends DecisionEventInfo
{
    public ErrorInfo(final int decision, final ATNConfigSet configs, final TokenStream input, final int startIndex, final int stopIndex, final boolean fullCtx) {
        super(decision, configs, input, startIndex, stopIndex, fullCtx);
    }
}
