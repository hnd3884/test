package org.w3c.dom.html;

public interface HTMLLabelElement extends HTMLElement
{
    HTMLFormElement getForm();
    
    String getAccessKey();
    
    void setAccessKey(final String p0);
    
    String getHtmlFor();
    
    void setHtmlFor(final String p0);
}
