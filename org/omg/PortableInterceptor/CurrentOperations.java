package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;

public interface CurrentOperations extends org.omg.CORBA.CurrentOperations
{
    Any get_slot(final int p0) throws InvalidSlot;
    
    void set_slot(final int p0, final Any p1) throws InvalidSlot;
}
