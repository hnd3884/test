package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class AdapterNonExistent extends UserException
{
    public AdapterNonExistent() {
        super(AdapterNonExistentHelper.id());
    }
    
    public AdapterNonExistent(final String s) {
        super(AdapterNonExistentHelper.id() + "  " + s);
    }
}
