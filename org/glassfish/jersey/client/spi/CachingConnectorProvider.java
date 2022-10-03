package org.glassfish.jersey.client.spi;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.client.Client;

public class CachingConnectorProvider implements ConnectorProvider
{
    private final ConnectorProvider delegate;
    private Connector connector;
    
    public CachingConnectorProvider(final ConnectorProvider delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public synchronized Connector getConnector(final Client client, final Configuration runtimeConfig) {
        if (this.connector == null) {
            this.connector = this.delegate.getConnector(client, runtimeConfig);
        }
        return this.connector;
    }
}
