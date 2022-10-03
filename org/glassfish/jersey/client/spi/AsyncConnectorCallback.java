package org.glassfish.jersey.client.spi;

import org.glassfish.jersey.client.ClientResponse;

public interface AsyncConnectorCallback
{
    void response(final ClientResponse p0);
    
    void failure(final Throwable p0);
}
