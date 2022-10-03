package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class WrongAdapter extends UserException
{
    public WrongAdapter() {
        super(WrongAdapterHelper.id());
    }
    
    public WrongAdapter(final String s) {
        super(WrongAdapterHelper.id() + "  " + s);
    }
}
