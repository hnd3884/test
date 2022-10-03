package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.orb.ORB;

public interface IORFactory extends Writeable, MakeImmutable
{
    IOR makeIOR(final ORB p0, final String p1, final ObjectId p2);
    
    boolean isEquivalent(final IORFactory p0);
}
