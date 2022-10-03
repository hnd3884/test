package org.antlr.v4.runtime.dfa;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.Vocabulary;

public class DFASerializer
{
    private final DFA dfa;
    private final Vocabulary vocabulary;
    
    @Deprecated
    public DFASerializer(final DFA dfa, final String[] tokenNames) {
        this(dfa, VocabularyImpl.fromTokenNames(tokenNames));
    }
    
    public DFASerializer(final DFA dfa, final Vocabulary vocabulary) {
        this.dfa = dfa;
        this.vocabulary = vocabulary;
    }
    
    @Override
    public String toString() {
        if (this.dfa.s0 == null) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        final List<DFAState> states = this.dfa.getStates();
        for (final DFAState s : states) {
            int n = 0;
            if (s.edges != null) {
                n = s.edges.length;
            }
            for (int i = 0; i < n; ++i) {
                final DFAState t = s.edges[i];
                if (t != null && t.stateNumber != Integer.MAX_VALUE) {
                    buf.append(this.getStateString(s));
                    final String label = this.getEdgeLabel(i);
                    buf.append("-").append(label).append("->").append(this.getStateString(t)).append('\n');
                }
            }
        }
        final String output = buf.toString();
        if (output.length() == 0) {
            return null;
        }
        return output;
    }
    
    protected String getEdgeLabel(final int i) {
        return this.vocabulary.getDisplayName(i - 1);
    }
    
    protected String getStateString(final DFAState s) {
        final int n = s.stateNumber;
        final String baseStateStr = (s.isAcceptState ? ":" : "") + "s" + n + (s.requiresFullContext ? "^" : "");
        if (!s.isAcceptState) {
            return baseStateStr;
        }
        if (s.predicates != null) {
            return baseStateStr + "=>" + Arrays.toString(s.predicates);
        }
        return baseStateStr + "=>" + s.prediction;
    }
}
