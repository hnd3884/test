package com.sun.xml.internal.ws.api.server;

import javax.xml.ws.Provider;
import java.lang.reflect.InvocationTargetException;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.ws.WebServiceContext;
import com.sun.istack.internal.NotNull;
import java.lang.reflect.Method;

public abstract class Invoker extends com.sun.xml.internal.ws.server.sei.Invoker
{
    private static final Method invokeMethod;
    private static final Method asyncInvokeMethod;
    
    public void start(@NotNull final WSWebServiceContext wsc, @NotNull final WSEndpoint endpoint) {
        this.start(wsc);
    }
    
    @Deprecated
    public void start(@NotNull final WebServiceContext wsc) {
        throw new IllegalStateException("deprecated version called");
    }
    
    public void dispose() {
    }
    
    public <T> T invokeProvider(@NotNull final Packet p, final T arg) throws IllegalAccessException, InvocationTargetException {
        return (T)this.invoke(p, Invoker.invokeMethod, arg);
    }
    
    public <T> void invokeAsyncProvider(@NotNull final Packet p, final T arg, final AsyncProviderCallback cbak, final WebServiceContext ctxt) throws IllegalAccessException, InvocationTargetException {
        this.invoke(p, Invoker.asyncInvokeMethod, arg, cbak, ctxt);
    }
    
    static {
        try {
            invokeMethod = Provider.class.getMethod("invoke", Object.class);
        }
        catch (final NoSuchMethodException e) {
            throw new AssertionError((Object)e);
        }
        try {
            asyncInvokeMethod = AsyncProvider.class.getMethod("invoke", Object.class, AsyncProviderCallback.class, WebServiceContext.class);
        }
        catch (final NoSuchMethodException e) {
            throw new AssertionError((Object)e);
        }
    }
}
