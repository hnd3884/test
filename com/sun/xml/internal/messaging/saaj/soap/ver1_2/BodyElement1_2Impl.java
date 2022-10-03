package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import javax.xml.soap.SOAPElement;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.BodyElementImpl;

public class BodyElement1_2Impl extends BodyElementImpl
{
    public BodyElement1_2Impl(final SOAPDocumentImpl ownerDoc, final Name qname) {
        super(ownerDoc, qname);
    }
    
    public BodyElement1_2Impl(final SOAPDocumentImpl ownerDoc, final QName qname) {
        super(ownerDoc, qname);
    }
    
    @Override
    public SOAPElement setElementQName(final QName newName) throws SOAPException {
        final BodyElementImpl copy = new BodyElement1_2Impl((SOAPDocumentImpl)this.getOwnerDocument(), newName);
        return ElementImpl.replaceElementWithSOAPElement(this, copy);
    }
}
