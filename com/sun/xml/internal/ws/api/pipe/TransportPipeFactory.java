package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;

public abstract class TransportPipeFactory
{
    public abstract Pipe doCreate(@NotNull final ClientPipeAssemblerContext p0);
    
    @Deprecated
    public static Pipe create(@Nullable final ClassLoader classLoader, @NotNull final ClientPipeAssemblerContext context) {
        return PipeAdapter.adapt(TransportTubeFactory.create(classLoader, context));
    }
}
