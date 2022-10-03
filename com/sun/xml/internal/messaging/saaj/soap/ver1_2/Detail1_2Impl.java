package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import javax.xml.namespace.QName;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailImpl;

public class Detail1_2Impl extends DetailImpl
{
    protected static final Logger log;
    
    public Detail1_2Impl(final SOAPDocumentImpl ownerDocument, final String prefix) {
        super(ownerDocument, NameImpl.createSOAP12Name("Detail", prefix));
    }
    
    public Detail1_2Impl(final SOAPDocumentImpl ownerDocument) {
        super(ownerDocument, NameImpl.createSOAP12Name("Detail"));
    }
    
    @Override
    protected DetailEntry createDetailEntry(final Name name) {
        return new DetailEntry1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
    }
    
    @Override
    protected DetailEntry createDetailEntry(final QName name) {
        return new DetailEntry1_2Impl(((SOAPDocument)this.getOwnerDocument()).getDocument(), name);
    }
    
    @Override
    public void setEncodingStyle(final String encodingStyle) throws SOAPException {
        Detail1_2Impl.log.severe("SAAJ0403.ver1_2.no.encodingStyle.in.detail");
        throw new SOAPExceptionImpl("EncodingStyle attribute cannot appear in Detail");
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
        log = Logger.getLogger(Detail1_2Impl.class.getName(), "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
    }
}
