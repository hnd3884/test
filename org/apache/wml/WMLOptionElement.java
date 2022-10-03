package org.apache.wml;

public interface WMLOptionElement extends WMLElement
{
    void setValue(final String p0);
    
    String getValue();
    
    void setTitle(final String p0);
    
    String getTitle();
    
    void setOnPick(final String p0);
    
    String getOnPick();
    
    void setXmlLang(final String p0);
    
    String getXmlLang();
}
