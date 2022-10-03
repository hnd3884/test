package org.apache.wml;

public interface WMLInputElement extends WMLElement
{
    void setName(final String p0);
    
    String getName();
    
    void setValue(final String p0);
    
    String getValue();
    
    void setType(final String p0);
    
    String getType();
    
    void setFormat(final String p0);
    
    String getFormat();
    
    void setEmptyOk(final boolean p0);
    
    boolean getEmptyOk();
    
    void setSize(final int p0);
    
    int getSize();
    
    void setMaxLength(final int p0);
    
    int getMaxLength();
    
    void setTitle(final String p0);
    
    String getTitle();
    
    void setTabIndex(final int p0);
    
    int getTabIndex();
    
    void setXmlLang(final String p0);
    
    String getXmlLang();
}
