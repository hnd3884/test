package com.sun.corba.se.spi.orbutil.fsm;

class TestAction1 implements Action
{
    private State oldState;
    private Input label;
    private State newState;
    
    @Override
    public void doIt(final FSM fsm, final Input input) {
        System.out.println("TestAction1:");
        System.out.println("\tlabel    = " + this.label);
        System.out.println("\toldState = " + this.oldState);
        System.out.println("\tnewState = " + this.newState);
        if (this.label != input) {
            throw new Error("Unexcepted Input " + input);
        }
        if (this.oldState != fsm.getState()) {
            throw new Error("Unexpected old State " + fsm.getState());
        }
    }
    
    public TestAction1(final State oldState, final Input label, final State newState) {
        this.oldState = oldState;
        this.newState = newState;
        this.label = label;
    }
}
