package org.w3c.dom.html;

public interface HTMLFormElement extends HTMLElement
{
    HTMLCollection getElements();
    
    int getLength();
    
    String getName();
    
    void setName(final String p0);
    
    String getAcceptCharset();
    
    void setAcceptCharset(final String p0);
    
    String getAction();
    
    void setAction(final String p0);
    
    String getEnctype();
    
    void setEnctype(final String p0);
    
    String getMethod();
    
    void setMethod(final String p0);
    
    String getTarget();
    
    void setTarget(final String p0);
    
    void submit();
    
    void reset();
}
