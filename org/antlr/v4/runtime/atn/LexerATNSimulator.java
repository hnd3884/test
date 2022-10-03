package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.RuleContext;
import java.util.Iterator;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.Lexer;

public class LexerATNSimulator extends ATNSimulator
{
    public static final boolean debug = false;
    public static final boolean dfa_debug = false;
    public static final int MIN_DFA_EDGE = 0;
    public static final int MAX_DFA_EDGE = 127;
    protected final Lexer recog;
    protected int startIndex;
    protected int line;
    protected int charPositionInLine;
    public final DFA[] decisionToDFA;
    protected int mode;
    protected final SimState prevAccept;
    public static int match_calls;
    
    public LexerATNSimulator(final ATN atn, final DFA[] decisionToDFA, final PredictionContextCache sharedContextCache) {
        this(null, atn, decisionToDFA, sharedContextCache);
    }
    
    public LexerATNSimulator(final Lexer recog, final ATN atn, final DFA[] decisionToDFA, final PredictionContextCache sharedContextCache) {
        super(atn, sharedContextCache);
        this.startIndex = -1;
        this.line = 1;
        this.charPositionInLine = 0;
        this.mode = 0;
        this.prevAccept = new SimState();
        this.decisionToDFA = decisionToDFA;
        this.recog = recog;
    }
    
    public void copyState(final LexerATNSimulator simulator) {
        this.charPositionInLine = simulator.charPositionInLine;
        this.line = simulator.line;
        this.mode = simulator.mode;
        this.startIndex = simulator.startIndex;
    }
    
    public int match(final CharStream input, final int mode) {
        ++LexerATNSimulator.match_calls;
        this.mode = mode;
        final int mark = input.mark();
        try {
            this.startIndex = input.index();
            this.prevAccept.reset();
            final DFA dfa = this.decisionToDFA[mode];
            if (dfa.s0 == null) {
                return this.matchATN(input);
            }
            return this.execATN(input, dfa.s0);
        }
        finally {
            input.release(mark);
        }
    }
    
    @Override
    public void reset() {
        this.prevAccept.reset();
        this.startIndex = -1;
        this.line = 1;
        this.charPositionInLine = 0;
        this.mode = 0;
    }
    
    @Override
    public void clearDFA() {
        for (int d = 0; d < this.decisionToDFA.length; ++d) {
            this.decisionToDFA[d] = new DFA(this.atn.getDecisionState(d), d);
        }
    }
    
    protected int matchATN(final CharStream input) {
        final ATNState startState = this.atn.modeToStartState.get(this.mode);
        final int old_mode = this.mode;
        final ATNConfigSet s0_closure = this.computeStartState(input, startState);
        final boolean suppressEdge = s0_closure.hasSemanticContext;
        s0_closure.hasSemanticContext = false;
        final DFAState next = this.addDFAState(s0_closure);
        if (!suppressEdge) {
            this.decisionToDFA[this.mode].s0 = next;
        }
        final int predict = this.execATN(input, next);
        return predict;
    }
    
    protected int execATN(final CharStream input, final DFAState ds0) {
        if (ds0.isAcceptState) {
            this.captureSimState(this.prevAccept, input, ds0);
        }
        int t = input.LA(1);
        DFAState s = ds0;
        while (true) {
            DFAState target = this.getExistingTargetState(s, t);
            if (target == null) {
                target = this.computeTargetState(input, s, t);
            }
            if (target == LexerATNSimulator.ERROR) {
                break;
            }
            if (t != -1) {
                this.consume(input);
            }
            if (target.isAcceptState) {
                this.captureSimState(this.prevAccept, input, target);
                if (t == -1) {
                    break;
                }
            }
            t = input.LA(1);
            s = target;
        }
        return this.failOrAccept(this.prevAccept, input, s.configs, t);
    }
    
    protected DFAState getExistingTargetState(final DFAState s, final int t) {
        if (s.edges == null || t < 0 || t > 127) {
            return null;
        }
        final DFAState target = s.edges[t - 0];
        return target;
    }
    
    protected DFAState computeTargetState(final CharStream input, final DFAState s, final int t) {
        final ATNConfigSet reach = new OrderedATNConfigSet();
        this.getReachableConfigSet(input, s.configs, reach, t);
        if (reach.isEmpty()) {
            if (!reach.hasSemanticContext) {
                this.addDFAEdge(s, t, LexerATNSimulator.ERROR);
            }
            return LexerATNSimulator.ERROR;
        }
        return this.addDFAEdge(s, t, reach);
    }
    
    protected int failOrAccept(final SimState prevAccept, final CharStream input, final ATNConfigSet reach, final int t) {
        if (prevAccept.dfaState != null) {
            final LexerActionExecutor lexerActionExecutor = prevAccept.dfaState.lexerActionExecutor;
            this.accept(input, lexerActionExecutor, this.startIndex, prevAccept.index, prevAccept.line, prevAccept.charPos);
            return prevAccept.dfaState.prediction;
        }
        if (t == -1 && input.index() == this.startIndex) {
            return -1;
        }
        throw new LexerNoViableAltException(this.recog, input, this.startIndex, reach);
    }
    
