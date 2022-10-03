package org.apache.wml;

public interface WMLSelectElement extends WMLElement
{
    void setTabIndex(final int p0);
    
    int getTabIndex();
    
    void setMultiple(final boolean p0);
    
    boolean getMultiple();
    
    void setName(final String p0);
    
    String getName();
    
    void setValue(final String p0);
    
    String getValue();
    
    void setTitle(final String p0);
    
    String getTitle();
    
    void setIName(final String p0);
    
    String getIName();
    
    void setIValue(final String p0);
    
    String getIValue();
    
    void setXmlLang(final String p0);
    
    String getXmlLang();
}
