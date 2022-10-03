package org.apache.html.dom;

import org.w3c.dom.html.HTMLLIElement;

public class HTMLLIElementImpl extends HTMLElementImpl implements HTMLLIElement
{
    private static final long serialVersionUID = -8987309345926701831L;
    
    public String getType() {
        return this.getAttribute("type");
    }
    
    public void setType(final String s) {
        this.setAttribute("type", s);
    }
    
    public int getValue() {
        return this.getInteger(this.getAttribute("value"));
    }
    
    public void setValue(final int n) {
        this.setAttribute("value", String.valueOf(n));
    }
    
    public HTMLLIElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
