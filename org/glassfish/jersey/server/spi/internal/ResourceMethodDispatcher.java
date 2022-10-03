package org.glassfish.jersey.server.spi.internal;

import org.glassfish.jersey.server.internal.inject.ConfiguredValidator;
import java.lang.reflect.InvocationHandler;
import org.glassfish.jersey.server.model.Invocable;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.ContainerRequest;

public interface ResourceMethodDispatcher
{
    Response dispatch(final Object p0, final ContainerRequest p1) throws ProcessingException;
    
    public interface Provider
    {
        ResourceMethodDispatcher create(final Invocable p0, final InvocationHandler p1, final ConfiguredValidator p2);
    }
}
