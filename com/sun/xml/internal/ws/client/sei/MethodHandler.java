package com.sun.xml.internal.ws.client.sei;

import javax.xml.ws.WebServiceException;
import java.lang.reflect.Method;

public abstract class MethodHandler
{
    protected final SEIStub owner;
    protected Method method;
    
    protected MethodHandler(final SEIStub owner, final Method m) {
        this.owner = owner;
        this.method = m;
    }
    
    abstract Object invoke(final Object p0, final Object[] p1) throws WebServiceException, Throwable;
}
