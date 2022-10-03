package org.apache.xmlbeans.impl.soap;

public abstract class SOAPFactory
{
    private static final String SF_PROPERTY = "javax.xml.soap.SOAPFactory";
    private static final String DEFAULT_SF = "org.apache.axis.soap.SOAPFactoryImpl";
    
    public abstract SOAPElement createElement(final Name p0) throws SOAPException;
    
    public abstract SOAPElement createElement(final String p0) throws SOAPException;
    
    public abstract SOAPElement createElement(final String p0, final String p1, final String p2) throws SOAPException;
    
    public abstract Detail createDetail() throws SOAPException;
    
    public abstract Name createName(final String p0, final String p1, final String p2) throws SOAPException;
    
    public abstract Name createName(final String p0) throws SOAPException;
    
    public static SOAPFactory newInstance() throws SOAPException {
        try {
            return (SOAPFactory)FactoryFinder.find("javax.xml.soap.SOAPFactory", "org.apache.axis.soap.SOAPFactoryImpl");
        }
        catch (final Exception exception) {
            throw new SOAPException("Unable to create SOAP Factory: " + exception.getMessage());
        }
    }
}
