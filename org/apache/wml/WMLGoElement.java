package org.apache.wml;

public interface WMLGoElement extends WMLElement
{
    void setSendreferer(final String p0);
    
    String getSendreferer();
    
    void setAcceptCharset(final String p0);
    
    String getAcceptCharset();
    
    void setHref(final String p0);
    
    String getHref();
    
    void setMethod(final String p0);
    
    String getMethod();
}
