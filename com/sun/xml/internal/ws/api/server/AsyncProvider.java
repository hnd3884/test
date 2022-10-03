package com.sun.xml.internal.ws.api.server;

import javax.xml.ws.WebServiceContext;
import com.sun.istack.internal.NotNull;

public interface AsyncProvider<T>
{
    void invoke(@NotNull final T p0, @NotNull final AsyncProviderCallback<T> p1, @NotNull final WebServiceContext p2);
}
