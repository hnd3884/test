package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceException;
import com.sun.istack.internal.NotNull;

public abstract class BindingIDFactory
{
    @Nullable
    public abstract BindingID parse(@NotNull final String p0) throws WebServiceException;
    
    @Nullable
    public BindingID create(@NotNull final String transport, @NotNull final SOAPVersion soapVersion) throws WebServiceException {
        return null;
    }
}
