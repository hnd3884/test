package com.sun.corba.se.spi.resolver;

import java.util.Set;
import org.omg.CORBA.Object;

public interface Resolver
{
    org.omg.CORBA.Object resolve(final String p0);
    
    Set list();
}
