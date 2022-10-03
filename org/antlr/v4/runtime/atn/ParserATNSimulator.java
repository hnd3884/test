package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.misc.Pair;
import java.util.Map;
import org.antlr.v4.runtime.Recognizer;
import java.util.HashMap;
import org.antlr.v4.runtime.misc.IntervalSet;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.BitSet;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.DoubleKeyMap;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.Parser;

public class ParserATNSimulator extends ATNSimulator
{
    public static final boolean debug = false;
    public static final boolean debug_list_atn_decisions = false;
    public static final boolean dfa_debug = false;
    public static final boolean retry_debug = false;
    protected final Parser parser;
    public final DFA[] decisionToDFA;
    private PredictionMode mode;
    protected DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache;
    protected TokenStream _input;
    protected int _startIndex;
    protected ParserRuleContext _outerContext;
    protected DFA _dfa;
    
    public ParserATNSimulator(final ATN atn, final DFA[] decisionToDFA, final PredictionContextCache sharedContextCache) {
        this(null, atn, decisionToDFA, sharedContextCache);
    }
    
    public ParserATNSimulator(final Parser parser, final ATN atn, final DFA[] decisionToDFA, final PredictionContextCache sharedContextCache) {
        super(atn, sharedContextCache);
        this.mode = PredictionMode.LL;
        this.parser = parser;
        this.decisionToDFA = decisionToDFA;
    }
    
    @Override
    public void reset() {
    }
    
    @Override
    public void clearDFA() {
        for (int d = 0; d < this.decisionToDFA.length; ++d) {
            this.decisionToDFA[d] = new DFA(this.atn.getDecisionState(d), d);
        }
    }
    
    public int adaptivePredict(final TokenStream input, final int decision, ParserRuleContext outerContext) {
        this._input = input;
        this._startIndex = input.index();
        this._outerContext = outerContext;
        final DFA dfa = this.decisionToDFA[decision];
        this._dfa = dfa;
        final int m = input.mark();
        final int index = this._startIndex;
        try {
            DFAState s0;
            if (dfa.isPrecedenceDfa()) {
                s0 = dfa.getPrecedenceStartState(this.parser.getPrecedence());
            }
            else {
                s0 = dfa.s0;
            }
            if (s0 == null) {
                if (outerContext == null) {
                    outerContext = ParserRuleContext.EMPTY;
                }
                final boolean fullCtx = false;
                ATNConfigSet s0_closure = this.computeStartState(dfa.atnStartState, ParserRuleContext.EMPTY, fullCtx);
                if (dfa.isPrecedenceDfa()) {
                    dfa.s0.configs = s0_closure;
                    s0_closure = this.applyPrecedenceFilter(s0_closure);
                    s0 = this.addDFAState(dfa, new DFAState(s0_closure));
                    dfa.setPrecedenceStartState(this.parser.getPrecedence(), s0);
                }
                else {
                    s0 = this.addDFAState(dfa, new DFAState(s0_closure));
                    dfa.s0 = s0;
                }
            }
            final int alt = this.execATN(dfa, s0, input, index, outerContext);
            return alt;
        }
        finally {
            this.mergeCache = null;
            this._dfa = null;
            input.seek(index);
            input.release(m);
        }
    }
    
