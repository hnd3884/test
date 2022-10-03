package org.apache.xmlbeans.impl.soap;

public class SOAPElementFactory
{
    private SOAPFactory sf;
    
    private SOAPElementFactory(final SOAPFactory soapfactory) {
        this.sf = soapfactory;
    }
    
    @Deprecated
    public SOAPElement create(final Name name) throws SOAPException {
        return this.sf.createElement(name);
    }
    
    @Deprecated
    public SOAPElement create(final String localName) throws SOAPException {
        return this.sf.createElement(localName);
    }
    
    @Deprecated
    public SOAPElement create(final String localName, final String prefix, final String uri) throws SOAPException {
        return this.sf.createElement(localName, prefix, uri);
    }
    
    @Deprecated
    public static SOAPElementFactory newInstance() throws SOAPException {
        try {
            return new SOAPElementFactory(SOAPFactory.newInstance());
        }
        catch (final Exception exception) {
            throw new SOAPException("Unable to create SOAP Element Factory: " + exception.getMessage());
        }
    }
}
