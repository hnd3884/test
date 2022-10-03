package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;

public interface DocumentAddressResolver
{
    @Nullable
    String getRelativeAddressFor(@NotNull final SDDocument p0, @NotNull final SDDocument p1);
}
