package org.apache.html.dom;

import org.w3c.dom.html.HTMLModElement;

public class HTMLModElementImpl extends HTMLElementImpl implements HTMLModElement
{
    private static final long serialVersionUID = 6424581972706750120L;
    
    public String getCite() {
        return this.getAttribute("cite");
    }
    
    public void setCite(final String s) {
        this.setAttribute("cite", s);
    }
    
    public String getDateTime() {
        return this.getAttribute("datetime");
    }
    
    public void setDateTime(final String s) {
        this.setAttribute("datetime", s);
    }
    
    public HTMLModElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
