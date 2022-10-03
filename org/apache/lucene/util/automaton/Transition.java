package org.apache.lucene.util.automaton;

public class Transition
{
    public int source;
    public int dest;
    public int min;
    public int max;
    int transitionUpto;
    
    public Transition() {
        this.transitionUpto = -1;
    }
    
    @Override
    public String toString() {
        return this.source + " --> " + this.dest + " " + (char)this.min + "-" + (char)this.max;
    }
}
