package com.sun.xml.internal.ws.api.client;

import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSService;
import javax.xml.ws.handler.PortInfo;

public interface WSPortInfo extends PortInfo
{
    @NotNull
    WSService getOwner();
    
    @NotNull
    BindingID getBindingId();
    
    @NotNull
    EndpointAddress getEndpointAddress();
    
    @Nullable
    WSDLPort getPort();
    
    @Deprecated
    PolicyMap getPolicyMap();
}
