package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntSet;
import org.antlr.v4.runtime.RuleContext;
import java.util.Set;
import java.util.BitSet;
import java.util.HashSet;
import org.antlr.v4.runtime.misc.IntervalSet;

public class LL1Analyzer
{
    public static final int HIT_PRED = 0;
    public final ATN atn;
    
    public LL1Analyzer(final ATN atn) {
        this.atn = atn;
    }
    
    public IntervalSet[] getDecisionLookahead(final ATNState s) {
        if (s == null) {
            return null;
        }
        final IntervalSet[] look = new IntervalSet[s.getNumberOfTransitions()];
        for (int alt = 0; alt < s.getNumberOfTransitions(); ++alt) {
            look[alt] = new IntervalSet(new int[0]);
            final Set<ATNConfig> lookBusy = new HashSet<ATNConfig>();
            final boolean seeThruPreds = false;
            this._LOOK(s.transition(alt).target, null, PredictionContext.EMPTY, look[alt], lookBusy, new BitSet(), seeThruPreds, false);
            if (look[alt].size() == 0 || look[alt].contains(0)) {
                look[alt] = null;
            }
        }
        return look;
    }
    
    public IntervalSet LOOK(final ATNState s, final RuleContext ctx) {
        return this.LOOK(s, null, ctx);
    }
    
    public IntervalSet LOOK(final ATNState s, final ATNState stopState, final RuleContext ctx) {
        final IntervalSet r = new IntervalSet(new int[0]);
        final boolean seeThruPreds = true;
        final PredictionContext lookContext = (ctx != null) ? PredictionContext.fromRuleContext(s.atn, ctx) : null;
        this._LOOK(s, stopState, lookContext, r, new HashSet<ATNConfig>(), new BitSet(), seeThruPreds, true);
        return r;
    }
    
    protected void _LOOK(final ATNState s, final ATNState stopState, final PredictionContext ctx, final IntervalSet look, final Set<ATNConfig> lookBusy, final BitSet calledRuleStack, final boolean seeThruPreds, final boolean addEOF) {
        final ATNConfig c = new ATNConfig(s, 0, ctx);
        if (!lookBusy.add(c)) {
            return;
        }
        if (s == stopState) {
            if (ctx == null) {
                look.add(-2);
                return;
            }
            if (ctx.isEmpty() && addEOF) {
                look.add(-1);
                return;
            }
        }
        if (s instanceof RuleStopState) {
            if (ctx == null) {
                look.add(-2);
                return;
            }
            if (ctx.isEmpty() && addEOF) {
                look.add(-1);
                return;
            }
            if (ctx != PredictionContext.EMPTY) {
                for (int i = 0; i < ctx.size(); ++i) {
                    final ATNState returnState = this.atn.states.get(ctx.getReturnState(i));
                    final boolean removed = calledRuleStack.get(returnState.ruleIndex);
                    try {
                        calledRuleStack.clear(returnState.ruleIndex);
                        this._LOOK(returnState, stopState, ctx.getParent(i), look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                    }
                    finally {
                        if (removed) {
                            calledRuleStack.set(returnState.ruleIndex);
                        }
                    }
                }
                return;
            }
        }
        for (int n = s.getNumberOfTransitions(), j = 0; j < n; ++j) {
            final Transition t = s.transition(j);
            if (t.getClass() == RuleTransition.class) {
                if (!calledRuleStack.get(((RuleTransition)t).target.ruleIndex)) {
                    final PredictionContext newContext = SingletonPredictionContext.create(ctx, ((RuleTransition)t).followState.stateNumber);
                    try {
                        calledRuleStack.set(((RuleTransition)t).target.ruleIndex);
                        this._LOOK(t.target, stopState, newContext, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                    }
                    finally {
                        calledRuleStack.clear(((RuleTransition)t).target.ruleIndex);
                    }
                }
            }
            else if (t instanceof AbstractPredicateTransition) {
                if (seeThruPreds) {
                    this._LOOK(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
                }
                else {
                    look.add(0);
                }
            }
            else if (t.isEpsilon()) {
                this._LOOK(t.target, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF);
            }
            else if (t.getClass() == WildcardTransition.class) {
                look.addAll((IntSet)IntervalSet.of(1, this.atn.maxTokenType));
            }
            else {
                IntervalSet set = t.label();
                if (set != null) {
                    if (t instanceof NotSetTransition) {
                        set = set.complement((IntSet)IntervalSet.of(1, this.atn.maxTokenType));
                    }
                    look.addAll((IntSet)set);
                }
            }
        }
    }
}
