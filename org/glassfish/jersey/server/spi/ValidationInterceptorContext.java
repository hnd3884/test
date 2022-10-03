package org.glassfish.jersey.server.spi;

import javax.validation.ValidationException;
import org.glassfish.jersey.server.model.Invocable;

public interface ValidationInterceptorContext
{
    Object getResource();
    
    void setResource(final Object p0);
    
    Invocable getInvocable();
    
    Object[] getArgs();
    
    void setArgs(final Object[] p0);
    
    void proceed() throws ValidationException;
}
