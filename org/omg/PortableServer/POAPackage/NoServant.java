package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class NoServant extends UserException
{
    public NoServant() {
        super(NoServantHelper.id());
    }
    
    public NoServant(final String s) {
        super(NoServantHelper.id() + "  " + s);
    }
}
