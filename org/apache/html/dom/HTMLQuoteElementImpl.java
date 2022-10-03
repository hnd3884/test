package org.apache.html.dom;

import org.w3c.dom.html.HTMLQuoteElement;

public class HTMLQuoteElementImpl extends HTMLElementImpl implements HTMLQuoteElement
{
    private static final long serialVersionUID = -67544811597906132L;
    
    public String getCite() {
        return this.getAttribute("cite");
    }
    
    public void setCite(final String s) {
        this.setAttribute("cite", s);
    }
    
    public HTMLQuoteElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
