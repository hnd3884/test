package org.glassfish.jersey.spi;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;

public interface ContextResolvers
{
     <T> ContextResolver<T> resolve(final Type p0, final MediaType p1);
}
