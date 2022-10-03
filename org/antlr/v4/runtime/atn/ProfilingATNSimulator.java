package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Recognizer;
import java.util.BitSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.dfa.DFAState;

public class ProfilingATNSimulator extends ParserATNSimulator
{
    protected final DecisionInfo[] decisions;
    protected int numDecisions;
    protected int _sllStopIndex;
    protected int _llStopIndex;
    protected int currentDecision;
    protected DFAState currentState;
    protected int conflictingAltResolvedBySLL;
    
    public ProfilingATNSimulator(final Parser parser) {
        super(parser, ((Recognizer<Symbol, ParserATNSimulator>)parser).getInterpreter().atn, ((Recognizer<Symbol, ParserATNSimulator>)parser).getInterpreter().decisionToDFA, ((Recognizer<Symbol, ParserATNSimulator>)parser).getInterpreter().sharedContextCache);
        this.numDecisions = this.atn.decisionToState.size();
        this.decisions = new DecisionInfo[this.numDecisions];
        for (int i = 0; i < this.numDecisions; ++i) {
            this.decisions[i] = new DecisionInfo(i);
        }
    }
    
    @Override
    public int adaptivePredict(final TokenStream input, final int decision, final ParserRuleContext outerContext) {
        try {
            this._sllStopIndex = -1;
            this._llStopIndex = -1;
            this.currentDecision = decision;
            final long start = System.nanoTime();
            final int alt = super.adaptivePredict(input, decision, outerContext);
            final long stop = System.nanoTime();
            final DecisionInfo decisionInfo = this.decisions[decision];
            decisionInfo.timeInPrediction += stop - start;
            final DecisionInfo decisionInfo2 = this.decisions[decision];
            ++decisionInfo2.invocations;
            final int SLL_k = this._sllStopIndex - this._startIndex + 1;
            final DecisionInfo decisionInfo3 = this.decisions[decision];
            decisionInfo3.SLL_TotalLook += SLL_k;
            this.decisions[decision].SLL_MinLook = ((this.decisions[decision].SLL_MinLook == 0L) ? SLL_k : Math.min(this.decisions[decision].SLL_MinLook, SLL_k));
            if (SLL_k > this.decisions[decision].SLL_MaxLook) {
                this.decisions[decision].SLL_MaxLook = SLL_k;
                this.decisions[decision].SLL_MaxLookEvent = new LookaheadEventInfo(decision, null, alt, input, this._startIndex, this._sllStopIndex, false);
            }
            if (this._llStopIndex >= 0) {
                final int LL_k = this._llStopIndex - this._startIndex + 1;
                final DecisionInfo decisionInfo4 = this.decisions[decision];
                decisionInfo4.LL_TotalLook += LL_k;
                this.decisions[decision].LL_MinLook = ((this.decisions[decision].LL_MinLook == 0L) ? LL_k : Math.min(this.decisions[decision].LL_MinLook, LL_k));
                if (LL_k > this.decisions[decision].LL_MaxLook) {
                    this.decisions[decision].LL_MaxLook = LL_k;
                    this.decisions[decision].LL_MaxLookEvent = new LookaheadEventInfo(decision, null, alt, input, this._startIndex, this._llStopIndex, true);
                }
            }
            return alt;
        }
        finally {
            this.currentDecision = -1;
        }
    }
    
    @Override
    protected DFAState getExistingTargetState(final DFAState previousD, final int t) {
        this._sllStopIndex = this._input.index();
        final DFAState existingTargetState = super.getExistingTargetState(previousD, t);
        if (existingTargetState != null) {
            final DecisionInfo decisionInfo = this.decisions[this.currentDecision];
            ++decisionInfo.SLL_DFATransitions;
            if (existingTargetState == ProfilingATNSimulator.ERROR) {
                this.decisions[this.currentDecision].errors.add(new ErrorInfo(this.currentDecision, previousD.configs, this._input, this._startIndex, this._sllStopIndex, false));
            }
        }
        return this.currentState = existingTargetState;
    }
    
    @Override
    protected DFAState computeTargetState(final DFA dfa, final DFAState previousD, final int t) {
        final DFAState state = super.computeTargetState(dfa, previousD, t);
        return this.currentState = state;
    }
    
