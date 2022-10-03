package com.sun.xml.internal.messaging.saaj.soap.dynamic;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import com.sun.xml.internal.messaging.saaj.soap.MessageFactoryImpl;

public class SOAPMessageFactoryDynamicImpl extends MessageFactoryImpl
{
    @Override
    public SOAPMessage createMessage() throws SOAPException {
        throw new UnsupportedOperationException("createMessage() not supported for Dynamic Protocol");
    }
}
