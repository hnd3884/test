package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;

public interface EndpointComponent
{
    @Nullable
     <T> T getSPI(@NotNull final Class<T> p0);
}
