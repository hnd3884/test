package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;

public abstract class PortAddressResolver
{
    @Nullable
    public abstract String getAddressFor(@NotNull final QName p0, @NotNull final String p1);
    
    @Nullable
    public String getAddressFor(@NotNull final QName serviceName, @NotNull final String portName, final String currentAddress) {
        return this.getAddressFor(serviceName, portName);
    }
}
