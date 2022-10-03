package com.sun.corba.se.spi.orbutil.proxy;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;

public interface CompositeInvocationHandler extends InvocationHandler, Serializable
{
    void addInvocationHandler(final Class p0, final InvocationHandler p1);
    
    void setDefaultHandler(final InvocationHandler p0);
}
