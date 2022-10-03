package org.apache.xml.security.encryption;

public interface CipherData
{
    public static final int VALUE_TYPE = 1;
    public static final int REFERENCE_TYPE = 2;
    
    int getDataType();
    
    CipherValue getCipherValue();
    
    void setCipherValue(final CipherValue p0) throws XMLEncryptionException;
    
    CipherReference getCipherReference();
    
    void setCipherReference(final CipherReference p0) throws XMLEncryptionException;
}
