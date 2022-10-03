package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.TokenStream;
import java.util.BitSet;

public class AmbiguityInfo extends DecisionEventInfo
{
    public BitSet ambigAlts;
    
    public AmbiguityInfo(final int decision, final ATNConfigSet configs, final BitSet ambigAlts, final TokenStream input, final int startIndex, final int stopIndex, final boolean fullCtx) {
        super(decision, configs, input, startIndex, stopIndex, fullCtx);
        this.ambigAlts = ambigAlts;
    }
}
