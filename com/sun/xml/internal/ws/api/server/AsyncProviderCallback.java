package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public interface AsyncProviderCallback<T>
{
    void send(@Nullable final T p0);
    
    void sendError(@NotNull final Throwable p0);
}
