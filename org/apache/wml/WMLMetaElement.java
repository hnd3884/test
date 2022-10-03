package org.apache.wml;

public interface WMLMetaElement extends WMLElement
{
    void setName(final String p0);
    
    String getName();
    
    void setHttpEquiv(final String p0);
    
    String getHttpEquiv();
    
    void setForua(final boolean p0);
    
    boolean getForua();
    
    void setScheme(final String p0);
    
    String getScheme();
    
    void setContent(final String p0);
    
    String getContent();
}
