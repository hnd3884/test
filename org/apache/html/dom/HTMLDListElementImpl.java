package org.apache.html.dom;

import org.w3c.dom.html.HTMLDListElement;

public class HTMLDListElementImpl extends HTMLElementImpl implements HTMLDListElement
{
    private static final long serialVersionUID = -2130005642453038604L;
    
    public boolean getCompact() {
        return this.getBinary("compact");
    }
    
    public void setCompact(final boolean b) {
        this.setAttribute("compact", b);
    }
    
    public HTMLDListElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
