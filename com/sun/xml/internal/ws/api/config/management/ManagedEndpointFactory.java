package com.sun.xml.internal.ws.api.config.management;

import com.sun.xml.internal.ws.api.server.WSEndpoint;

public interface ManagedEndpointFactory
{
     <T> WSEndpoint<T> createEndpoint(final WSEndpoint<T> p0, final EndpointCreationAttributes p1);
}
