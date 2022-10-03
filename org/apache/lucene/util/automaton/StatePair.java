package org.apache.lucene.util.automaton;

public class StatePair
{
    int s;
    int s1;
    int s2;
    
    StatePair(final int s, final int s1, final int s2) {
        this.s = s;
        this.s1 = s1;
        this.s2 = s2;
    }
    
    public StatePair(final int s1, final int s2) {
        this.s1 = s1;
        this.s2 = s2;
        this.s = -1;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof StatePair) {
            final StatePair p = (StatePair)obj;
            return p.s1 == this.s1 && p.s2 == this.s2;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.s1 ^ this.s2;
    }
    
    @Override
    public String toString() {
        return "StatePair(s1=" + this.s1 + " s2=" + this.s2 + ")";
    }
}
