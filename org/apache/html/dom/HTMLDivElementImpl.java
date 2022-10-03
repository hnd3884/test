package org.apache.html.dom;

import org.w3c.dom.html.HTMLDivElement;

public class HTMLDivElementImpl extends HTMLElementImpl implements HTMLDivElement
{
    private static final long serialVersionUID = 2327098984177358833L;
    
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public HTMLDivElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