    @Override
    protected ATNConfigSet computeReachSet(final ATNConfigSet closure, final int t, final boolean fullCtx) {
        if (fullCtx) {
            this._llStopIndex = this._input.index();
        }
        final ATNConfigSet reachConfigs = super.computeReachSet(closure, t, fullCtx);
        if (fullCtx) {
            final DecisionInfo decisionInfo = this.decisions[this.currentDecision];
            ++decisionInfo.LL_ATNTransitions;
            if (reachConfigs == null) {
                this.decisions[this.currentDecision].errors.add(new ErrorInfo(this.currentDecision, closure, this._input, this._startIndex, this._llStopIndex, true));
            }
        }
        else {
            final DecisionInfo decisionInfo2 = this.decisions[this.currentDecision];
            ++decisionInfo2.SLL_ATNTransitions;
            if (reachConfigs == null) {
                this.decisions[this.currentDecision].errors.add(new ErrorInfo(this.currentDecision, closure, this._input, this._startIndex, this._sllStopIndex, false));
            }
        }
        return reachConfigs;
    }
    
    @Override
    protected boolean evalSemanticContext(final SemanticContext pred, final ParserRuleContext parserCallStack, final int alt, final boolean fullCtx) {
        final boolean result = super.evalSemanticContext(pred, parserCallStack, alt, fullCtx);
        if (!(pred instanceof SemanticContext.PrecedencePredicate)) {
            final boolean fullContext = this._llStopIndex >= 0;
            final int stopIndex = fullContext ? this._llStopIndex : this._sllStopIndex;
            this.decisions[this.currentDecision].predicateEvals.add(new PredicateEvalInfo(this.currentDecision, this._input, this._startIndex, stopIndex, pred, result, alt, fullCtx));
        }
        return result;
    }
    
    @Override
    protected void reportAttemptingFullContext(final DFA dfa, final BitSet conflictingAlts, final ATNConfigSet configs, final int startIndex, final int stopIndex) {
        if (conflictingAlts != null) {
            this.conflictingAltResolvedBySLL = conflictingAlts.nextSetBit(0);
        }
        else {
            this.conflictingAltResolvedBySLL = configs.getAlts().nextSetBit(0);
        }
        final DecisionInfo decisionInfo = this.decisions[this.currentDecision];
        ++decisionInfo.LL_Fallback;
        super.reportAttemptingFullContext(dfa, conflictingAlts, configs, startIndex, stopIndex);
    }
    
    @Override
    protected void reportContextSensitivity(final DFA dfa, final int prediction, final ATNConfigSet configs, final int startIndex, final int stopIndex) {
        if (prediction != this.conflictingAltResolvedBySLL) {
            this.decisions[this.currentDecision].contextSensitivities.add(new ContextSensitivityInfo(this.currentDecision, configs, this._input, startIndex, stopIndex));
        }
        super.reportContextSensitivity(dfa, prediction, configs, startIndex, stopIndex);
    }
    
    @Override
    protected void reportAmbiguity(final DFA dfa, final DFAState D, final int startIndex, final int stopIndex, final boolean exact, final BitSet ambigAlts, final ATNConfigSet configs) {
        int prediction;
        if (ambigAlts != null) {
            prediction = ambigAlts.nextSetBit(0);
        }
        else {
            prediction = configs.getAlts().nextSetBit(0);
        }
        if (configs.fullCtx && prediction != this.conflictingAltResolvedBySLL) {
            this.decisions[this.currentDecision].contextSensitivities.add(new ContextSensitivityInfo(this.currentDecision, configs, this._input, startIndex, stopIndex));
        }
        this.decisions[this.currentDecision].ambiguities.add(new AmbiguityInfo(this.currentDecision, configs, ambigAlts, this._input, startIndex, stopIndex, configs.fullCtx));
        super.reportAmbiguity(dfa, D, startIndex, stopIndex, exact, ambigAlts, configs);
    }
    
    public DecisionInfo[] getDecisionInfo() {
        return this.decisions;
    }
    
    public DFAState getCurrentState() {
        return this.currentState;
    }
}
