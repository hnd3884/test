package org.apache.xmlbeans.impl.soap;

import java.io.IOException;
import java.io.InputStream;

public abstract class MessageFactory
{
    private static final String DEFAULT_MESSAGE_FACTORY = "org.apache.axis.soap.MessageFactoryImpl";
    private static final String MESSAGE_FACTORY_PROPERTY = "javax.xml.soap.MessageFactory";
    
    public static MessageFactory newInstance() throws SOAPException {
        try {
            return (MessageFactory)FactoryFinder.find("javax.xml.soap.MessageFactory", "org.apache.axis.soap.MessageFactoryImpl");
        }
        catch (final Exception exception) {
            throw new SOAPException("Unable to create message factory for SOAP: " + exception.getMessage());
        }
    }
    
    public abstract SOAPMessage createMessage() throws SOAPException;
    
    public abstract SOAPMessage createMessage(final MimeHeaders p0, final InputStream p1) throws IOException, SOAPException;
}
