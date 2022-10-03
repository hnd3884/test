package com.sun.corba.se.impl.oa.toa;

import org.omg.CORBA.Object;
import com.sun.corba.se.spi.oa.ObjectAdapter;

public interface TOA extends ObjectAdapter
{
    void connect(final org.omg.CORBA.Object p0);
    
    void disconnect(final org.omg.CORBA.Object p0);
}
