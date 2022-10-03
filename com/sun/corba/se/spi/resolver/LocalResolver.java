package com.sun.corba.se.spi.resolver;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public interface LocalResolver extends Resolver
{
    void register(final String p0, final Closure p1);
}
