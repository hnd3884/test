package javax.xml.soap;

public abstract class SAAJMetaFactory
{
    private static final String META_FACTORY_CLASS_PROPERTY = "javax.xml.soap.MetaFactory";
    static final String DEFAULT_META_FACTORY_CLASS = "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl";
    
    static SAAJMetaFactory getInstance() throws SOAPException {
        try {
            final SAAJMetaFactory instance = (SAAJMetaFactory)FactoryFinder.find("javax.xml.soap.MetaFactory", "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl");
            return instance;
        }
        catch (final Exception e) {
            throw new SOAPException("Unable to create SAAJ meta-factory" + e.getMessage());
        }
    }
    
    protected SAAJMetaFactory() {
    }
    
    protected abstract MessageFactory newMessageFactory(final String p0) throws SOAPException;
    
    protected abstract SOAPFactory newSOAPFactory(final String p0) throws SOAPException;
}
