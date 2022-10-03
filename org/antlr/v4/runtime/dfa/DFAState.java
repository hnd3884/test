package org.antlr.v4.runtime.dfa;

import org.antlr.v4.runtime.atn.SemanticContext;
import java.util.Arrays;
import org.antlr.v4.runtime.misc.MurmurHash;
import java.util.Iterator;
import org.antlr.v4.runtime.atn.ATNConfig;
import java.util.HashSet;
import java.util.Set;
import org.antlr.v4.runtime.atn.LexerActionExecutor;
import org.antlr.v4.runtime.atn.ATNConfigSet;

public class DFAState
{
    public int stateNumber;
    public ATNConfigSet configs;
    public DFAState[] edges;
    public boolean isAcceptState;
    public int prediction;
    public LexerActionExecutor lexerActionExecutor;
    public boolean requiresFullContext;
    public PredPrediction[] predicates;
    
    public DFAState() {
        this.stateNumber = -1;
        this.configs = new ATNConfigSet();
        this.isAcceptState = false;
    }
    
    public DFAState(final int stateNumber) {
        this.stateNumber = -1;
        this.configs = new ATNConfigSet();
        this.isAcceptState = false;
        this.stateNumber = stateNumber;
    }
    
    public DFAState(final ATNConfigSet configs) {
        this.stateNumber = -1;
        this.configs = new ATNConfigSet();
        this.isAcceptState = false;
        this.configs = configs;
    }
    
    public Set<Integer> getAltSet() {
        final Set<Integer> alts = new HashSet<Integer>();
        if (this.configs != null) {
            for (final ATNConfig c : this.configs) {
                alts.add(c.alt);
            }
        }
        if (alts.isEmpty()) {
            return null;
        }
        return alts;
    }
    
    @Override
    public int hashCode() {
        int hash = MurmurHash.initialize(7);
        hash = MurmurHash.update(hash, this.configs.hashCode());
        hash = MurmurHash.finish(hash, 1);
        return hash;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DFAState)) {
            return false;
        }
        final DFAState other = (DFAState)o;
        final boolean sameSet = this.configs.equals(other.configs);
        return sameSet;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.stateNumber).append(":").append(this.configs);
        if (this.isAcceptState) {
            buf.append("=>");
            if (this.predicates != null) {
                buf.append(Arrays.toString(this.predicates));
            }
            else {
                buf.append(this.prediction);
            }
        }
        return buf.toString();
    }
    
    public static class PredPrediction
    {
        public SemanticContext pred;
        public int alt;
        
        public PredPrediction(final SemanticContext pred, final int alt) {
            this.alt = alt;
            this.pred = pred;
        }
        
        @Override
        public String toString() {
            return "(" + this.pred + ", " + this.alt + ")";
        }
    }
}
