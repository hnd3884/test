package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import javax.xml.soap.SOAPElement;
import javax.xml.namespace.QName;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;

public class FaultElement1_2Impl extends FaultElementImpl
{
    public FaultElement1_2Impl(final SOAPDocumentImpl ownerDoc, final NameImpl qname) {
        super(ownerDoc, qname);
    }
    
    public FaultElement1_2Impl(final SOAPDocumentImpl ownerDoc, final QName qname) {
        super(ownerDoc, qname);
    }
    
    public FaultElement1_2Impl(final SOAPDocumentImpl ownerDoc, final String localName) {
        super(ownerDoc, NameImpl.createSOAP12Name(localName));
    }
    
    @Override
    protected boolean isStandardFaultElement() {
        final String localName = this.elementQName.getLocalPart();
        return localName.equalsIgnoreCase("code") || localName.equalsIgnoreCase("reason") || localName.equalsIgnoreCase("node") || localName.equalsIgnoreCase("role");
    }
    
    @Override
    public SOAPElement setElementQName(final QName newName) throws SOAPException {
        if (!this.isStandardFaultElement()) {
            final FaultElement1_2Impl copy = new FaultElement1_2Impl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
            return ElementImpl.replaceElementWithSOAPElement(this, copy);
        }
        return super.setElementQName(newName);
    }
    
    @Override
    public void setEncodingStyle(final String encodingStyle) throws SOAPException {
        FaultElement1_2Impl.log.severe("SAAJ0408.ver1_2.no.encodingStyle.in.fault.child");
        throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on a Fault child element");
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
}
