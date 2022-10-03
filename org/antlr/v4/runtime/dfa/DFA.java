package org.antlr.v4.runtime.dfa;

import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.StarLoopEntryState;
import java.util.HashMap;
import org.antlr.v4.runtime.atn.DecisionState;
import java.util.Map;

public class DFA
{
    public final Map<DFAState, DFAState> states;
    public volatile DFAState s0;
    public final int decision;
    public final DecisionState atnStartState;
    private final boolean precedenceDfa;
    
    public DFA(final DecisionState atnStartState) {
        this(atnStartState, 0);
    }
    
    public DFA(final DecisionState atnStartState, final int decision) {
        this.states = new HashMap<DFAState, DFAState>();
        this.atnStartState = atnStartState;
        this.decision = decision;
        boolean precedenceDfa = false;
        if (atnStartState instanceof StarLoopEntryState && ((StarLoopEntryState)atnStartState).isPrecedenceDecision) {
            precedenceDfa = true;
            final DFAState precedenceState = new DFAState(new ATNConfigSet());
            precedenceState.edges = new DFAState[0];
            precedenceState.isAcceptState = false;
            precedenceState.requiresFullContext = false;
            this.s0 = precedenceState;
        }
        this.precedenceDfa = precedenceDfa;
    }
    
    public final boolean isPrecedenceDfa() {
        return this.precedenceDfa;
    }
    
    public final DFAState getPrecedenceStartState(final int precedence) {
        if (!this.isPrecedenceDfa()) {
            throw new IllegalStateException("Only precedence DFAs may contain a precedence start state.");
        }
        if (precedence < 0 || precedence >= this.s0.edges.length) {
            return null;
        }
        return this.s0.edges[precedence];
    }
    
    public final void setPrecedenceStartState(final int precedence, final DFAState startState) {
        if (!this.isPrecedenceDfa()) {
            throw new IllegalStateException("Only precedence DFAs may contain a precedence start state.");
        }
        if (precedence < 0) {
            return;
        }
        synchronized (this.s0) {
            if (precedence >= this.s0.edges.length) {
                this.s0.edges = Arrays.copyOf(this.s0.edges, precedence + 1);
            }
            this.s0.edges[precedence] = startState;
        }
    }
    
    @Deprecated
    public final void setPrecedenceDfa(final boolean precedenceDfa) {
        if (precedenceDfa != this.isPrecedenceDfa()) {
            throw new UnsupportedOperationException("The precedenceDfa field cannot change after a DFA is constructed.");
        }
    }
    
    public List<DFAState> getStates() {
        final List<DFAState> result = new ArrayList<DFAState>(this.states.keySet());
        Collections.sort(result, new Comparator<DFAState>() {
            @Override
            public int compare(final DFAState o1, final DFAState o2) {
                return o1.stateNumber - o2.stateNumber;
            }
        });
        return result;
    }
    
    @Override
    public String toString() {
        return this.toString(VocabularyImpl.EMPTY_VOCABULARY);
    }
    
    @Deprecated
    public String toString(final String[] tokenNames) {
        if (this.s0 == null) {
            return "";
        }
        final DFASerializer serializer = new DFASerializer(this, tokenNames);
        return serializer.toString();
    }
    
    public String toString(final Vocabulary vocabulary) {
        if (this.s0 == null) {
            return "";
        }
        final DFASerializer serializer = new DFASerializer(this, vocabulary);
        return serializer.toString();
    }
    
    public String toLexerString() {
        if (this.s0 == null) {
            return "";
        }
        final DFASerializer serializer = new LexerDFASerializer(this);
        return serializer.toString();
    }
}
