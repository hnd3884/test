package org.apache.xml.security.encryption;

import java.util.Iterator;

public interface EncryptionProperties
{
    String getId();
    
    void setId(final String p0);
    
    Iterator getEncryptionProperties();
    
    void addEncryptionProperty(final EncryptionProperty p0);
    
    void removeEncryptionProperty(final EncryptionProperty p0);
}
