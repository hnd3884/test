package org.antlr.v4.runtime.atn;

public final class BlockEndState extends ATNState
{
    public BlockStartState startState;
    
    @Override
    public int getStateType() {
        return 8;
    }
}
