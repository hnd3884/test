package org.glassfish.jersey.server;

import javax.ws.rs.core.Response;
import org.glassfish.jersey.internal.util.Producer;
import javax.ws.rs.container.AsyncResponse;

public interface AsyncContext extends AsyncResponse
{
    boolean suspend();
    
    void invokeManaged(final Producer<Response> p0);
    
    public enum State
    {
        RUNNING, 
        SUSPENDED, 
        RESUMED, 
        COMPLETED;
    }
}
