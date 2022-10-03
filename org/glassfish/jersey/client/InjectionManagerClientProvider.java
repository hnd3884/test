package org.glassfish.jersey.client;

import javax.ws.rs.client.ClientResponseContext;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.internal.inject.InjectionManagerSupplier;
import org.glassfish.jersey.internal.inject.InjectionManager;
import javax.ws.rs.client.ClientRequestContext;
import org.glassfish.jersey.InjectionManagerProvider;

public class InjectionManagerClientProvider extends InjectionManagerProvider
{
    public static InjectionManager getInjectionManager(final ClientRequestContext clientRequestContext) {
        if (!(clientRequestContext instanceof InjectionManagerSupplier)) {
            throw new IllegalArgumentException(LocalizationMessages.ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_REQUEST(clientRequestContext.getClass().getName()));
        }
        return ((InjectionManagerSupplier)clientRequestContext).getInjectionManager();
    }
    
    public static InjectionManager getInjectionManager(final ClientResponseContext clientResponseContext) {
        if (!(clientResponseContext instanceof InjectionManagerSupplier)) {
            throw new IllegalArgumentException(LocalizationMessages.ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_RESPONSE(clientResponseContext.getClass().getName()));
        }
        return ((InjectionManagerSupplier)clientResponseContext).getInjectionManager();
    }
}
