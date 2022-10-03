package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.NotNull;
import java.net.URI;

public abstract class MetaDataResolver
{
    @Nullable
    public abstract ServiceDescriptor resolve(@NotNull final URI p0);
}
