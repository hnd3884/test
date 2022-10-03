package org.glassfish.jersey.spi;

import javax.ws.rs.ext.ExceptionMapper;

public interface ExtendedExceptionMapper<T extends Throwable> extends ExceptionMapper<T>
{
    boolean isMappable(final T p0);
}
