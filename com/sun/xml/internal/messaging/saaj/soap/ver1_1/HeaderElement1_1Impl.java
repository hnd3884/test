package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import java.util.logging.Level;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import javax.xml.soap.SOAPElement;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import com.sun.xml.internal.messaging.saaj.soap.impl.HeaderElementImpl;

public class HeaderElement1_1Impl extends HeaderElementImpl
{
    protected static final Logger log;
    
    public HeaderElement1_1Impl(final SOAPDocumentImpl ownerDoc, final Name qname) {
        super(ownerDoc, qname);
    }
    
    public HeaderElement1_1Impl(final SOAPDocumentImpl ownerDoc, final QName qname) {
        super(ownerDoc, qname);
    }
    
    @Override
    public SOAPElement setElementQName(final QName newName) throws SOAPException {
        final HeaderElementImpl copy = new HeaderElement1_1Impl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
        return ElementImpl.replaceElementWithSOAPElement(this, copy);
    }
    
    @Override
    protected NameImpl getActorAttributeName() {
        return NameImpl.create("actor", null, "http://schemas.xmlsoap.org/soap/envelope/");
    }
    
    @Override
    protected NameImpl getRoleAttributeName() {
        HeaderElement1_1Impl.log.log(Level.SEVERE, "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", new String[] { "Role" });
        throw new UnsupportedOperationException("Role not supported by SOAP 1.1");
    }
    
    @Override
    protected NameImpl getMustunderstandAttributeName() {
        return NameImpl.create("mustUnderstand", null, "http://schemas.xmlsoap.org/soap/envelope/");
    }
    
    @Override
    protected String getMustunderstandLiteralValue(final boolean mustUnderstand) {
        return mustUnderstand ? "1" : "0";
    }
    
    @Override
    protected boolean getMustunderstandAttributeValue(final String mu) {
        return "1".equals(mu) || "true".equalsIgnoreCase(mu);
    }
    
    @Override
    protected NameImpl getRelayAttributeName() {
        HeaderElement1_1Impl.log.log(Level.SEVERE, "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", new String[] { "Relay" });
        throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
    }
    
    @Override
    protected String getRelayLiteralValue(final boolean relayAttr) {
        HeaderElement1_1Impl.log.log(Level.SEVERE, "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", new String[] { "Relay" });
        throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
    }
    
    @Override
    protected boolean getRelayAttributeValue(final String mu) {
        HeaderElement1_1Impl.log.log(Level.SEVERE, "SAAJ0302.ver1_1.hdr.attr.unsupported.in.SOAP1.1", new String[] { "Relay" });
        throw new UnsupportedOperationException("Relay not supported by SOAP 1.1");
    }
    
    @Override
    protected String getActorOrRole() {
        return this.getActor();
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");
    }
}
