package com.sun.xml.internal.messaging.saaj.soap.impl;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPElement;
import javax.xml.namespace.QName;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPHeaderElement;

public abstract class HeaderElementImpl extends ElementImpl implements SOAPHeaderElement
{
    protected static Name RELAY_ATTRIBUTE_LOCAL_NAME;
    protected static Name MUST_UNDERSTAND_ATTRIBUTE_LOCAL_NAME;
    Name actorAttNameWithoutNS;
    Name roleAttNameWithoutNS;
    
    public HeaderElementImpl(final SOAPDocumentImpl ownerDoc, final Name qname) {
        super(ownerDoc, qname);
        this.actorAttNameWithoutNS = NameImpl.createFromTagName("actor");
        this.roleAttNameWithoutNS = NameImpl.createFromTagName("role");
    }
    
    public HeaderElementImpl(final SOAPDocumentImpl ownerDoc, final QName qname) {
        super(ownerDoc, qname);
        this.actorAttNameWithoutNS = NameImpl.createFromTagName("actor");
        this.roleAttNameWithoutNS = NameImpl.createFromTagName("role");
    }
    
    protected abstract NameImpl getActorAttributeName();
    
    protected abstract NameImpl getRoleAttributeName();
    
    protected abstract NameImpl getMustunderstandAttributeName();
    
    protected abstract boolean getMustunderstandAttributeValue(final String p0);
    
    protected abstract String getMustunderstandLiteralValue(final boolean p0);
    
    protected abstract NameImpl getRelayAttributeName();
    
    protected abstract boolean getRelayAttributeValue(final String p0);
    
    protected abstract String getRelayLiteralValue(final boolean p0);
    
    protected abstract String getActorOrRole();
    
    @Override
    public void setParentElement(final SOAPElement element) throws SOAPException {
        if (!(element instanceof SOAPHeader)) {
            HeaderElementImpl.log.severe("SAAJ0130.impl.header.elem.parent.mustbe.header");
            throw new SOAPException("Parent of a SOAPHeaderElement has to be a SOAPHeader");
        }
        super.setParentElement(element);
    }
    
    @Override
    public void setActor(final String actorUri) {
        try {
            this.removeAttribute(this.getActorAttributeName());
            this.addAttribute(this.getActorAttributeName(), actorUri);
        }
        catch (final SOAPException ex) {}
    }
    
    @Override
    public void setRole(final String roleUri) throws SOAPException {
        this.removeAttribute(this.getRoleAttributeName());
        this.addAttribute(this.getRoleAttributeName(), roleUri);
    }
    
    @Override
    public String getActor() {
        final String actor = this.getAttributeValue(this.getActorAttributeName());
        return actor;
    }
    
    @Override
    public String getRole() {
        final String role = this.getAttributeValue(this.getRoleAttributeName());
        return role;
    }
    
    @Override
    public void setMustUnderstand(final boolean mustUnderstand) {
        try {
            this.removeAttribute(this.getMustunderstandAttributeName());
            this.addAttribute(this.getMustunderstandAttributeName(), this.getMustunderstandLiteralValue(mustUnderstand));
        }
        catch (final SOAPException ex) {}
    }
    
    @Override
    public boolean getMustUnderstand() {
        final String mu = this.getAttributeValue(this.getMustunderstandAttributeName());
        return mu != null && this.getMustunderstandAttributeValue(mu);
    }
    
    @Override
    public void setRelay(final boolean relay) throws SOAPException {
        this.removeAttribute(this.getRelayAttributeName());
        this.addAttribute(this.getRelayAttributeName(), this.getRelayLiteralValue(relay));
    }
    
    @Override
    public boolean getRelay() {
        final String mu = this.getAttributeValue(this.getRelayAttributeName());
        return mu != null && this.getRelayAttributeValue(mu);
    }
    
    static {
        HeaderElementImpl.RELAY_ATTRIBUTE_LOCAL_NAME = NameImpl.createFromTagName("relay");
        HeaderElementImpl.MUST_UNDERSTAND_ATTRIBUTE_LOCAL_NAME = NameImpl.createFromTagName("mustUnderstand");
    }
}
