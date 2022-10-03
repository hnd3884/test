package com.sun.xml.internal.ws.server;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;
import java.security.Principal;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.ws.handler.MessageContext;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;

public abstract class AbstractWebServiceContext implements WSWebServiceContext
{
    private final WSEndpoint endpoint;
    
    public AbstractWebServiceContext(@NotNull final WSEndpoint endpoint) {
        this.endpoint = endpoint;
    }
    
    @Override
    public MessageContext getMessageContext() {
        final Packet packet = this.getRequestPacket();
        if (packet == null) {
            throw new IllegalStateException("getMessageContext() can only be called while servicing a request");
        }
        return new EndpointMessageContextImpl(packet);
    }
    
    @Override
    public Principal getUserPrincipal() {
        final Packet packet = this.getRequestPacket();
        if (packet == null) {
            throw new IllegalStateException("getUserPrincipal() can only be called while servicing a request");
        }
        return packet.webServiceContextDelegate.getUserPrincipal(packet);
    }
    
    @Override
    public boolean isUserInRole(final String role) {
        final Packet packet = this.getRequestPacket();
        if (packet == null) {
            throw new IllegalStateException("isUserInRole() can only be called while servicing a request");
        }
        return packet.webServiceContextDelegate.isUserInRole(packet, role);
    }
    
    @Override
    public EndpointReference getEndpointReference(final Element... referenceParameters) {
        return this.getEndpointReference(W3CEndpointReference.class, referenceParameters);
    }
    
    @Override
    public <T extends EndpointReference> T getEndpointReference(final Class<T> clazz, final Element... referenceParameters) {
        final Packet packet = this.getRequestPacket();
        if (packet == null) {
            throw new IllegalStateException("getEndpointReference() can only be called while servicing a request");
        }
        final String address = packet.webServiceContextDelegate.getEPRAddress(packet, this.endpoint);
        String wsdlAddress = null;
        if (this.endpoint.getServiceDefinition() != null) {
            wsdlAddress = packet.webServiceContextDelegate.getWSDLAddress(packet, this.endpoint);
        }
        return clazz.cast(this.endpoint.getEndpointReference(clazz, address, wsdlAddress, referenceParameters));
    }
}
