package com.sun.corba.se.spi.oa;

import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;

public interface ObjectAdapterFactory
{
    void init(final ORB p0);
    
    void shutdown(final boolean p0);
    
    ObjectAdapter find(final ObjectAdapterId p0);
    
    ORB getORB();
}
