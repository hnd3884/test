package com.sun.corba.se.spi.orbutil.fsm;

class MyFSM extends FSMImpl
{
    public int counter;
    
    public MyFSM(final StateEngine stateEngine) {
        super(stateEngine, FSMTest.STATE1);
        this.counter = 0;
    }
}
