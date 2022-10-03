package org.apache.xml.security.encryption;

import org.w3c.dom.Element;
import java.util.Iterator;

public interface Reference
{
    String getURI();
    
    void setURI(final String p0);
    
    Iterator getElementRetrievalInformation();
    
    void addElementRetrievalInformation(final Element p0);
    
    void removeElementRetrievalInformation(final Element p0);
}
