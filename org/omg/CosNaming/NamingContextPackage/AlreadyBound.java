package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;

public final class AlreadyBound extends UserException
{
    public AlreadyBound() {
        super(AlreadyBoundHelper.id());
    }
    
    public AlreadyBound(final String s) {
        super(AlreadyBoundHelper.id() + "  " + s);
    }
}
