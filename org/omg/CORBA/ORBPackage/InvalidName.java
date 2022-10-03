package org.omg.CORBA.ORBPackage;

import org.omg.CORBA.UserException;

public final class InvalidName extends UserException
{
    public InvalidName() {
    }
    
    public InvalidName(final String s) {
        super(s);
    }
}
