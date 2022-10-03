package com.sun.corba.se.spi.orbutil.proxy;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;

public interface LinkedInvocationHandler extends InvocationHandler
{
    void setProxy(final Proxy p0);
    
    Proxy getProxy();
}
