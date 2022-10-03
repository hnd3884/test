package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.ServerPipeAssemblerContext;

public abstract class ServerPipelineHook
{
    @NotNull
    public Pipe createMonitoringPipe(final ServerPipeAssemblerContext ctxt, @NotNull final Pipe tail) {
        return tail;
    }
    
    @NotNull
    public Pipe createSecurityPipe(final ServerPipeAssemblerContext ctxt, @NotNull final Pipe tail) {
        return tail;
    }
}
