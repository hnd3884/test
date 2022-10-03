package com.sun.corba.se.spi.ior;

import org.omg.CORBA_2_3.portable.InputStream;

public interface IdentifiableFactoryFinder
{
    Identifiable create(final int p0, final InputStream p1);
    
    void registerFactory(final IdentifiableFactory p0);
}
