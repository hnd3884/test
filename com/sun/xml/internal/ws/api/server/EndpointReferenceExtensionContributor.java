package com.sun.xml.internal.ws.api.server;

import javax.xml.namespace.QName;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;

public abstract class EndpointReferenceExtensionContributor
{
    public abstract WSEndpointReference.EPRExtension getEPRExtension(final WSEndpoint p0, @Nullable final WSEndpointReference.EPRExtension p1);
    
    public abstract QName getQName();
}
