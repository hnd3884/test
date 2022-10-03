package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class AdapterAlreadyExists extends UserException
{
    public AdapterAlreadyExists() {
        super(AdapterAlreadyExistsHelper.id());
    }
    
    public AdapterAlreadyExists(final String s) {
        super(AdapterAlreadyExistsHelper.id() + "  " + s);
    }
}
