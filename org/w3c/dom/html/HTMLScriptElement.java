package org.w3c.dom.html;

public interface HTMLScriptElement extends HTMLElement
{
    String getText();
    
    void setText(final String p0);
    
    String getHtmlFor();
    
    void setHtmlFor(final String p0);
    
    String getEvent();
    
    void setEvent(final String p0);
    
    String getCharset();
    
    void setCharset(final String p0);
    
    boolean getDefer();
    
    void setDefer(final boolean p0);
    
    String getSrc();
    
    void setSrc(final String p0);
    
    String getType();
    
    void setType(final String p0);
}
