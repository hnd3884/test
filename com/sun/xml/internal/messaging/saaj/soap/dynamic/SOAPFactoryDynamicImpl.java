package com.sun.xml.internal.messaging.saaj.soap.dynamic;

import javax.xml.soap.SOAPException;
import javax.xml.soap.Detail;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPFactoryImpl;

public class SOAPFactoryDynamicImpl extends SOAPFactoryImpl
{
    @Override
    protected SOAPDocumentImpl createDocument() {
        return null;
    }
    
    @Override
    public Detail createDetail() throws SOAPException {
        throw new UnsupportedOperationException("createDetail() not supported for Dynamic Protocol");
    }
}
