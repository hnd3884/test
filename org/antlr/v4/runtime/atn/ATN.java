package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntSet;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.RuleContext;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class ATN
{
    public static final int INVALID_ALT_NUMBER = 0;
    public final List<ATNState> states;
    public final List<DecisionState> decisionToState;
    public RuleStartState[] ruleToStartState;
    public RuleStopState[] ruleToStopState;
    public final Map<String, TokensStartState> modeNameToStartState;
    public final ATNType grammarType;
    public final int maxTokenType;
    public int[] ruleToTokenType;
    public LexerAction[] lexerActions;
    public final List<TokensStartState> modeToStartState;
    
    public ATN(final ATNType grammarType, final int maxTokenType) {
        this.states = new ArrayList<ATNState>();
        this.decisionToState = new ArrayList<DecisionState>();
        this.modeNameToStartState = new LinkedHashMap<String, TokensStartState>();
        this.modeToStartState = new ArrayList<TokensStartState>();
        this.grammarType = grammarType;
        this.maxTokenType = maxTokenType;
    }
    
    public IntervalSet nextTokens(final ATNState s, final RuleContext ctx) {
        final LL1Analyzer anal = new LL1Analyzer(this);
        final IntervalSet next = anal.LOOK(s, ctx);
        return next;
    }
    
    public IntervalSet nextTokens(final ATNState s) {
        if (s.nextTokenWithinRule != null) {
            return s.nextTokenWithinRule;
        }
        (s.nextTokenWithinRule = this.nextTokens(s, null)).setReadonly(true);
        return s.nextTokenWithinRule;
    }
    
    public void addState(final ATNState state) {
        if (state != null) {
            state.atn = this;
            state.stateNumber = this.states.size();
        }
        this.states.add(state);
    }
    
    public void removeState(final ATNState state) {
        this.states.set(state.stateNumber, null);
    }
    
    public int defineDecisionState(final DecisionState s) {
        this.decisionToState.add(s);
        return s.decision = this.decisionToState.size() - 1;
    }
    
    public DecisionState getDecisionState(final int decision) {
        if (!this.decisionToState.isEmpty()) {
            return this.decisionToState.get(decision);
        }
        return null;
    }
    
    public int getNumberOfDecisions() {
        return this.decisionToState.size();
    }
    
    public IntervalSet getExpectedTokens(final int stateNumber, final RuleContext context) {
        if (stateNumber < 0 || stateNumber >= this.states.size()) {
            throw new IllegalArgumentException("Invalid state number.");
        }
        RuleContext ctx = context;
        final ATNState s = this.states.get(stateNumber);
        IntervalSet following = this.nextTokens(s);
        if (!following.contains(-2)) {
            return following;
        }
        final IntervalSet expected = new IntervalSet(new int[0]);
        expected.addAll((IntSet)following);
        expected.remove(-2);
        while (ctx != null && ctx.invokingState >= 0 && following.contains(-2)) {
            final ATNState invokingState = this.states.get(ctx.invokingState);
            final RuleTransition rt = (RuleTransition)invokingState.transition(0);
            following = this.nextTokens(rt.followState);
            expected.addAll((IntSet)following);
            expected.remove(-2);
            ctx = ctx.parent;
        }
        if (following.contains(-2)) {
            expected.add(-1);
        }
        return expected;
    }
}
