package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;

public interface PipelineAssembler
{
    @NotNull
    Pipe createClient(@NotNull final ClientPipeAssemblerContext p0);
    
    @NotNull
    Pipe createServer(@NotNull final ServerPipeAssemblerContext p0);
}
