package org.omg.PortableInterceptor.ORBInitInfoPackage;

import org.omg.CORBA.UserException;

public final class InvalidName extends UserException
{
    public InvalidName() {
        super(InvalidNameHelper.id());
    }
    
    public InvalidName(final String s) {
        super(InvalidNameHelper.id() + "  " + s);
    }
}
