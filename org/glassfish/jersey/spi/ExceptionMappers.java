package org.glassfish.jersey.spi;

import javax.ws.rs.ext.ExceptionMapper;

public interface ExceptionMappers
{
     <T extends Throwable> ExceptionMapper<T> find(final Class<T> p0);
    
     <T extends Throwable> ExceptionMapper<T> findMapping(final T p0);
}
