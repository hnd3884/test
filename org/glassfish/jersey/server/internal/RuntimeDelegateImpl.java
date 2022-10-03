package org.glassfish.jersey.server.internal;

import org.glassfish.jersey.server.ContainerFactory;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.message.internal.MessagingBinders;
import org.glassfish.jersey.internal.AbstractRuntimeDelegate;

public class RuntimeDelegateImpl extends AbstractRuntimeDelegate
{
    public RuntimeDelegateImpl() {
        super(new MessagingBinders.HeaderDelegateProviders().getHeaderDelegateProviders());
    }
    
    public <T> T createEndpoint(final Application application, final Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
        if (application == null) {
            throw new IllegalArgumentException("application is null.");
        }
        return ContainerFactory.createContainer(endpointType, application);
    }
}
