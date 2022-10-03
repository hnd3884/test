package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.soap.dynamic.SOAPFactoryDynamicImpl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.SOAPFactory1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPException;
import java.util.logging.Level;
import com.sun.xml.internal.messaging.saaj.soap.dynamic.SOAPMessageFactoryDynamicImpl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.SOAPMessageFactory1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl;
import javax.xml.soap.MessageFactory;
import java.util.logging.Logger;
import javax.xml.soap.SAAJMetaFactory;

public class SAAJMetaFactoryImpl extends SAAJMetaFactory
{
    protected static final Logger log;
    
    @Override
    protected MessageFactory newMessageFactory(final String protocol) throws SOAPException {
        if ("SOAP 1.1 Protocol".equals(protocol)) {
            return new SOAPMessageFactory1_1Impl();
        }
        if ("SOAP 1.2 Protocol".equals(protocol)) {
            return new SOAPMessageFactory1_2Impl();
        }
        if ("Dynamic Protocol".equals(protocol)) {
            return new SOAPMessageFactoryDynamicImpl();
        }
        SAAJMetaFactoryImpl.log.log(Level.SEVERE, "SAAJ0569.soap.unknown.protocol", new Object[] { protocol, "MessageFactory" });
        throw new SOAPException("Unknown Protocol: " + protocol + "  specified for creating MessageFactory");
    }
    
    @Override
    protected SOAPFactory newSOAPFactory(final String protocol) throws SOAPException {
        if ("SOAP 1.1 Protocol".equals(protocol)) {
            return new SOAPFactory1_1Impl();
        }
        if ("SOAP 1.2 Protocol".equals(protocol)) {
            return new SOAPFactory1_2Impl();
        }
        if ("Dynamic Protocol".equals(protocol)) {
            return new SOAPFactoryDynamicImpl();
        }
        SAAJMetaFactoryImpl.log.log(Level.SEVERE, "SAAJ0569.soap.unknown.protocol", new Object[] { protocol, "SOAPFactory" });
        throw new SOAPException("Unknown Protocol: " + protocol + "  specified for creating SOAPFactory");
    }
    
    static {
        log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
    }
}
