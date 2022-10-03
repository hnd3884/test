package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class WrongPolicy extends UserException
{
    public WrongPolicy() {
        super(WrongPolicyHelper.id());
    }
    
    public WrongPolicy(final String s) {
        super(WrongPolicyHelper.id() + "  " + s);
    }
}
