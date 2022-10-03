package org.apache.html.dom;

import org.w3c.dom.html.HTMLPreElement;

public class HTMLPreElementImpl extends HTMLElementImpl implements HTMLPreElement
{
    private static final long serialVersionUID = -4195360849946217644L;
    
    public int getWidth() {
        return this.getInteger(this.getAttribute("width"));
    }
    
    public void setWidth(final int n) {
        this.setAttribute("width", String.valueOf(n));
    }
    
    public HTMLPreElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
