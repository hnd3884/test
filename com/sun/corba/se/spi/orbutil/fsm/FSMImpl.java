package com.sun.corba.se.spi.orbutil.fsm;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.fsm.StateEngineImpl;

public class FSMImpl implements FSM
{
    private boolean debug;
    private State state;
    private StateEngineImpl stateEngine;
    
    public FSMImpl(final StateEngine stateEngine, final State state) {
        this(stateEngine, state, false);
    }
    
    public FSMImpl(final StateEngine stateEngine, final State state, final boolean debug) {
        this.state = state;
        this.stateEngine = (StateEngineImpl)stateEngine;
        this.debug = debug;
    }
    
    @Override
    public State getState() {
        return this.state;
    }
    
    @Override
    public void doIt(final Input input) {
        this.stateEngine.doIt(this, input, this.debug);
    }
    
    public void internalSetState(final State state) {
        if (this.debug) {
            ORBUtility.dprint(this, "Calling internalSetState with nextState = " + state);
        }
        this.state = state;
        if (this.debug) {
            ORBUtility.dprint(this, "Exiting internalSetState with state = " + this.state);
        }
    }
}
