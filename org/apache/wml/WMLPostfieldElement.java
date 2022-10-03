package org.apache.wml;

public interface WMLPostfieldElement extends WMLElement
{
    void setValue(final String p0);
    
    String getValue();
    
    void setName(final String p0);
    
    String getName();
}
