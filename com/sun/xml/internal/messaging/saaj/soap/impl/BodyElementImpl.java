package com.sun.xml.internal.messaging.saaj.soap.impl;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import javax.xml.soap.SOAPBodyElement;

public abstract class BodyElementImpl extends ElementImpl implements SOAPBodyElement
{
    public BodyElementImpl(final SOAPDocumentImpl ownerDoc, final Name qname) {
        super(ownerDoc, qname);
    }
    
    public BodyElementImpl(final SOAPDocumentImpl ownerDoc, final QName qname) {
        super(ownerDoc, qname);
    }
    
    @Override
    public void setParentElement(final SOAPElement element) throws SOAPException {
        if (!(element instanceof SOAPBody)) {
            BodyElementImpl.log.severe("SAAJ0101.impl.parent.of.body.elem.mustbe.body");
            throw new SOAPException("Parent of a SOAPBodyElement has to be a SOAPBody");
        }
        super.setParentElement(element);
    }
}
