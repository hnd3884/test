package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ServantAlreadyActive extends UserException
{
    public ServantAlreadyActive() {
        super(ServantAlreadyActiveHelper.id());
    }
    
    public ServantAlreadyActive(final String s) {
        super(ServantAlreadyActiveHelper.id() + "  " + s);
    }
}
