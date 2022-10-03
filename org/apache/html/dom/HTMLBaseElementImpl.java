package org.apache.html.dom;

import org.w3c.dom.html.HTMLBaseElement;

public class HTMLBaseElementImpl extends HTMLElementImpl implements HTMLBaseElement
{
    private static final long serialVersionUID = -396648580810072153L;
    
    public String getHref() {
        return this.getAttribute("href");
    }
    
    public void setHref(final String s) {
        this.setAttribute("href", s);
    }
    
    public String getTarget() {
        return this.getAttribute("target");
    }
    
    public void setTarget(final String s) {
        this.setAttribute("target", s);
    }
    
    public HTMLBaseElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
