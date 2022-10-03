package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import javax.xml.soap.SOAPElement;
import javax.xml.namespace.QName;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;

public class FaultElement1_1Impl extends FaultElementImpl
{
    public FaultElement1_1Impl(final SOAPDocumentImpl ownerDoc, final NameImpl qname) {
        super(ownerDoc, qname);
    }
    
    public FaultElement1_1Impl(final SOAPDocumentImpl ownerDoc, final QName qname) {
        super(ownerDoc, qname);
    }
    
    public FaultElement1_1Impl(final SOAPDocumentImpl ownerDoc, final String localName) {
        super(ownerDoc, NameImpl.createFaultElement1_1Name(localName));
    }
    
    public FaultElement1_1Impl(final SOAPDocumentImpl ownerDoc, final String localName, final String prefix) {
        super(ownerDoc, NameImpl.createFaultElement1_1Name(localName, prefix));
    }
    
    @Override
    protected boolean isStandardFaultElement() {
        final String localName = this.elementQName.getLocalPart();
        return localName.equalsIgnoreCase("faultcode") || localName.equalsIgnoreCase("faultstring") || localName.equalsIgnoreCase("faultactor");
    }
    
    @Override
    public SOAPElement setElementQName(final QName newName) throws SOAPException {
        if (!this.isStandardFaultElement()) {
            final FaultElement1_1Impl copy = new FaultElement1_1Impl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
            return ElementImpl.replaceElementWithSOAPElement(this, copy);
        }
        return super.setElementQName(newName);
    }
}
