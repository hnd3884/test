package org.glassfish.jersey.client.spi;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.client.Client;

public interface ConnectorProvider
{
    Connector getConnector(final Client p0, final Configuration p1);
}
