package org.glassfish.jersey.servlet.internal.spi;

public interface ExtendedServletContainerProvider extends ServletContainerProvider
{
    RequestScopedInitializerProvider getRequestScopedInitializerProvider();
    
    boolean bindsServletRequestResponse();
}
