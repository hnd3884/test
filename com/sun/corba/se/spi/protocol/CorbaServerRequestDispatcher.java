package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.pept.protocol.ServerRequestDispatcher;

public interface CorbaServerRequestDispatcher extends ServerRequestDispatcher
{
    IOR locate(final ObjectKey p0);
}
