package com.sun.xml.internal.ws.api.server;

import java.util.concurrent.Executor;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.istack.internal.NotNull;

public abstract class HttpEndpoint
{
    public static HttpEndpoint create(@NotNull final WSEndpoint endpoint) {
        return new com.sun.xml.internal.ws.transport.http.server.HttpEndpoint(null, HttpAdapter.createAlone(endpoint));
    }
    
    public abstract void publish(@NotNull final String p0);
    
    public abstract void stop();
}
