package org.antlr.v4.runtime.atn;

import java.util.ArrayList;
import java.util.List;

public class DecisionInfo
{
    public final int decision;
    public long invocations;
    public long timeInPrediction;
    public long SLL_TotalLook;
    public long SLL_MinLook;
    public long SLL_MaxLook;
    public LookaheadEventInfo SLL_MaxLookEvent;
    public long LL_TotalLook;
    public long LL_MinLook;
    public long LL_MaxLook;
    public LookaheadEventInfo LL_MaxLookEvent;
    public final List<ContextSensitivityInfo> contextSensitivities;
    public final List<ErrorInfo> errors;
    public final List<AmbiguityInfo> ambiguities;
    public final List<PredicateEvalInfo> predicateEvals;
    public long SLL_ATNTransitions;
    public long SLL_DFATransitions;
    public long LL_Fallback;
    public long LL_ATNTransitions;
    public long LL_DFATransitions;
    
    public DecisionInfo(final int decision) {
        this.contextSensitivities = new ArrayList<ContextSensitivityInfo>();
        this.errors = new ArrayList<ErrorInfo>();
        this.ambiguities = new ArrayList<AmbiguityInfo>();
        this.predicateEvals = new ArrayList<PredicateEvalInfo>();
        this.decision = decision;
    }
    
    @Override
    public String toString() {
        return "{decision=" + this.decision + ", contextSensitivities=" + this.contextSensitivities.size() + ", errors=" + this.errors.size() + ", ambiguities=" + this.ambiguities.size() + ", SLL_lookahead=" + this.SLL_TotalLook + ", SLL_ATNTransitions=" + this.SLL_ATNTransitions + ", SLL_DFATransitions=" + this.SLL_DFATransitions + ", LL_Fallback=" + this.LL_Fallback + ", LL_lookahead=" + this.LL_TotalLook + ", LL_ATNTransitions=" + this.LL_ATNTransitions + '}';
    }
}
