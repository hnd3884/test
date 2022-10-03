package com.sun.xml.internal.ws.transport.http.server;

import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;

public class ServerAdapterList extends HttpAdapterList<ServerAdapter>
{
    @Override
    protected ServerAdapter createHttpAdapter(final String name, final String urlPattern, final WSEndpoint<?> endpoint) {
        return new ServerAdapter(name, urlPattern, endpoint, this);
    }
}
