package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import javax.xml.soap.SOAPElement;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPFault;
import java.util.Locale;
import javax.xml.namespace.QName;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.BodyImpl;

public class Body1_1Impl extends BodyImpl
{
    public Body1_1Impl(final SOAPDocumentImpl ownerDocument, final String prefix) {
        super(ownerDocument, NameImpl.createBody1_1Name(prefix));
    }
    
    public SOAPFault addSOAP12Fault(final QName faultCode, final String faultReason, final Locale locale) {
        throw new UnsupportedOperationException("Not supported in SOAP 1.1");
    }
    
    @Override
    protected NameImpl getFaultName(final String name) {
        return NameImpl.createFault1_1Name(null);
    }
    
    @Override
    protected SOAPBodyElement createBodyElement(final Name name) {
        return new BodyElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
    }
    
    @Override
    protected SOAPBodyElement createBodyElement(final QName name) {
        return new BodyElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
    }
    
    @Override
    protected QName getDefaultFaultCode() {
        return new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server");
    }
    
    @Override
    protected boolean isFault(final SOAPElement child) {
        return child.getElementName().equals(this.getFaultName(null));
    }
    
    @Override
    protected SOAPFault createFaultElement() {
        return new Fault1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), this.getPrefix());
    }
}