    protected int execATN(final DFA dfa, final DFAState s0, final TokenStream input, final int startIndex, final ParserRuleContext outerContext) {
        DFAState previousD = s0;
        int t = input.LA(1);
        while (true) {
            DFAState D = this.getExistingTargetState(previousD, t);
            if (D == null) {
                D = this.computeTargetState(dfa, previousD, t);
            }
            if (D == ParserATNSimulator.ERROR) {
                final NoViableAltException e = this.noViableAlt(input, outerContext, previousD.configs, startIndex);
                input.seek(startIndex);
                final int alt = this.getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previousD.configs, outerContext);
                if (alt != 0) {
                    return alt;
                }
                throw e;
            }
            else {
                if (D.requiresFullContext && this.mode != PredictionMode.SLL) {
                    BitSet conflictingAlts = D.configs.conflictingAlts;
                    if (D.predicates != null) {
                        final int conflictIndex = input.index();
                        if (conflictIndex != startIndex) {
                            input.seek(startIndex);
                        }
                        conflictingAlts = this.evalSemanticContext(D.predicates, outerContext, true);
                        if (conflictingAlts.cardinality() == 1) {
                            return conflictingAlts.nextSetBit(0);
                        }
                        if (conflictIndex != startIndex) {
                            input.seek(conflictIndex);
                        }
                    }
                    final boolean fullCtx = true;
                    final ATNConfigSet s0_closure = this.computeStartState(dfa.atnStartState, outerContext, fullCtx);
                    this.reportAttemptingFullContext(dfa, conflictingAlts, D.configs, startIndex, input.index());
                    final int alt2 = this.execATNWithFullContext(dfa, D, s0_closure, input, startIndex, outerContext);
                    return alt2;
                }
                if (D.isAcceptState) {
                    if (D.predicates == null) {
                        return D.prediction;
                    }
                    final int stopIndex = input.index();
                    input.seek(startIndex);
                    final BitSet alts = this.evalSemanticContext(D.predicates, outerContext, true);
                    switch (alts.cardinality()) {
                        case 0: {
                            throw this.noViableAlt(input, outerContext, D.configs, startIndex);
                        }
                        case 1: {
                            return alts.nextSetBit(0);
                        }
                        default: {
                            this.reportAmbiguity(dfa, D, startIndex, stopIndex, false, alts, D.configs);
                            return alts.nextSetBit(0);
                        }
                    }
                }
                else {
                    previousD = D;
                    if (t == -1) {
                        continue;
                    }
                    input.consume();
                    t = input.LA(1);
                }
            }
        }
    }
    
    protected DFAState getExistingTargetState(final DFAState previousD, final int t) {
        final DFAState[] edges = previousD.edges;
        if (edges == null || t + 1 < 0 || t + 1 >= edges.length) {
            return null;
        }
        return edges[t + 1];
    }
    
    protected DFAState computeTargetState(final DFA dfa, final DFAState previousD, final int t) {
        final ATNConfigSet reach = this.computeReachSet(previousD.configs, t, false);
        if (reach == null) {
            this.addDFAEdge(dfa, previousD, t, ParserATNSimulator.ERROR);
            return ParserATNSimulator.ERROR;
        }
        DFAState D = new DFAState(reach);
        final int predictedAlt = getUniqueAlt(reach);
        if (predictedAlt != 0) {
            D.isAcceptState = true;
            D.configs.uniqueAlt = predictedAlt;
            D.prediction = predictedAlt;
        }
        else if (PredictionMode.hasSLLConflictTerminatingPrediction(this.mode, reach)) {
            D.configs.conflictingAlts = this.getConflictingAlts(reach);
            D.requiresFullContext = true;
            D.isAcceptState = true;
            D.prediction = D.configs.conflictingAlts.nextSetBit(0);
        }
        if (D.isAcceptState && D.configs.hasSemanticContext) {
            this.predicateDFAState(D, this.atn.getDecisionState(dfa.decision));
            if (D.predicates != null) {
                D.prediction = 0;
            }
        }
        D = this.addDFAEdge(dfa, previousD, t, D);
        return D;
    }
    
    protected void predicateDFAState(final DFAState dfaState, final DecisionState decisionState) {
        final int nalts = decisionState.getNumberOfTransitions();
        final BitSet altsToCollectPredsFrom = this.getConflictingAltsOrUniqueAlt(dfaState.configs);
        final SemanticContext[] altToPred = this.getPredsForAmbigAlts(altsToCollectPredsFrom, dfaState.configs, nalts);
        if (altToPred != null) {
            dfaState.predicates = this.getPredicatePredictions(altsToCollectPredsFrom, altToPred);
            dfaState.prediction = 0;
        }
        else {
            dfaState.prediction = altsToCollectPredsFrom.nextSetBit(0);
        }
    }
    
    protected int execATNWithFullContext(final DFA dfa, final DFAState D, final ATNConfigSet s0, final TokenStream input, final int startIndex, final ParserRuleContext outerContext) {
        final boolean fullCtx = true;
        boolean foundExactAmbig = false;
        ATNConfigSet reach = null;
        ATNConfigSet previous = s0;
        input.seek(startIndex);
        int t = input.LA(1);
        int predictedAlt;
        while (true) {
            reach = this.computeReachSet(previous, t, fullCtx);
            if (reach == null) {
                final NoViableAltException e = this.noViableAlt(input, outerContext, previous, startIndex);
                input.seek(startIndex);
                final int alt = this.getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previous, outerContext);
                if (alt != 0) {
                    return alt;
                }
                throw e;
            }
            else {
                final Collection<BitSet> altSubSets = PredictionMode.getConflictingAltSubsets(reach);
                reach.uniqueAlt = getUniqueAlt(reach);
                if (reach.uniqueAlt != 0) {
                    predictedAlt = reach.uniqueAlt;
                    break;
                }
                if (this.mode != PredictionMode.LL_EXACT_AMBIG_DETECTION) {
                    predictedAlt = PredictionMode.resolvesToJustOneViableAlt(altSubSets);
                    if (predictedAlt != 0) {
                        break;
                    }
                }
                else if (PredictionMode.allSubsetsConflict(altSubSets) && PredictionMode.allSubsetsEqual(altSubSets)) {
                    foundExactAmbig = true;
                    predictedAlt = PredictionMode.getSingleViableAlt(altSubSets);
                    break;
                }
                previous = reach;
                if (t == -1) {
                    continue;
                }
                input.consume();
                t = input.LA(1);
            }
        }
        if (reach.uniqueAlt != 0) {
            this.reportContextSensitivity(dfa, predictedAlt, reach, startIndex, input.index());
            return predictedAlt;
        }
        this.reportAmbiguity(dfa, D, startIndex, input.index(), foundExactAmbig, reach.getAlts(), reach);
        return predictedAlt;
    }
    
    protected ATNConfigSet computeReachSet(final ATNConfigSet closure, final int t, final boolean fullCtx) {
        if (this.mergeCache == null) {
            this.mergeCache = new DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext>();
        }
        final ATNConfigSet intermediate = new ATNConfigSet(fullCtx);
        List<ATNConfig> skippedStopStates = null;
        for (final ATNConfig c : closure) {
            if (c.state instanceof RuleStopState) {
                assert c.context.isEmpty();
                if (!fullCtx && t != -1) {
                    continue;
                }
                if (skippedStopStates == null) {
                    skippedStopStates = new ArrayList<ATNConfig>();
                }
                skippedStopStates.add(c);
            }
            else {
                for (int n = c.state.getNumberOfTransitions(), ti = 0; ti < n; ++ti) {
                    final Transition trans = c.state.transition(ti);
                    final ATNState target = this.getReachableTarget(trans, t);
                    if (target != null) {
                        intermediate.add(new ATNConfig(c, target), this.mergeCache);
                    }
                }
            }
        }
        ATNConfigSet reach = null;
        if (skippedStopStates == null && t != -1) {
            if (intermediate.size() == 1) {
                reach = intermediate;
            }
            else if (getUniqueAlt(intermediate) != 0) {
                reach = intermediate;
            }
        }
        if (reach == null) {
            reach = new ATNConfigSet(fullCtx);
            final Set<ATNConfig> closureBusy = new HashSet<ATNConfig>();
            final boolean treatEofAsEpsilon = t == -1;
            for (final ATNConfig c2 : intermediate) {
                this.closure(c2, reach, closureBusy, false, fullCtx, treatEofAsEpsilon);
            }
        }
        if (t == -1) {
            reach = this.removeAllConfigsNotInRuleStopState(reach, reach == intermediate);
        }
        if (skippedStopStates != null && (!fullCtx || !PredictionMode.hasConfigInRuleStopState(reach))) {
            assert !skippedStopStates.isEmpty();
            for (final ATNConfig c3 : skippedStopStates) {
                reach.add(c3, this.mergeCache);
            }
        }
        if (reach.isEmpty()) {
            return null;
        }
        return reach;
    }
    
    protected ATNConfigSet removeAllConfigsNotInRuleStopState(final ATNConfigSet configs, final boolean lookToEndOfRule) {
        if (PredictionMode.allConfigsInRuleStopStates(configs)) {
            return configs;
        }
        final ATNConfigSet result = new ATNConfigSet(configs.fullCtx);
        for (final ATNConfig config : configs) {
            if (config.state instanceof RuleStopState) {
                result.add(config, this.mergeCache);
            }
            else {
                if (!lookToEndOfRule || !config.state.onlyHasEpsilonTransitions()) {
                    continue;
                }
                final IntervalSet nextTokens = this.atn.nextTokens(config.state);
                if (!nextTokens.contains(-2)) {
                    continue;
                }
                final ATNState endOfRuleState = this.atn.ruleToStopState[config.state.ruleIndex];
                result.add(new ATNConfig(config, endOfRuleState), this.mergeCache);
            }
        }
        return result;
    }
    
    protected ATNConfigSet computeStartState(final ATNState p, final RuleContext ctx, final boolean fullCtx) {
        final PredictionContext initialContext = PredictionContext.fromRuleContext(this.atn, ctx);
        final ATNConfigSet configs = new ATNConfigSet(fullCtx);
        for (int i = 0; i < p.getNumberOfTransitions(); ++i) {
            final ATNState target = p.transition(i).target;
            final ATNConfig c = new ATNConfig(target, i + 1, initialContext);
            final Set<ATNConfig> closureBusy = new HashSet<ATNConfig>();
            this.closure(c, configs, closureBusy, true, fullCtx, false);
        }
        return configs;
    }
    
    protected ATNConfigSet applyPrecedenceFilter(final ATNConfigSet configs) {
        final Map<Integer, PredictionContext> statesFromAlt1 = new HashMap<Integer, PredictionContext>();
        final ATNConfigSet configSet = new ATNConfigSet(configs.fullCtx);
        for (final ATNConfig config : configs) {
            if (config.alt != 1) {
                continue;
            }
            final SemanticContext updatedContext = config.semanticContext.evalPrecedence(this.parser, this._outerContext);
            if (updatedContext == null) {
                continue;
            }
            statesFromAlt1.put(config.state.stateNumber, config.context);
            if (updatedContext != config.semanticContext) {
                configSet.add(new ATNConfig(config, updatedContext), this.mergeCache);
            }
            else {
                configSet.add(config, this.mergeCache);
            }
        }
        for (final ATNConfig config : configs) {
            if (config.alt == 1) {
                continue;
            }
            if (!config.isPrecedenceFilterSuppressed()) {
                final PredictionContext context = statesFromAlt1.get(config.state.stateNumber);
                if (context != null && context.equals(config.context)) {
                    continue;
                }
            }
            configSet.add(config, this.mergeCache);
        }
        return configSet;
    }
    
    protected ATNState getReachableTarget(final Transition trans, final int ttype) {
        if (trans.matches(ttype, 0, this.atn.maxTokenType)) {
            return trans.target;
        }
        return null;
    }
    
    protected SemanticContext[] getPredsForAmbigAlts(final BitSet ambigAlts, final ATNConfigSet configs, final int nalts) {
        SemanticContext[] altToPred = new SemanticContext[nalts + 1];
        for (final ATNConfig c : configs) {
            if (ambigAlts.get(c.alt)) {
                altToPred[c.alt] = SemanticContext.or(altToPred[c.alt], c.semanticContext);
            }
        }
        int nPredAlts = 0;
        for (int i = 1; i <= nalts; ++i) {
            if (altToPred[i] == null) {
                altToPred[i] = SemanticContext.NONE;
            }
            else if (altToPred[i] != SemanticContext.NONE) {
                ++nPredAlts;
            }
        }
        if (nPredAlts == 0) {
            altToPred = null;
        }
        return altToPred;
    }
    
    protected DFAState.PredPrediction[] getPredicatePredictions(final BitSet ambigAlts, final SemanticContext[] altToPred) {
        final List<DFAState.PredPrediction> pairs = new ArrayList<DFAState.PredPrediction>();
        boolean containsPredicate = false;
        for (int i = 1; i < altToPred.length; ++i) {
            final SemanticContext pred = altToPred[i];
            assert pred != null;
            if (ambigAlts != null && ambigAlts.get(i)) {
                pairs.add(new DFAState.PredPrediction(pred, i));
            }
            if (pred != SemanticContext.NONE) {
                containsPredicate = true;
            }
        }
        if (!containsPredicate) {
            return null;
        }
        return pairs.toArray(new DFAState.PredPrediction[pairs.size()]);
    }
    
    protected int getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(final ATNConfigSet configs, final ParserRuleContext outerContext) {
        final Pair<ATNConfigSet, ATNConfigSet> sets = this.splitAccordingToSemanticValidity(configs, outerContext);
        final ATNConfigSet semValidConfigs = sets.a;
        final ATNConfigSet semInvalidConfigs = sets.b;
        int alt = this.getAltThatFinishedDecisionEntryRule(semValidConfigs);
        if (alt != 0) {
            return alt;
        }
        if (semInvalidConfigs.size() > 0) {
            alt = this.getAltThatFinishedDecisionEntryRule(semInvalidConfigs);
            if (alt != 0) {
                return alt;
            }
        }
        return 0;
    }
    
    protected int getAltThatFinishedDecisionEntryRule(final ATNConfigSet configs) {
        final IntervalSet alts = new IntervalSet(new int[0]);
        for (final ATNConfig c : configs) {
            if (c.getOuterContextDepth() > 0 || (c.state instanceof RuleStopState && c.context.hasEmptyPath())) {
                alts.add(c.alt);
            }
        }
        if (alts.size() == 0) {
            return 0;
        }
        return alts.getMinElement();
    }
    
    protected Pair<ATNConfigSet, ATNConfigSet> splitAccordingToSemanticValidity(final ATNConfigSet configs, final ParserRuleContext outerContext) {
        final ATNConfigSet succeeded = new ATNConfigSet(configs.fullCtx);
        final ATNConfigSet failed = new ATNConfigSet(configs.fullCtx);
        for (final ATNConfig c : configs) {
            if (c.semanticContext != SemanticContext.NONE) {
                final boolean predicateEvaluationResult = this.evalSemanticContext(c.semanticContext, outerContext, c.alt, configs.fullCtx);
                if (predicateEvaluationResult) {
                    succeeded.add(c);
                }
                else {
                    failed.add(c);
                }
            }
            else {
                succeeded.add(c);
            }
        }
        return new Pair<ATNConfigSet, ATNConfigSet>(succeeded, failed);
    }
    
    protected BitSet evalSemanticContext(final DFAState.PredPrediction[] predPredictions, final ParserRuleContext outerContext, final boolean complete) {
        final BitSet predictions = new BitSet();
        for (final DFAState.PredPrediction pair : predPredictions) {
            if (pair.pred == SemanticContext.NONE) {
                predictions.set(pair.alt);
                if (!complete) {
                    break;
                }
            }
            else {
                final boolean fullCtx = false;
                final boolean predicateEvaluationResult = this.evalSemanticContext(pair.pred, outerContext, pair.alt, fullCtx);
                if (predicateEvaluationResult) {
                    predictions.set(pair.alt);
                    if (!complete) {
                        break;
                    }
                }
            }
        }
        return predictions;
    }
    
    protected boolean evalSemanticContext(final SemanticContext pred, final ParserRuleContext parserCallStack, final int alt, final boolean fullCtx) {
        return pred.eval(this.parser, parserCallStack);
    }
    
    protected void closure(final ATNConfig config, final ATNConfigSet configs, final Set<ATNConfig> closureBusy, final boolean collectPredicates, final boolean fullCtx, final boolean treatEofAsEpsilon) {
        final int initialDepth = 0;
        this.closureCheckingStopState(config, configs, closureBusy, collectPredicates, fullCtx, 0, treatEofAsEpsilon);
        assert !configs.dipsIntoOuterContext;
    }
    
    protected void closureCheckingStopState(final ATNConfig config, final ATNConfigSet configs, final Set<ATNConfig> closureBusy, final boolean collectPredicates, final boolean fullCtx, final int depth, final boolean treatEofAsEpsilon) {
        if (config.state instanceof RuleStopState) {
            if (!config.context.isEmpty()) {
                for (int i = 0; i < config.context.size(); ++i) {
                    if (config.context.getReturnState(i) == Integer.MAX_VALUE) {
                        if (fullCtx) {
                            configs.add(new ATNConfig(config, config.state, PredictionContext.EMPTY), this.mergeCache);
                        }
                        else {
                            this.closure_(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon);
                        }
                    }
                    else {
                        final ATNState returnState = this.atn.states.get(config.context.getReturnState(i));
                        final PredictionContext newContext = config.context.getParent(i);
                        final ATNConfig c = new ATNConfig(returnState, config.alt, newContext, config.semanticContext);
                        c.reachesIntoOuterContext = config.reachesIntoOuterContext;
                        assert depth > Integer.MIN_VALUE;
                        this.closureCheckingStopState(c, configs, closureBusy, collectPredicates, fullCtx, depth - 1, treatEofAsEpsilon);
                    }
                }
                return;
            }
            if (fullCtx) {
                configs.add(config, this.mergeCache);
                return;
            }
        }
        this.closure_(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon);
    }
    
    protected void closure_(final ATNConfig config, final ATNConfigSet configs, final Set<ATNConfig> closureBusy, final boolean collectPredicates, final boolean fullCtx, final int depth, final boolean treatEofAsEpsilon) {
        final ATNState p = config.state;
        if (!p.onlyHasEpsilonTransitions()) {
            configs.add(config, this.mergeCache);
        }
        for (int i = 0; i < p.getNumberOfTransitions(); ++i) {
            final Transition t = p.transition(i);
            final boolean continueCollecting = !(t instanceof ActionTransition) && collectPredicates;
            final ATNConfig c = this.getEpsilonTarget(config, t, continueCollecting, depth == 0, fullCtx, treatEofAsEpsilon);
            if (c != null) {
                if (t.isEpsilon() || closureBusy.add(c)) {
                    int newDepth = depth;
                    if (config.state instanceof RuleStopState) {
                        assert !fullCtx;
                        if (!closureBusy.add(c)) {
                            continue;
                        }
                        if (this._dfa != null && this._dfa.isPrecedenceDfa()) {
                            final int outermostPrecedenceReturn = ((EpsilonTransition)t).outermostPrecedenceReturn();
                            if (outermostPrecedenceReturn == this._dfa.atnStartState.ruleIndex) {
                                c.setPrecedenceFilterSuppressed(true);
                            }
                        }
                        final ATNConfig atnConfig = c;
                        ++atnConfig.reachesIntoOuterContext;
                        configs.dipsIntoOuterContext = true;
                        assert newDepth > Integer.MIN_VALUE;
                        --newDepth;
                    }
                    else if (t instanceof RuleTransition && newDepth >= 0) {
                        ++newDepth;
                    }
                    this.closureCheckingStopState(c, configs, closureBusy, continueCollecting, fullCtx, newDepth, treatEofAsEpsilon);
                }
            }
        }
    }
    
    public String getRuleName(final int index) {
        if (this.parser != null && index >= 0) {
            return this.parser.getRuleNames()[index];
        }
        return "<rule " + index + ">";
    }
    
    protected ATNConfig getEpsilonTarget(final ATNConfig config, final Transition t, final boolean collectPredicates, final boolean inContext, final boolean fullCtx, final boolean treatEofAsEpsilon) {
        switch (t.getSerializationType()) {
            case 3: {
                return this.ruleTransition(config, (RuleTransition)t);
            }
            case 10: {
                return this.precedenceTransition(config, (PrecedencePredicateTransition)t, collectPredicates, inContext, fullCtx);
            }
            case 4: {
                return this.predTransition(config, (PredicateTransition)t, collectPredicates, inContext, fullCtx);
            }
            case 6: {
                return this.actionTransition(config, (ActionTransition)t);
            }
            case 1: {
                return new ATNConfig(config, t.target);
            }
            case 2:
            case 5:
            case 7: {
                if (treatEofAsEpsilon && t.matches(-1, 0, 1)) {
                    return new ATNConfig(config, t.target);
                }
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    protected ATNConfig actionTransition(final ATNConfig config, final ActionTransition t) {
        return new ATNConfig(config, t.target);
    }
    
    public ATNConfig precedenceTransition(final ATNConfig config, final PrecedencePredicateTransition pt, final boolean collectPredicates, final boolean inContext, final boolean fullCtx) {
        ATNConfig c = null;
        if (collectPredicates && inContext) {
            if (fullCtx) {
                final int currentPosition = this._input.index();
                this._input.seek(this._startIndex);
                final boolean predSucceeds = this.evalSemanticContext(pt.getPredicate(), this._outerContext, config.alt, fullCtx);
                this._input.seek(currentPosition);
                if (predSucceeds) {
                    c = new ATNConfig(config, pt.target);
                }
            }
            else {
                final SemanticContext newSemCtx = SemanticContext.and(config.semanticContext, pt.getPredicate());
                c = new ATNConfig(config, pt.target, newSemCtx);
            }
        }
        else {
            c = new ATNConfig(config, pt.target);
        }
        return c;
    }
    
    protected ATNConfig predTransition(final ATNConfig config, final PredicateTransition pt, final boolean collectPredicates, final boolean inContext, final boolean fullCtx) {
        ATNConfig c = null;
        if (collectPredicates && (!pt.isCtxDependent || (pt.isCtxDependent && inContext))) {
            if (fullCtx) {
                final int currentPosition = this._input.index();
                this._input.seek(this._startIndex);
                final boolean predSucceeds = this.evalSemanticContext(pt.getPredicate(), this._outerContext, config.alt, fullCtx);
                this._input.seek(currentPosition);
                if (predSucceeds) {
                    c = new ATNConfig(config, pt.target);
                }
            }
            else {
                final SemanticContext newSemCtx = SemanticContext.and(config.semanticContext, pt.getPredicate());
                c = new ATNConfig(config, pt.target, newSemCtx);
            }
        }
        else {
            c = new ATNConfig(config, pt.target);
        }
        return c;
    }
    
    protected ATNConfig ruleTransition(final ATNConfig config, final RuleTransition t) {
        final ATNState returnState = t.followState;
        final PredictionContext newContext = SingletonPredictionContext.create(config.context, returnState.stateNumber);
        return new ATNConfig(config, t.target, newContext);
    }
    
    protected BitSet getConflictingAlts(final ATNConfigSet configs) {
        final Collection<BitSet> altsets = PredictionMode.getConflictingAltSubsets(configs);
        return PredictionMode.getAlts(altsets);
    }
    
    protected BitSet getConflictingAltsOrUniqueAlt(final ATNConfigSet configs) {
        BitSet conflictingAlts;
        if (configs.uniqueAlt != 0) {
            conflictingAlts = new BitSet();
            conflictingAlts.set(configs.uniqueAlt);
        }
        else {
            conflictingAlts = configs.conflictingAlts;
        }
        return conflictingAlts;
    }
    
    public String getTokenName(final int t) {
        if (t == -1) {
            return "EOF";
        }
        final Vocabulary vocabulary = (this.parser != null) ? this.parser.getVocabulary() : VocabularyImpl.EMPTY_VOCABULARY;
        final String displayName = vocabulary.getDisplayName(t);
        if (displayName.equals(Integer.toString(t))) {
            return displayName;
        }
        return displayName + "<" + t + ">";
    }
    
    public String getLookaheadName(final TokenStream input) {
        return this.getTokenName(input.LA(1));
    }
    
    public void dumpDeadEndConfigs(final NoViableAltException nvae) {
        System.err.println("dead end configs: ");
        for (final ATNConfig c : nvae.getDeadEndConfigs()) {
            String trans = "no edges";
            if (c.state.getNumberOfTransitions() > 0) {
                final Transition t = c.state.transition(0);
                if (t instanceof AtomTransition) {
                    final AtomTransition at = (AtomTransition)t;
                    trans = "Atom " + this.getTokenName(at.label);
                }
                else if (t instanceof SetTransition) {
                    final SetTransition st = (SetTransition)t;
                    final boolean not = st instanceof NotSetTransition;
                    trans = (not ? "~" : "") + "Set " + st.set.toString();
                }
            }
            System.err.println(c.toString(this.parser, true) + ":" + trans);
        }
    }
    
    protected NoViableAltException noViableAlt(final TokenStream input, final ParserRuleContext outerContext, final ATNConfigSet configs, final int startIndex) {
        return new NoViableAltException(this.parser, input, input.get(startIndex), input.LT(1), configs, outerContext);
    }
    
    protected static int getUniqueAlt(final ATNConfigSet configs) {
        int alt = 0;
        for (final ATNConfig c : configs) {
            if (alt == 0) {
                alt = c.alt;
            }
            else {
                if (c.alt != alt) {
                    return 0;
                }
                continue;
            }
        }
        return alt;
    }
    
    protected DFAState addDFAEdge(final DFA dfa, final DFAState from, final int t, DFAState to) {
        if (to == null) {
            return null;
        }
        to = this.addDFAState(dfa, to);
        if (from == null || t < -1 || t > this.atn.maxTokenType) {
            return to;
        }
        synchronized (from) {
            if (from.edges == null) {
                from.edges = new DFAState[this.atn.maxTokenType + 1 + 1];
            }
            from.edges[t + 1] = to;
        }
        return to;
    }
    
    protected DFAState addDFAState(final DFA dfa, final DFAState D) {
        if (D == ParserATNSimulator.ERROR) {
            return D;
        }
        synchronized (dfa.states) {
            final DFAState existing = dfa.states.get(D);
            if (existing != null) {
                return existing;
            }
            D.stateNumber = dfa.states.size();
            if (!D.configs.isReadonly()) {
                D.configs.optimizeConfigs(this);
                D.configs.setReadonly(true);
            }
            dfa.states.put(D, D);
            return D;
        }
    }
    
    protected void reportAttemptingFullContext(final DFA dfa, final BitSet conflictingAlts, final ATNConfigSet configs, final int startIndex, final int stopIndex) {
        if (this.parser != null) {
            this.parser.getErrorListenerDispatch().reportAttemptingFullContext(this.parser, dfa, startIndex, stopIndex, conflictingAlts, configs);
        }
    }
    
    protected void reportContextSensitivity(final DFA dfa, final int prediction, final ATNConfigSet configs, final int startIndex, final int stopIndex) {
        if (this.parser != null) {
            this.parser.getErrorListenerDispatch().reportContextSensitivity(this.parser, dfa, startIndex, stopIndex, prediction, configs);
        }
    }
    
    protected void reportAmbiguity(final DFA dfa, final DFAState D, final int startIndex, final int stopIndex, final boolean exact, final BitSet ambigAlts, final ATNConfigSet configs) {
        if (this.parser != null) {
            this.parser.getErrorListenerDispatch().reportAmbiguity(this.parser, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
        }
    }
    
    public final void setPredictionMode(final PredictionMode mode) {
        this.mode = mode;
    }
    
    public final PredictionMode getPredictionMode() {
        return this.mode;
    }
    
    public Parser getParser() {
        return this.parser;
    }
}
