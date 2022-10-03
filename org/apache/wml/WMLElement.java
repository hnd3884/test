package org.apache.wml;

import org.w3c.dom.Element;

public interface WMLElement extends Element
{
    void setId(final String p0);
    
    String getId();
    
    void setClassName(final String p0);
    
    String getClassName();
}
