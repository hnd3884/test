package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.logging.Logger;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;

public class Envelope1_2Impl extends EnvelopeImpl
{
    protected static final Logger log;
    
    public Envelope1_2Impl(final SOAPDocumentImpl ownerDoc, final String prefix) {
        super(ownerDoc, NameImpl.createEnvelope1_2Name(prefix));
    }
    
    public Envelope1_2Impl(final SOAPDocumentImpl ownerDoc, final String prefix, final boolean createHeader, final boolean createBody) throws SOAPException {
        super(ownerDoc, NameImpl.createEnvelope1_2Name(prefix), createHeader, createBody);
    }
    
    @Override
    protected NameImpl getBodyName(final String prefix) {
        return NameImpl.createBody1_2Name(prefix);
    }
    
    @Override
    protected NameImpl getHeaderName(final String prefix) {
        return NameImpl.createHeader1_2Name(prefix);
    }
    
    @Override
    public void setEncodingStyle(final String encodingStyle) throws SOAPException {
        Envelope1_2Impl.log.severe("SAAJ0404.ver1_2.no.encodingStyle.in.envelope");
        throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Envelope");
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
    
    @Override
    public SOAPElement addChildElement(final Name name) throws SOAPException {
        if (this.getBody() != null) {
            Envelope1_2Impl.log.severe("SAAJ0405.ver1_2.body.must.last.in.envelope");
            throw new SOAPExceptionImpl("Body must be the last element in SOAP Envelope");
        }
        return super.addChildElement(name);
    }
    
    @Override
    public SOAPElement addChildElement(final QName name) throws SOAPException {
        if (this.getBody() != null) {
            Envelope1_2Impl.log.severe("SAAJ0405.ver1_2.body.must.last.in.envelope");
            throw new SOAPExceptionImpl("Body must be the last element in SOAP Envelope");
        }
        return super.addChildElement(name);
    }
    
    @Override
    public SOAPElement addTextNode(final String text) throws SOAPException {
        Envelope1_2Impl.log.log(Level.SEVERE, "SAAJ0416.ver1_2.adding.text.not.legal", this.getElementQName());
        throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Envelope is not legal");
    }
    
    static {
        log = Logger.getLogger(Envelope1_2Impl.class.getName(), "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
    }
}
