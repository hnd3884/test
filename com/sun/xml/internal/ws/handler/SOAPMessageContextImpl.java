package com.sun.xml.internal.ws.handler;

import java.util.Iterator;
import java.util.List;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Header;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.soap.SOAPException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.WSBinding;
import javax.xml.soap.SOAPMessage;
import java.util.Set;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPMessageContextImpl extends MessageUpdatableContext implements SOAPMessageContext
{
    private Set<String> roles;
    private SOAPMessage soapMsg;
    private WSBinding binding;
    
    public SOAPMessageContextImpl(final WSBinding binding, final Packet packet, final Set<String> roles) {
        super(packet);
        this.soapMsg = null;
        this.binding = binding;
        this.roles = roles;
    }
    
    @Override
    public SOAPMessage getMessage() {
        if (this.soapMsg == null) {
            try {
                final Message m = this.packet.getMessage();
                this.soapMsg = ((m != null) ? m.readAsSOAPMessage() : null);
            }
            catch (final SOAPException e) {
                throw new WebServiceException(e);
            }
        }
        return this.soapMsg;
    }
    
    @Override
    public void setMessage(final SOAPMessage soapMsg) {
        try {
            this.soapMsg = soapMsg;
        }
        catch (final Exception e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    void setPacketMessage(final Message newMessage) {
        if (newMessage != null) {
            this.packet.setMessage(newMessage);
            this.soapMsg = null;
        }
    }
    
    protected void updateMessage() {
        if (this.soapMsg != null) {
            this.packet.setMessage(SAAJFactory.create(this.soapMsg));
            this.soapMsg = null;
        }
    }
    
    @Override
    public Object[] getHeaders(final QName header, final JAXBContext jaxbContext, final boolean allRoles) {
        final SOAPVersion soapVersion = this.binding.getSOAPVersion();
        final List<Object> beanList = new ArrayList<Object>();
        try {
            final Iterator<Header> itr = this.packet.getMessage().getHeaders().getHeaders(header, false);
            if (allRoles) {
                while (itr.hasNext()) {
                    beanList.add(itr.next().readAsJAXB(jaxbContext.createUnmarshaller()));
                }
            }
            else {
                while (itr.hasNext()) {
                    final Header soapHeader = itr.next();
                    final String role = soapHeader.getRole(soapVersion);
                    if (this.getRoles().contains(role)) {
                        beanList.add(soapHeader.readAsJAXB(jaxbContext.createUnmarshaller()));
                    }
                }
            }
            return beanList.toArray();
        }
        catch (final Exception e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    public Set<String> getRoles() {
        return this.roles;
    }
}
