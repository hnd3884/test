package com.sun.xml.internal.messaging.saaj.soap.impl;

import javax.xml.soap.SOAPException;
import java.util.logging.Level;
import javax.xml.soap.SOAPElement;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import javax.xml.soap.SOAPFaultElement;

public abstract class FaultElementImpl extends ElementImpl implements SOAPFaultElement
{
    protected FaultElementImpl(final SOAPDocumentImpl ownerDoc, final NameImpl qname) {
        super(ownerDoc, qname);
    }
    
    protected FaultElementImpl(final SOAPDocumentImpl ownerDoc, final QName qname) {
        super(ownerDoc, qname);
    }
    
    protected abstract boolean isStandardFaultElement();
    
    @Override
    public SOAPElement setElementQName(final QName newName) throws SOAPException {
        FaultElementImpl.log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[] { this.elementQName.getLocalPart(), newName.getLocalPart() });
        throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + newName.getLocalPart());
    }
}
