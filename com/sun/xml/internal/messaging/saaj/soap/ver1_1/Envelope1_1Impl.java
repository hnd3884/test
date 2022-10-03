package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import javax.xml.soap.SOAPException;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;

public class Envelope1_1Impl extends EnvelopeImpl
{
    public Envelope1_1Impl(final SOAPDocumentImpl ownerDoc, final String prefix) {
        super(ownerDoc, NameImpl.createEnvelope1_1Name(prefix));
    }
    
    Envelope1_1Impl(final SOAPDocumentImpl ownerDoc, final String prefix, final boolean createHeader, final boolean createBody) throws SOAPException {
        super(ownerDoc, NameImpl.createEnvelope1_1Name(prefix), createHeader, createBody);
    }
    
    @Override
    protected NameImpl getBodyName(final String prefix) {
        return NameImpl.createBody1_1Name(prefix);
    }
    
    @Override
    protected NameImpl getHeaderName(final String prefix) {
        return NameImpl.createHeader1_1Name(prefix);
    }
}
