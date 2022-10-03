package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.pipe.Tube;

public interface EndpointAwareTube extends Tube
{
    void setEndpoint(final WSEndpoint<?> p0);
}
