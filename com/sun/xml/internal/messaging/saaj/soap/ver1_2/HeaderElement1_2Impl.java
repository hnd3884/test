package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

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

public class HeaderElement1_2Impl extends HeaderElementImpl
{
    private static final Logger log;
    
    public HeaderElement1_2Impl(final SOAPDocumentImpl ownerDoc, final Name qname) {
        super(ownerDoc, qname);
    }
    
    public HeaderElement1_2Impl(final SOAPDocumentImpl ownerDoc, final QName qname) {
        super(ownerDoc, qname);
    }
    
    @Override
    public SOAPElement setElementQName(final QName newName) throws SOAPException {
        final HeaderElementImpl copy = new HeaderElement1_2Impl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
        return ElementImpl.replaceElementWithSOAPElement(this, copy);
    }
    
    @Override
    protected NameImpl getRoleAttributeName() {
        return NameImpl.create("role", null, "http://www.w3.org/2003/05/soap-envelope");
    }
    
    @Override
    protected NameImpl getActorAttributeName() {
        return this.getRoleAttributeName();
    }
    
    @Override
    protected NameImpl getMustunderstandAttributeName() {
        return NameImpl.create("mustUnderstand", null, "http://www.w3.org/2003/05/soap-envelope");
    }
    
    @Override
    protected String getMustunderstandLiteralValue(final boolean mustUnderstand) {
        return mustUnderstand ? "true" : "false";
    }
    
    @Override
    protected boolean getMustunderstandAttributeValue(final String mu) {
        return mu.equals("true") || mu.equals("1");
    }
    
    @Override
    protected NameImpl getRelayAttributeName() {
        return NameImpl.create("relay", null, "http://www.w3.org/2003/05/soap-envelope");
    }
    
    @Override
    protected String getRelayLiteralValue(final boolean relay) {
        return relay ? "true" : "false";
    }
    
    @Override
    protected boolean getRelayAttributeValue(final String relay) {
        return relay.equals("true") || relay.equals("1");
    }
    
    @Override
    protected String getActorOrRole() {
        return this.getRole();
    }
    
    static {
        log = Logger.getLogger(HeaderElement1_2Impl.class.getName(), "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
    }
}
