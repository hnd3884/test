package com.sun.corba.se.spi.orbutil.fsm;

class TestAction3 implements Action
{
    private State oldState;
    private Input label;
    
    @Override
    public void doIt(final FSM fsm, final Input input) {
        System.out.println("TestAction1:");
        System.out.println("\tlabel    = " + this.label);
        System.out.println("\toldState = " + this.oldState);
        if (this.label != input) {
            throw new Error("Unexcepted Input " + input);
        }
    }
    
    public TestAction3(final State oldState, final Input label) {
        this.oldState = oldState;
        this.label = label;
    }
}
