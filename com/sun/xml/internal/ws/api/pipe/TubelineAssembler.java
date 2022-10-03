package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;

public interface TubelineAssembler
{
    @NotNull
    Tube createClient(@NotNull final ClientTubeAssemblerContext p0);
    
    @NotNull
    Tube createServer(@NotNull final ServerTubeAssemblerContext p0);
}
