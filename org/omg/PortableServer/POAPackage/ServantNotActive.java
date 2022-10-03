package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ServantNotActive extends UserException
{
    public ServantNotActive() {
        super(ServantNotActiveHelper.id());
    }
    
    public ServantNotActive(final String s) {
        super(ServantNotActiveHelper.id() + "  " + s);
    }
}
