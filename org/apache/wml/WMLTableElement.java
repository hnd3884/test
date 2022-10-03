package org.apache.wml;

public interface WMLTableElement extends WMLElement
{
    void setTitle(final String p0);
    
    String getTitle();
    
    void setAlign(final String p0);
    
    String getAlign();
    
    void setColumns(final int p0);
    
    int getColumns();
    
    void setXmlLang(final String p0);
    
    String getXmlLang();
}
