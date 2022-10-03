package com.sun.corba.se.spi.orbutil.fsm;

class NegateGuard implements Guard
{
    Guard guard;
    
    public NegateGuard(final Guard guard) {
        this.guard = guard;
    }
    
    @Override
    public Result evaluate(final FSM fsm, final Input input) {
        return this.guard.evaluate(fsm, input).complement();
    }
}
