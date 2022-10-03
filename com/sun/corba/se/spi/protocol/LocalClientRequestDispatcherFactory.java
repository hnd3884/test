package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.spi.ior.IOR;

public interface LocalClientRequestDispatcherFactory
{
    LocalClientRequestDispatcher create(final int p0, final IOR p1);
}
