package org.glassfish.jersey.apache.connector;

import org.glassfish.jersey.client.Initializable;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import javax.ws.rs.core.Configurable;
import org.glassfish.jersey.client.spi.Connector;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.client.Client;
import org.glassfish.jersey.client.spi.ConnectorProvider;

public class ApacheConnectorProvider implements ConnectorProvider
{
    public Connector getConnector(final Client client, final Configuration runtimeConfig) {
        return (Connector)new ApacheConnector(client, runtimeConfig);
    }
    
    public static HttpClient getHttpClient(final Configurable<?> component) {
        return getConnector(component).getHttpClient();
    }
    
    public static CookieStore getCookieStore(final Configurable<?> component) {
        return getConnector(component).getCookieStore();
    }
    
    private static ApacheConnector getConnector(final Configurable<?> component) {
        if (!(component instanceof Initializable)) {
            throw new IllegalArgumentException(LocalizationMessages.INVALID_CONFIGURABLE_COMPONENT_TYPE(component.getClass().getName()));
        }
        final Initializable<?> initializable = (Initializable<?>)component;
        Connector connector = initializable.getConfiguration().getConnector();
        if (connector == null) {
            initializable.preInitialize();
            connector = initializable.getConfiguration().getConnector();
        }
        if (connector instanceof ApacheConnector) {
            return (ApacheConnector)connector;
        }
        throw new IllegalArgumentException(LocalizationMessages.EXPECTED_CONNECTOR_PROVIDER_NOT_USED());
    }
}