    protected void getReachableConfigSet(final CharStream input, final ATNConfigSet closure, final ATNConfigSet reach, final int t) {
        int skipAlt = 0;
        for (final ATNConfig c : closure) {
            final boolean currentAltReachedAcceptState = c.alt == skipAlt;
            if (currentAltReachedAcceptState && ((LexerATNConfig)c).hasPassedThroughNonGreedyDecision()) {
                continue;
            }
            for (int n = c.state.getNumberOfTransitions(), ti = 0; ti < n; ++ti) {
                final Transition trans = c.state.transition(ti);
                final ATNState target = this.getReachableTarget(trans, t);
                if (target != null) {
                    LexerActionExecutor lexerActionExecutor = ((LexerATNConfig)c).getLexerActionExecutor();
                    if (lexerActionExecutor != null) {
                        lexerActionExecutor = lexerActionExecutor.fixOffsetBeforeMatch(input.index() - this.startIndex);
                    }
                    final boolean treatEofAsEpsilon = t == -1;
                    if (this.closure(input, new LexerATNConfig((LexerATNConfig)c, target, lexerActionExecutor), reach, currentAltReachedAcceptState, true, treatEofAsEpsilon)) {
                        skipAlt = c.alt;
                        break;
                    }
                }
            }
        }
    }
    
    protected void accept(final CharStream input, final LexerActionExecutor lexerActionExecutor, final int startIndex, final int index, final int line, final int charPos) {
        input.seek(index);
        this.line = line;
        this.charPositionInLine = charPos;
        if (lexerActionExecutor != null && this.recog != null) {
            lexerActionExecutor.execute(this.recog, input, startIndex);
        }
    }
    
    protected ATNState getReachableTarget(final Transition trans, final int t) {
        if (trans.matches(t, 0, 65535)) {
            return trans.target;
        }
        return null;
    }
    
    protected ATNConfigSet computeStartState(final CharStream input, final ATNState p) {
        final PredictionContext initialContext = PredictionContext.EMPTY;
        final ATNConfigSet configs = new OrderedATNConfigSet();
        for (int i = 0; i < p.getNumberOfTransitions(); ++i) {
            final ATNState target = p.transition(i).target;
            final LexerATNConfig c = new LexerATNConfig(target, i + 1, initialContext);
            this.closure(input, c, configs, false, false, false);
        }
        return configs;
    }
    
    protected boolean closure(final CharStream input, final LexerATNConfig config, final ATNConfigSet configs, boolean currentAltReachedAcceptState, final boolean speculative, final boolean treatEofAsEpsilon) {
        if (config.state instanceof RuleStopState) {
            if (config.context == null || config.context.hasEmptyPath()) {
                if (config.context == null || config.context.isEmpty()) {
                    configs.add((ATNConfig)config);
                    return true;
                }
                configs.add((ATNConfig)new LexerATNConfig(config, config.state, PredictionContext.EMPTY));
                currentAltReachedAcceptState = true;
            }
            if (config.context != null && !config.context.isEmpty()) {
                for (int i = 0; i < config.context.size(); ++i) {
                    if (config.context.getReturnState(i) != Integer.MAX_VALUE) {
                        final PredictionContext newContext = config.context.getParent(i);
                        final ATNState returnState = this.atn.states.get(config.context.getReturnState(i));
                        final LexerATNConfig c = new LexerATNConfig(config, returnState, newContext);
                        currentAltReachedAcceptState = this.closure(input, c, configs, currentAltReachedAcceptState, speculative, treatEofAsEpsilon);
                    }
                }
            }
            return currentAltReachedAcceptState;
        }
        if (!config.state.onlyHasEpsilonTransitions() && (!currentAltReachedAcceptState || !config.hasPassedThroughNonGreedyDecision())) {
            configs.add((ATNConfig)config);
        }
        final ATNState p = config.state;
        for (int j = 0; j < p.getNumberOfTransitions(); ++j) {
            final Transition t = p.transition(j);
            final LexerATNConfig c = this.getEpsilonTarget(input, config, t, configs, speculative, treatEofAsEpsilon);
            if (c != null) {
                currentAltReachedAcceptState = this.closure(input, c, configs, currentAltReachedAcceptState, speculative, treatEofAsEpsilon);
            }
        }
        return currentAltReachedAcceptState;
    }
    
