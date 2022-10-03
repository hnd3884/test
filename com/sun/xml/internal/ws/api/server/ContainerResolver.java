package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;

public abstract class ContainerResolver
{
    private static final ThreadLocalContainerResolver DEFAULT;
    private static volatile ContainerResolver theResolver;
    
    public static void setInstance(ContainerResolver resolver) {
        if (resolver == null) {
            resolver = ContainerResolver.DEFAULT;
        }
        ContainerResolver.theResolver = resolver;
    }
    
    @NotNull
    public static ContainerResolver getInstance() {
        return ContainerResolver.theResolver;
    }
    
    public static ThreadLocalContainerResolver getDefault() {
        return ContainerResolver.DEFAULT;
    }
    
    @NotNull
    public abstract Container getContainer();
    
    static {
        DEFAULT = new ThreadLocalContainerResolver();
        ContainerResolver.theResolver = ContainerResolver.DEFAULT;
    }
}
