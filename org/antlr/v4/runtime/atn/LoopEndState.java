package org.antlr.v4.runtime.atn;

public final class LoopEndState extends ATNState
{
    public ATNState loopBackState;
    
    @Override
    public int getStateType() {
        return 12;
    }
}
