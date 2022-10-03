package org.glassfish.jersey.client;

import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.process.internal.RequestScope;

interface ResponseCallback
{
    void completed(final ClientResponse p0, final RequestScope p1);
    
    void failed(final ProcessingException p0);
}
