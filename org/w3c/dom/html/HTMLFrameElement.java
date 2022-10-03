package org.w3c.dom.html;

public interface HTMLFrameElement extends HTMLElement
{
    String getFrameBorder();
    
    void setFrameBorder(final String p0);
    
    String getLongDesc();
    
    void setLongDesc(final String p0);
    
    String getMarginHeight();
    
    void setMarginHeight(final String p0);
    
    String getMarginWidth();
    
    void setMarginWidth(final String p0);
    
    String getName();
    
    void setName(final String p0);
    
    boolean getNoResize();
    
    void setNoResize(final boolean p0);
    
    String getScrolling();
    
    void setScrolling(final String p0);
    
    String getSrc();
    
    void setSrc(final String p0);
}
