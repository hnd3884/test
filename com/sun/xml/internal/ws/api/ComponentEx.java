package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;

public interface ComponentEx extends Component
{
    @NotNull
     <S> Iterable<S> getIterableSPI(@NotNull final Class<S> p0);
}
