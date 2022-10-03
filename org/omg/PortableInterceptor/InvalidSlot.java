package org.omg.PortableInterceptor;

import org.omg.CORBA.UserException;

public final class InvalidSlot extends UserException
{
    public InvalidSlot() {
        super(InvalidSlotHelper.id());
    }
    
    public InvalidSlot(final String s) {
        super(InvalidSlotHelper.id() + "  " + s);
    }
}
