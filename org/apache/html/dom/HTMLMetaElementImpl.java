package org.apache.html.dom;

import org.w3c.dom.html.HTMLMetaElement;

public class HTMLMetaElementImpl extends HTMLElementImpl implements HTMLMetaElement
{
    private static final long serialVersionUID = -2401961905874264272L;
    
    public String getContent() {
        return this.getAttribute("content");
    }
    
    public void setContent(final String s) {
        this.setAttribute("content", s);
    }
    
    public String getHttpEquiv() {
        return this.getAttribute("http-equiv");
    }
    
    public void setHttpEquiv(final String s) {
        this.setAttribute("http-equiv", s);
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public String getScheme() {
        return this.getAttribute("scheme");
    }
    
    public void setScheme(final String s) {
        this.setAttribute("scheme", s);
    }
    
    public HTMLMetaElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
