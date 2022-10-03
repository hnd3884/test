package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.TokenStream;

public class ContextSensitivityInfo extends DecisionEventInfo
{
    public ContextSensitivityInfo(final int decision, final ATNConfigSet configs, final TokenStream input, final int startIndex, final int stopIndex) {
        super(decision, configs, input, startIndex, stopIndex, true);
    }
}
