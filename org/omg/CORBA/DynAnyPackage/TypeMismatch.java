package org.omg.CORBA.DynAnyPackage;

import org.omg.CORBA.UserException;

public final class TypeMismatch extends UserException
{
    public TypeMismatch() {
    }
    
    public TypeMismatch(final String s) {
        super(s);
    }
}
