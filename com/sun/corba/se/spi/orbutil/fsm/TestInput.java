package com.sun.corba.se.spi.orbutil.fsm;

class TestInput
{
    Input value;
    String msg;
    
    TestInput(final Input value, final String msg) {
        this.value = value;
        this.msg = msg;
    }
    
    @Override
    public String toString() {
        return "Input " + this.value + " : " + this.msg;
    }
    
    public Input getInput() {
        return this.value;
    }
}
