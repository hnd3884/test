package com.sun.corba.se.impl.util;

import org.omg.CORBA.Object;

class StubEntry
{
    org.omg.CORBA.Object stub;
    boolean mostDerived;
    
    StubEntry(final org.omg.CORBA.Object stub, final boolean mostDerived) {
        this.stub = stub;
        this.mostDerived = mostDerived;
    }
}
