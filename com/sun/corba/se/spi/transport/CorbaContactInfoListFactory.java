package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;

public interface CorbaContactInfoListFactory
{
    void setORB(final ORB p0);
    
    CorbaContactInfoList create(final IOR p0);
}
