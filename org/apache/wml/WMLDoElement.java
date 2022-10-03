package org.apache.wml;

public interface WMLDoElement extends WMLElement
{
    void setOptional(final String p0);
    
    String getOptional();
    
    void setLabel(final String p0);
    
    String getLabel();
    
    void setType(final String p0);
    
    String getType();
    
    void setName(final String p0);
    
    String getName();
    
    void setXmlLang(final String p0);
    
    String getXmlLang();
}
