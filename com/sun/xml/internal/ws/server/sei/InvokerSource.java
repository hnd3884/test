package com.sun.xml.internal.ws.server.sei;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;

public interface InvokerSource<T extends Invoker>
{
    @NotNull
    T getInvoker(final Packet p0);
}
