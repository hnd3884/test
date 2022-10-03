package com.sun.xml.internal.messaging.saaj.client.p2p;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;

public class HttpSOAPConnectionFactory extends SOAPConnectionFactory
{
    @Override
    public SOAPConnection createConnection() throws SOAPException {
        return new HttpSOAPConnection();
    }
}