    protected LexerATNConfig getEpsilonTarget(final CharStream input, final LexerATNConfig config, final Transition t, final ATNConfigSet configs, final boolean speculative, final boolean treatEofAsEpsilon) {
        LexerATNConfig c = null;
        switch (t.getSerializationType()) {
            case 3: {
                final RuleTransition ruleTransition = (RuleTransition)t;
                final PredictionContext newContext = SingletonPredictionContext.create(config.context, ruleTransition.followState.stateNumber);
                c = new LexerATNConfig(config, t.target, newContext);
                break;
            }
            case 10: {
                throw new UnsupportedOperationException("Precedence predicates are not supported in lexers.");
            }
            case 4: {
                final PredicateTransition pt = (PredicateTransition)t;
                configs.hasSemanticContext = true;
                if (this.evaluatePredicate(input, pt.ruleIndex, pt.predIndex, speculative)) {
                    c = new LexerATNConfig(config, t.target);
                    break;
                }
                break;
            }
            case 6: {
                if (config.context == null || config.context.hasEmptyPath()) {
                    final LexerActionExecutor lexerActionExecutor = LexerActionExecutor.append(config.getLexerActionExecutor(), this.atn.lexerActions[((ActionTransition)t).actionIndex]);
                    c = new LexerATNConfig(config, t.target, lexerActionExecutor);
                    break;
                }
                c = new LexerATNConfig(config, t.target);
                break;
            }
            case 1: {
                c = new LexerATNConfig(config, t.target);
                break;
            }
            case 2:
            case 5:
            case 7: {
                if (treatEofAsEpsilon && t.matches(-1, 0, 65535)) {
                    c = new LexerATNConfig(config, t.target);
                    break;
                }
                break;
            }
        }
        return c;
    }
    
    protected boolean evaluatePredicate(final CharStream input, final int ruleIndex, final int predIndex, final boolean speculative) {
        if (this.recog == null) {
            return true;
        }
        if (!speculative) {
            return this.recog.sempred(null, ruleIndex, predIndex);
        }
        final int savedCharPositionInLine = this.charPositionInLine;
        final int savedLine = this.line;
        final int index = input.index();
        final int marker = input.mark();
        try {
            this.consume(input);
            return this.recog.sempred(null, ruleIndex, predIndex);
        }
        finally {
            this.charPositionInLine = savedCharPositionInLine;
            this.line = savedLine;
            input.seek(index);
            input.release(marker);
        }
    }
    
    protected void captureSimState(final SimState settings, final CharStream input, final DFAState dfaState) {
        settings.index = input.index();
        settings.line = this.line;
        settings.charPos = this.charPositionInLine;
        settings.dfaState = dfaState;
    }
    
    protected DFAState addDFAEdge(final DFAState from, final int t, final ATNConfigSet q) {
        final boolean suppressEdge = q.hasSemanticContext;
        q.hasSemanticContext = false;
        final DFAState to = this.addDFAState(q);
        if (suppressEdge) {
            return to;
        }
        this.addDFAEdge(from, t, to);
        return to;
    }
    
    protected void addDFAEdge(final DFAState p, final int t, final DFAState q) {
        if (t < 0 || t > 127) {
            return;
        }
        synchronized (p) {
            if (p.edges == null) {
                p.edges = new DFAState[128];
            }
            p.edges[t - 0] = q;
        }
    }
    
    protected DFAState addDFAState(final ATNConfigSet configs) {
        assert !configs.hasSemanticContext;
        final DFAState proposed = new DFAState(configs);
        ATNConfig firstConfigWithRuleStopState = null;
        for (final ATNConfig c : configs) {
            if (c.state instanceof RuleStopState) {
                firstConfigWithRuleStopState = c;
                break;
            }
        }
        if (firstConfigWithRuleStopState != null) {
            proposed.isAcceptState = true;
            proposed.lexerActionExecutor = ((LexerATNConfig)firstConfigWithRuleStopState).getLexerActionExecutor();
            proposed.prediction = this.atn.ruleToTokenType[firstConfigWithRuleStopState.state.ruleIndex];
        }
        final DFA dfa = this.decisionToDFA[this.mode];
        synchronized (dfa.states) {
            final DFAState existing = dfa.states.get(proposed);
            if (existing != null) {
                return existing;
            }
            final DFAState newState = proposed;
            newState.stateNumber = dfa.states.size();
            configs.setReadonly(true);
            newState.configs = configs;
            dfa.states.put(newState, newState);
            return newState;
        }
    }
    
    public final DFA getDFA(final int mode) {
        return this.decisionToDFA[mode];
    }
    
    public String getText(final CharStream input) {
        return input.getText(Interval.of(this.startIndex, input.index() - 1));
    }
    
    public int getLine() {
        return this.line;
    }
    
    public void setLine(final int line) {
        this.line = line;
    }
    
    public int getCharPositionInLine() {
        return this.charPositionInLine;
    }
    
    public void setCharPositionInLine(final int charPositionInLine) {
        this.charPositionInLine = charPositionInLine;
    }
    
    public void consume(final CharStream input) {
        final int curChar = input.LA(1);
        if (curChar == 10) {
            ++this.line;
            this.charPositionInLine = 0;
        }
        else {
            ++this.charPositionInLine;
        }
        input.consume();
    }
    
    public String getTokenName(final int t) {
        if (t == -1) {
            return "EOF";
        }
        return "'" + (char)t + "'";
    }
    
    static {
        LexerATNSimulator.match_calls = 0;
    }
    
    protected static class SimState
    {
        protected int index;
        protected int line;
        protected int charPos;
        protected DFAState dfaState;
        
        protected SimState() {
            this.index = -1;
            this.line = 0;
            this.charPos = -1;
        }
        
        protected void reset() {
            this.index = -1;
            this.line = 0;
            this.charPos = -1;
            this.dfaState = null;
        }
    }
}
