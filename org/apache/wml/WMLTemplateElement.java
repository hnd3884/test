package org.apache.wml;

public interface WMLTemplateElement extends WMLElement
{
    void setOnTimer(final String p0);
    
    String getOnTimer();
    
    void setOnEnterBackward(final String p0);
    
    String getOnEnterBackward();
    
    void setOnEnterForward(final String p0);
    
    String getOnEnterForward();
}
