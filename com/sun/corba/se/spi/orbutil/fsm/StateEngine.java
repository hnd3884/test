package com.sun.corba.se.spi.orbutil.fsm;

public interface StateEngine
{
    StateEngine add(final State p0, final Input p1, final Guard p2, final Action p3, final State p4) throws IllegalStateException;
    
    StateEngine add(final State p0, final Input p1, final Action p2, final State p3) throws IllegalStateException;
    
    StateEngine setDefault(final State p0, final Action p1, final State p2) throws IllegalStateException;
    
    StateEngine setDefault(final State p0, final State p1) throws IllegalStateException;
    
    StateEngine setDefault(final State p0) throws IllegalStateException;
    
    void setDefaultAction(final Action p0) throws IllegalStateException;
    
    void done() throws IllegalStateException;
    
    FSM makeFSM(final State p0) throws IllegalStateException;
}
