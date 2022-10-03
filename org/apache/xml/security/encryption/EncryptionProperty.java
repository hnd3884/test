package org.apache.xml.security.encryption;

import org.w3c.dom.Element;
import java.util.Iterator;

public interface EncryptionProperty
{
    String getTarget();
    
    void setTarget(final String p0);
    
    String getId();
    
    void setId(final String p0);
    
    String getAttribute(final String p0);
    
    void setAttribute(final String p0, final String p1);
    
    Iterator getEncryptionInformation();
    
    void addEncryptionInformation(final Element p0);
    
    void removeEncryptionInformation(final Element p0);
}
