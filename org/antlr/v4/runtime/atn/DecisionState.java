package org.antlr.v4.runtime.atn;

public abstract class DecisionState extends ATNState
{
    public int decision;
    public boolean nonGreedy;
    
    public DecisionState() {
        this.decision = -1;
    }
}
