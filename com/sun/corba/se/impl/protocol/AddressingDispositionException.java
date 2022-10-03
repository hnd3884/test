package com.sun.corba.se.impl.protocol;

public class AddressingDispositionException extends RuntimeException
{
    private short expectedAddrDisp;
    
    public AddressingDispositionException(final short expectedAddrDisp) {
        this.expectedAddrDisp = 0;
        this.expectedAddrDisp = expectedAddrDisp;
    }
    
    public short expectedAddrDisp() {
        return this.expectedAddrDisp;
    }
}
