package com.sun.xml.internal.ws.api.pipe;

public interface FiberContextSwitchInterceptor
{
     <R, P> R execute(final Fiber p0, final P p1, final Work<R, P> p2);
    
    public interface Work<R, P>
    {
        R execute(final P p0);
    }
}
