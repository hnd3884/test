package javax.xml.crypto;

public interface XMLCryptoContext
{
    String getBaseURI();
    
    void setBaseURI(final String p0);
    
    KeySelector getKeySelector();
    
    void setKeySelector(final KeySelector p0);
    
    URIDereferencer getURIDereferencer();
    
    void setURIDereferencer(final URIDereferencer p0);
    
    String getNamespacePrefix(final String p0, final String p1);
    
    String putNamespacePrefix(final String p0, final String p1);
    
    String getDefaultNamespacePrefix();
    
    void setDefaultNamespacePrefix(final String p0);
    
    Object setProperty(final String p0, final Object p1);
    
    Object getProperty(final String p0);
    
    Object get(final Object p0);
    
    Object put(final Object p0, final Object p1);
}
