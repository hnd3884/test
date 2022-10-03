package org.apache.xmlbeans.impl.soap;

public abstract class SOAPConnectionFactory
{
    private static final String DEFAULT_SOAP_CONNECTION_FACTORY = "org.apache.axis.soap.SOAPConnectionFactoryImpl";
    private static final String SF_PROPERTY = "javax.xml.soap.SOAPConnectionFactory";
    
    public static SOAPConnectionFactory newInstance() throws SOAPException, UnsupportedOperationException {
        try {
            return (SOAPConnectionFactory)FactoryFinder.find("javax.xml.soap.SOAPConnectionFactory", "org.apache.axis.soap.SOAPConnectionFactoryImpl");
        }
        catch (final Exception exception) {
            throw new SOAPException("Unable to create SOAP connection factory: " + exception.getMessage());
        }
    }
    
    public abstract SOAPConnection createConnection() throws SOAPException;
}
