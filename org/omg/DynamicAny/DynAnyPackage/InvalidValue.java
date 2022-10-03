package org.omg.DynamicAny.DynAnyPackage;

import org.omg.CORBA.UserException;

public final class InvalidValue extends UserException
{
    public InvalidValue() {
        super(InvalidValueHelper.id());
    }
    
    public InvalidValue(final String s) {
        super(InvalidValueHelper.id() + "  " + s);
    }
}
