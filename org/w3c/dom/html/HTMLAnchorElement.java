package org.w3c.dom.html;

public interface HTMLAnchorElement extends HTMLElement
{
    String getAccessKey();
    
    void setAccessKey(final String p0);
    
    String getCharset();
    
    void setCharset(final String p0);
    
    String getCoords();
    
    void setCoords(final String p0);
    
    String getHref();
    
    void setHref(final String p0);
    
    String getHreflang();
    
    void setHreflang(final String p0);
    
    String getName();
    
    void setName(final String p0);
    
    String getRel();
    
    void setRel(final String p0);
    
    String getRev();
    
    void setRev(final String p0);
    
    String getShape();
    
    void setShape(final String p0);
    
    int getTabIndex();
    
    void setTabIndex(final int p0);
    
    String getTarget();
    
    void setTarget(final String p0);
    
    String getType();
    
    void setType(final String p0);
    
    void blur();
    
    void focus();
}
