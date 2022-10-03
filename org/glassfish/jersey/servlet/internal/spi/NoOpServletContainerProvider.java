package org.glassfish.jersey.servlet.internal.spi;

import org.glassfish.jersey.server.ResourceConfig;
import javax.servlet.ServletException;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.glassfish.jersey.internal.util.collection.Ref;
import javax.ws.rs.core.GenericType;
import java.lang.reflect.Type;

public class NoOpServletContainerProvider implements ExtendedServletContainerProvider
{
    public final Type HTTP_SERVLET_REQUEST_TYPE;
    public final Type HTTP_SERVLET_RESPONSE_TYPE;
    
    public NoOpServletContainerProvider() {
        this.HTTP_SERVLET_REQUEST_TYPE = new GenericType<Ref<HttpServletRequest>>() {}.getType();
        this.HTTP_SERVLET_RESPONSE_TYPE = new GenericType<Ref<HttpServletResponse>>() {}.getType();
    }
    
    @Override
    public void preInit(final ServletContext servletContext, final Set<Class<?>> classes) throws ServletException {
    }
    
    @Override
    public void postInit(final ServletContext servletContext, final Set<Class<?>> classes, final Set<String> servletNames) {
    }
    
    @Override
    public void onRegister(final ServletContext servletContext, final Set<String> servletNames) throws ServletException {
    }
    
    @Override
    public void configure(final ResourceConfig resourceConfig) throws ServletException {
    }
    
    @Override
    public boolean bindsServletRequestResponse() {
        return false;
    }
    
    @Override
    public RequestScopedInitializerProvider getRequestScopedInitializerProvider() {
        return null;
    }
}
