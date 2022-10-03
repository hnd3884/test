package com.sun.xml.internal.ws.api.server;

import java.net.URI;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.Component;

public interface BoundEndpoint extends Component
{
    @NotNull
    WSEndpoint getEndpoint();
    
    @NotNull
    URI getAddress();
    
    @NotNull
    URI getAddress(final String p0);
}
