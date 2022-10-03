package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;
import com.sun.istack.internal.NotNull;

public interface WSDLService extends WSDLObject, WSDLExtensible
{
    @NotNull
    WSDLModel getParent();
    
    @NotNull
    QName getName();
    
    WSDLPort get(final QName p0);
    
    WSDLPort getFirstPort();
    
    @Nullable
    WSDLPort getMatchingPort(final QName p0);
    
    Iterable<? extends WSDLPort> getPorts();
}
