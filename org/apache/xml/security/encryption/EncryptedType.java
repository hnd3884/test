package org.apache.xml.security.encryption;

import org.apache.xml.security.keys.KeyInfo;

public interface EncryptedType
{
    String getId();
    
    void setId(final String p0);
    
    String getType();
    
    void setType(final String p0);
    
    String getMimeType();
    
    void setMimeType(final String p0);
    
    String getEncoding();
    
    void setEncoding(final String p0);
    
    EncryptionMethod getEncryptionMethod();
    
    void setEncryptionMethod(final EncryptionMethod p0);
    
    KeyInfo getKeyInfo();
    
    void setKeyInfo(final KeyInfo p0);
    
    CipherData getCipherData();
    
    EncryptionProperties getEncryptionProperties();
    
    void setEncryptionProperties(final EncryptionProperties p0);
}
