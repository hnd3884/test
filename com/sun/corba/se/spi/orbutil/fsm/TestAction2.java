package com.sun.corba.se.spi.orbutil.fsm;

class TestAction2 implements Action
{
    private State oldState;
    private State newState;
    
    @Override
    public void doIt(final FSM fsm, final Input input) {
        System.out.println("TestAction2:");
        System.out.println("\toldState = " + this.oldState);
        System.out.println("\tnewState = " + this.newState);
        System.out.println("\tinput    = " + input);
        if (this.oldState != fsm.getState()) {
            throw new Error("Unexpected old State " + fsm.getState());
        }
    }
    
    public TestAction2(final State oldState, final State newState) {
        this.oldState = oldState;
        this.newState = newState;
    }
}
