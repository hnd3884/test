package org.apache.xml.security.encryption;

import org.w3c.dom.Element;
import java.util.Iterator;

public interface EncryptionMethod
{
    String getAlgorithm();
    
    int getKeySize();
    
    void setKeySize(final int p0);
    
    byte[] getOAEPparams();
    
    void setOAEPparams(final byte[] p0);
    
    Iterator getEncryptionMethodInformation();
    
    void addEncryptionMethodInformation(final Element p0);
    
    void removeEncryptionMethodInformation(final Element p0);
}
