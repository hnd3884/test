package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;

public interface Component
{
    @Nullable
     <S> S getSPI(@NotNull final Class<S> p0);
}
