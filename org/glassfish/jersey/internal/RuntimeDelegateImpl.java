package org.glassfish.jersey.internal;

import javax.ws.rs.core.Application;
import org.glassfish.jersey.message.internal.MessagingBinders;

public class RuntimeDelegateImpl extends AbstractRuntimeDelegate
{
    public RuntimeDelegateImpl() {
        super(new MessagingBinders.HeaderDelegateProviders().getHeaderDelegateProviders());
    }
    
    public <T> T createEndpoint(final Application application, final Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException(LocalizationMessages.NO_CONTAINER_AVAILABLE());
    }
}
