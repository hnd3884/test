package org.w3c.dom.html;

import org.w3c.dom.Element;

public interface HTMLElement extends Element
{
    String getId();
    
    void setId(final String p0);
    
    String getTitle();
    
    void setTitle(final String p0);
    
    String getLang();
    
    void setLang(final String p0);
    
    String getDir();
    
    void setDir(final String p0);
    
    String getClassName();
    
    void setClassName(final String p0);
}
