package org.omg.CORBA.DynAnyPackage;

import org.omg.CORBA.UserException;

public final class InvalidValue extends UserException
{
    public InvalidValue() {
    }
    
    public InvalidValue(final String s) {
        super(s);
    }
}
