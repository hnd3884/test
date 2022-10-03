package org.apache.html.dom;

import org.w3c.dom.html.HTMLHtmlElement;

public class HTMLHtmlElementImpl extends HTMLElementImpl implements HTMLHtmlElement
{
    private static final long serialVersionUID = -4489734201536616166L;
    
    public String getVersion() {
        return this.capitalize(this.getAttribute("version"));
    }
    
    public void setVersion(final String s) {
        this.setAttribute("version", s);
    }
    
    public HTMLHtmlElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
