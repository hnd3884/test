package org.omg.CORBA.TypeCodePackage;

import org.omg.CORBA.UserException;

public final class BadKind extends UserException
{
    public BadKind() {
    }
    
    public BadKind(final String s) {
        super(s);
    }
}
