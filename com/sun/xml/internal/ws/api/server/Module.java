package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import java.util.List;
import com.sun.xml.internal.ws.api.Component;

public abstract class Module implements Component
{
    @NotNull
    public abstract List<BoundEndpoint> getBoundEndpoints();
    
    @Nullable
    @Override
    public <S> S getSPI(@NotNull final Class<S> spiType) {
        return null;
    }
}
