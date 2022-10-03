package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import java.security.Principal;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;

public interface WebServiceContextDelegate
{
    Principal getUserPrincipal(@NotNull final Packet p0);
    
    boolean isUserInRole(@NotNull final Packet p0, final String p1);
    
    @NotNull
    String getEPRAddress(@NotNull final Packet p0, @NotNull final WSEndpoint p1);
    
    @Nullable
    String getWSDLAddress(@NotNull final Packet p0, @NotNull final WSEndpoint p1);
}
