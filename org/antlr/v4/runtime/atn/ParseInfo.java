package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.dfa.DFA;
import java.util.ArrayList;
import java.util.List;

public class ParseInfo
{
    protected final ProfilingATNSimulator atnSimulator;
    
    public ParseInfo(final ProfilingATNSimulator atnSimulator) {
        this.atnSimulator = atnSimulator;
    }
    
    public DecisionInfo[] getDecisionInfo() {
        return this.atnSimulator.getDecisionInfo();
    }
    
    public List<Integer> getLLDecisions() {
        final DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        final List<Integer> LL = new ArrayList<Integer>();
        for (int i = 0; i < decisions.length; ++i) {
            final long fallBack = decisions[i].LL_Fallback;
            if (fallBack > 0L) {
                LL.add(i);
            }
        }
        return LL;
    }
    
    public long getTotalTimeInPrediction() {
        final DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        long t = 0L;
        for (int i = 0; i < decisions.length; ++i) {
            t += decisions[i].timeInPrediction;
        }
        return t;
    }
    
    public long getTotalSLLLookaheadOps() {
        final DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        long k = 0L;
        for (int i = 0; i < decisions.length; ++i) {
            k += decisions[i].SLL_TotalLook;
        }
        return k;
    }
    
    public long getTotalLLLookaheadOps() {
        final DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        long k = 0L;
        for (int i = 0; i < decisions.length; ++i) {
            k += decisions[i].LL_TotalLook;
        }
        return k;
    }
    
    public long getTotalSLLATNLookaheadOps() {
        final DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        long k = 0L;
        for (int i = 0; i < decisions.length; ++i) {
            k += decisions[i].SLL_ATNTransitions;
        }
        return k;
    }
    
    public long getTotalLLATNLookaheadOps() {
        final DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        long k = 0L;
        for (int i = 0; i < decisions.length; ++i) {
            k += decisions[i].LL_ATNTransitions;
        }
        return k;
    }
    
    public long getTotalATNLookaheadOps() {
        final DecisionInfo[] decisions = this.atnSimulator.getDecisionInfo();
        long k = 0L;
        for (int i = 0; i < decisions.length; ++i) {
            k += decisions[i].SLL_ATNTransitions;
            k += decisions[i].LL_ATNTransitions;
        }
        return k;
    }
    
    public int getDFASize() {
        int n = 0;
        final DFA[] decisionToDFA = this.atnSimulator.decisionToDFA;
        for (int i = 0; i < decisionToDFA.length; ++i) {
            n += this.getDFASize(i);
        }
        return n;
    }
    
    public int getDFASize(final int decision) {
        final DFA decisionToDFA = this.atnSimulator.decisionToDFA[decision];
        return decisionToDFA.states.size();
    }
}
