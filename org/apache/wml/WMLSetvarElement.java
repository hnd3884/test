package org.apache.wml;

public interface WMLSetvarElement extends WMLElement
{
    void setValue(final String p0);
    
    String getValue();
    
    void setName(final String p0);
    
    String getName();
}
