package com.sun.xml.internal.ws.addressing;

import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.oracle.webservices.internal.api.message.PropertySet;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.oracle.webservices.internal.api.message.BasePropertySet;

public class WsaPropertyBag extends BasePropertySet
{
    public static final String WSA_REPLYTO_FROM_REQUEST = "com.sun.xml.internal.ws.addressing.WsaPropertyBag.ReplyToFromRequest";
    public static final String WSA_FAULTTO_FROM_REQUEST = "com.sun.xml.internal.ws.addressing.WsaPropertyBag.FaultToFromRequest";
    public static final String WSA_MSGID_FROM_REQUEST = "com.sun.xml.internal.ws.addressing.WsaPropertyBag.MessageIdFromRequest";
    public static final String WSA_TO = "com.sun.xml.internal.ws.addressing.WsaPropertyBag.To";
    @NotNull
    private final AddressingVersion addressingVersion;
    @NotNull
    private final SOAPVersion soapVersion;
    @NotNull
    private final Packet packet;
    private static final PropertyMap model;
    private WSEndpointReference _replyToFromRequest;
    private WSEndpointReference _faultToFromRequest;
    private String _msgIdFromRequest;
    
    public WsaPropertyBag(final AddressingVersion addressingVersion, final SOAPVersion soapVersion, final Packet packet) {
        this._replyToFromRequest = null;
        this._faultToFromRequest = null;
        this._msgIdFromRequest = null;
        this.addressingVersion = addressingVersion;
        this.soapVersion = soapVersion;
        this.packet = packet;
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.api.addressing.to" })
    public String getTo() throws XMLStreamException {
        if (this.packet.getMessage() == null) {
            return null;
        }
        final Header h = this.packet.getMessage().getHeaders().get(this.addressingVersion.toTag, false);
        if (h == null) {
            return null;
        }
        return h.getStringContent();
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.addressing.WsaPropertyBag.To" })
    public WSEndpointReference getToAsReference() throws XMLStreamException {
        if (this.packet.getMessage() == null) {
            return null;
        }
        final Header h = this.packet.getMessage().getHeaders().get(this.addressingVersion.toTag, false);
        if (h == null) {
            return null;
        }
        return new WSEndpointReference(h.getStringContent(), this.addressingVersion);
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.api.addressing.from" })
    public WSEndpointReference getFrom() throws XMLStreamException {
        return this.getEPR(this.addressingVersion.fromTag);
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.api.addressing.action" })
    public String getAction() {
        if (this.packet.getMessage() == null) {
            return null;
        }
        final Header h = this.packet.getMessage().getHeaders().get(this.addressingVersion.actionTag, false);
        if (h == null) {
            return null;
        }
        return h.getStringContent();
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.api.addressing.messageId", "com.sun.xml.internal.ws.addressing.request.messageID" })
    public String getMessageID() {
        if (this.packet.getMessage() == null) {
            return null;
        }
        return AddressingUtils.getMessageID(this.packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
    }
    
    private WSEndpointReference getEPR(final QName tag) throws XMLStreamException {
        if (this.packet.getMessage() == null) {
            return null;
        }
        final Header h = this.packet.getMessage().getHeaders().get(tag, false);
        if (h == null) {
            return null;
        }
        return h.readAsEPR(this.addressingVersion);
    }
    
    @Override
    protected PropertyMap getPropertyMap() {
        return WsaPropertyBag.model;
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.addressing.WsaPropertyBag.ReplyToFromRequest" })
    public WSEndpointReference getReplyToFromRequest() {
        return this._replyToFromRequest;
    }
    
    public void setReplyToFromRequest(final WSEndpointReference ref) {
        this._replyToFromRequest = ref;
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.addressing.WsaPropertyBag.FaultToFromRequest" })
    public WSEndpointReference getFaultToFromRequest() {
        return this._faultToFromRequest;
    }
    
    public void setFaultToFromRequest(final WSEndpointReference ref) {
        this._faultToFromRequest = ref;
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.addressing.WsaPropertyBag.MessageIdFromRequest" })
    public String getMessageIdFromRequest() {
        return this._msgIdFromRequest;
    }
    
    public void setMessageIdFromRequest(final String id) {
        this._msgIdFromRequest = id;
    }
    
    static {
        model = BasePropertySet.parse(WsaPropertyBag.class);
    }
}
