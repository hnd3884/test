package com.sun.xml.internal.ws.api.server;

import com.sun.xml.internal.ws.server.DefaultResourceInjector;
import com.sun.istack.internal.NotNull;

public abstract class ResourceInjector
{
    public static final ResourceInjector STANDALONE;
    
    public abstract void inject(@NotNull final WSWebServiceContext p0, @NotNull final Object p1);
    
    static {
        STANDALONE = new DefaultResourceInjector();
    }
}
