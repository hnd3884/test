package com.sun.corba.se.spi.activation.InitialNameServicePackage;

import org.omg.CORBA.UserException;

public final class NameAlreadyBound extends UserException
{
    public NameAlreadyBound() {
        super(NameAlreadyBoundHelper.id());
    }
    
    public NameAlreadyBound(final String s) {
        super(NameAlreadyBoundHelper.id() + "  " + s);
    }
}
