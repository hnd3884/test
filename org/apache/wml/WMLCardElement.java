package org.apache.wml;

public interface WMLCardElement extends WMLElement
{
    void setOnEnterBackward(final String p0);
    
    String getOnEnterBackward();
    
    void setOnEnterForward(final String p0);
    
    String getOnEnterForward();
    
    void setOnTimer(final String p0);
    
    String getOnTimer();
    
    void setTitle(final String p0);
    
    String getTitle();
    
    void setNewContext(final boolean p0);
    
    boolean getNewContext();
    
    void setOrdered(final boolean p0);
    
    boolean getOrdered();
    
    void setXmlLang(final String p0);
    
    String getXmlLang();
}
