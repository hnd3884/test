package org.glassfish.jersey.client.spi;

import java.util.concurrent.Future;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.process.Inflector;

public interface Connector extends Inflector<ClientRequest, ClientResponse>
{
    ClientResponse apply(final ClientRequest p0);
    
    Future<?> apply(final ClientRequest p0, final AsyncConnectorCallback p1);
    
    String getName();
    
    void close();
}
