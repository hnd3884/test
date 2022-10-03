package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.namespace.QName;
import java.util.logging.Level;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import com.sun.xml.internal.messaging.saaj.soap.impl.HeaderImpl;

public class Header1_1Impl extends HeaderImpl
{
    protected static final Logger log;
    
    public Header1_1Impl(final SOAPDocumentImpl ownerDocument, final String prefix) {
        super(ownerDocument, NameImpl.createHeader1_1Name(prefix));
    }
    
    @Override
    protected NameImpl getNotUnderstoodName() {
        Header1_1Impl.log.log(Level.SEVERE, "SAAJ0301.ver1_1.hdr.op.unsupported.in.SOAP1.1", new String[] { "getNotUnderstoodName" });
        throw new UnsupportedOperationException("Not supported by SOAP 1.1");
    }
    
    @Override
    protected NameImpl getUpgradeName() {
        return NameImpl.create("Upgrade", this.getPrefix(), "http://schemas.xmlsoap.org/soap/envelope/");
    }
    
    @Override
    protected NameImpl getSupportedEnvelopeName() {
        return NameImpl.create("SupportedEnvelope", this.getPrefix(), "http://schemas.xmlsoap.org/soap/envelope/");
    }
    
    @Override
    public SOAPHeaderElement addNotUnderstoodHeaderElement(final QName name) throws SOAPException {
        Header1_1Impl.log.log(Level.SEVERE, "SAAJ0301.ver1_1.hdr.op.unsupported.in.SOAP1.1", new String[] { "addNotUnderstoodHeaderElement" });
        throw new UnsupportedOperationException("Not supported by SOAP 1.1");
    }
    
    @Override
    protected SOAPHeaderElement createHeaderElement(final Name name) {
        return new HeaderElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
    }
    
    @Override
    protected SOAPHeaderElement createHeaderElement(final QName name) {
        return new HeaderElement1_1Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");
    }
}
