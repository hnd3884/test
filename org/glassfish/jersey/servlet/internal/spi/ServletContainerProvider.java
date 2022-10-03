package org.glassfish.jersey.servlet.internal.spi;

import org.glassfish.jersey.server.ResourceConfig;
import javax.servlet.ServletException;
import java.util.Set;
import javax.servlet.ServletContext;

public interface ServletContainerProvider
{
    void preInit(final ServletContext p0, final Set<Class<?>> p1) throws ServletException;
    
    void postInit(final ServletContext p0, final Set<Class<?>> p1, final Set<String> p2) throws ServletException;
    
    void onRegister(final ServletContext p0, final Set<String> p1) throws ServletException;
    
    void configure(final ResourceConfig p0) throws ServletException;
}
