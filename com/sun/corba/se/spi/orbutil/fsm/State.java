package com.sun.corba.se.spi.orbutil.fsm;

public interface State
{
    void preAction(final FSM p0);
    
    void postAction(final FSM p0);
}
