package org.apache.html.dom;

import org.w3c.dom.html.HTMLParagraphElement;

public class HTMLParagraphElementImpl extends HTMLElementImpl implements HTMLParagraphElement
{
    private static final long serialVersionUID = 8075287150683866287L;
    
    public String getAlign() {
        return this.getAttribute("align");
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public HTMLParagraphElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
