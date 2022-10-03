package org.apache.xml.security.encryption;

public interface EncryptedKey extends EncryptedType
{
    String getRecipient();
    
    void setRecipient(final String p0);
    
    ReferenceList getReferenceList();
    
    void setReferenceList(final ReferenceList p0);
    
    String getCarriedName();
    
    void setCarriedName(final String p0);
}
