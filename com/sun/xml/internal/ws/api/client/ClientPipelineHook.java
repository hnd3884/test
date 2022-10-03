package com.sun.xml.internal.ws.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Pipe;
import com.sun.xml.internal.ws.api.pipe.ClientPipeAssemblerContext;

public abstract class ClientPipelineHook
{
    @NotNull
    public Pipe createSecurityPipe(final ClientPipeAssemblerContext ctxt, @NotNull final Pipe tail) {
        return tail;
    }
}
