package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;

public interface Tube
{
    @NotNull
    NextAction processRequest(@NotNull final Packet p0);
    
    @NotNull
    NextAction processResponse(@NotNull final Packet p0);
    
    @NotNull
    NextAction processException(@NotNull final Throwable p0);
    
    void preDestroy();
    
    Tube copy(final TubeCloner p0);
}
