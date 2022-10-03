package com.sun.xml.internal.ws.transport.http.server;

import com.sun.istack.internal.NotNull;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import java.util.List;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.xml.internal.ws.api.server.Container;

class ServerContainer extends Container
{
    private final Module module;
    
    ServerContainer() {
        this.module = new Module() {
            private final List<BoundEndpoint> endpoints = new ArrayList<BoundEndpoint>();
            
            @NotNull
            @Override
            public List<BoundEndpoint> getBoundEndpoints() {
                return this.endpoints;
            }
        };
    }
    
    @Override
    public <T> T getSPI(final Class<T> spiType) {
        final T t = super.getSPI(spiType);
        if (t != null) {
            return t;
        }
        if (spiType == Module.class) {
            return spiType.cast(this.module);
        }
        return null;
    }
}
