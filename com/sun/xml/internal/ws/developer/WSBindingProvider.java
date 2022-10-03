package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.message.Header;
import java.util.List;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import java.io.Closeable;
import javax.xml.ws.BindingProvider;

public interface WSBindingProvider extends BindingProvider, Closeable, ComponentRegistry
{
    void setOutboundHeaders(final List<Header> p0);
    
    void setOutboundHeaders(final Header... p0);
    
    void setOutboundHeaders(final Object... p0);
    
    List<Header> getInboundHeaders();
    
    void setAddress(final String p0);
    
    WSEndpointReference getWSEndpointReference();
    
    WSPortInfo getPortInfo();
    
    @NotNull
    ManagedObjectManager getManagedObjectManager();
}
