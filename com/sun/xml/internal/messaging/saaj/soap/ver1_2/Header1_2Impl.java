package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.util.logging.Level;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.namespace.QName;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import com.sun.xml.internal.messaging.saaj.soap.impl.HeaderImpl;

public class Header1_2Impl extends HeaderImpl
{
    protected static final Logger log;
    
    public Header1_2Impl(final SOAPDocumentImpl ownerDocument, final String prefix) {
        super(ownerDocument, NameImpl.createHeader1_2Name(prefix));
    }
    
    @Override
    protected NameImpl getNotUnderstoodName() {
        return NameImpl.createNotUnderstood1_2Name(null);
    }
    
    @Override
    protected NameImpl getUpgradeName() {
        return NameImpl.createUpgrade1_2Name(null);
    }
    
    @Override
    protected NameImpl getSupportedEnvelopeName() {
        return NameImpl.createSupportedEnvelope1_2Name(null);
    }
    
    @Override
    public SOAPHeaderElement addNotUnderstoodHeaderElement(final QName sourceName) throws SOAPException {
        if (sourceName == null) {
            Header1_2Impl.log.severe("SAAJ0410.ver1_2.no.null.to.addNotUnderstoodHeader");
            throw new SOAPException("Cannot pass NULL to addNotUnderstoodHeaderElement");
        }
        if ("".equals(sourceName.getNamespaceURI())) {
            Header1_2Impl.log.severe("SAAJ0417.ver1_2.qname.not.ns.qualified");
            throw new SOAPException("The qname passed to addNotUnderstoodHeaderElement must be namespace-qualified");
        }
        String prefix = sourceName.getPrefix();
        if ("".equals(prefix)) {
            prefix = "ns1";
        }
        final Name notunderstoodName = this.getNotUnderstoodName();
        final SOAPHeaderElement notunderstoodHeaderElement = (SOAPHeaderElement)this.addChildElement(notunderstoodName);
        notunderstoodHeaderElement.addAttribute(NameImpl.createFromUnqualifiedName("qname"), ElementImpl.getQualifiedName(new QName(sourceName.getNamespaceURI(), sourceName.getLocalPart(), prefix)));
        notunderstoodHeaderElement.addNamespaceDeclaration(prefix, sourceName.getNamespaceURI());
        return notunderstoodHeaderElement;
    }
    
    @Override
    public SOAPElement addTextNode(final String text) throws SOAPException {
        Header1_2Impl.log.log(Level.SEVERE, "SAAJ0416.ver1_2.adding.text.not.legal", this.getElementQName());
        throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Header is not legal");
    }
    
    @Override
    protected SOAPHeaderElement createHeaderElement(final Name name) throws SOAPException {
        final String uri = name.getURI();
        if (uri == null || uri.equals("")) {
            Header1_2Impl.log.severe("SAAJ0413.ver1_2.header.elems.must.be.ns.qualified");
            throw new SOAPExceptionImpl("SOAP 1.2 header elements must be namespace qualified");
        }
        return new HeaderElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
    }
    
    @Override
    protected SOAPHeaderElement createHeaderElement(final QName name) throws SOAPException {
        final String uri = name.getNamespaceURI();
        if (uri == null || uri.equals("")) {
            Header1_2Impl.log.severe("SAAJ0413.ver1_2.header.elems.must.be.ns.qualified");
            throw new SOAPExceptionImpl("SOAP 1.2 header elements must be namespace qualified");
        }
        return new HeaderElement1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
    }
    
    @Override
    public void setEncodingStyle(final String encodingStyle) throws SOAPException {
        Header1_2Impl.log.severe("SAAJ0409.ver1_2.no.encodingstyle.in.header");
        throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Header");
    }
    
    @Override
    public SOAPElement addAttribute(final Name name, final String value) throws SOAPException {
        if (name.getLocalName().equals("encodingStyle") && name.getURI().equals("http://www.w3.org/2003/05/soap-envelope")) {
            this.setEncodingStyle(value);
        }
        return super.addAttribute(name, value);
    }
    
    @Override
    public SOAPElement addAttribute(final QName name, final String value) throws SOAPException {
        if (name.getLocalPart().equals("encodingStyle") && name.getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope")) {
            this.setEncodingStyle(value);
        }
        return super.addAttribute(name, value);
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_2", "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
    }
}
