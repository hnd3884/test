package org.glassfish.jersey.server.spi.internal;

import java.lang.reflect.InvocationHandler;
import org.glassfish.jersey.server.model.Invocable;

public interface ResourceMethodInvocationHandlerProvider
{
    InvocationHandler create(final Invocable p0);
}
