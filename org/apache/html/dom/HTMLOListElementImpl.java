package org.apache.html.dom;

import org.w3c.dom.html.HTMLOListElement;

public class HTMLOListElementImpl extends HTMLElementImpl implements HTMLOListElement
{
    private static final long serialVersionUID = 1293750546025862146L;
    
    public boolean getCompact() {
        return this.getBinary("compact");
    }
    
    public void setCompact(final boolean b) {
        this.setAttribute("compact", b);
    }
    
    public int getStart() {
        return this.getInteger(this.getAttribute("start"));
    }
    
    public void setStart(final int n) {
        this.setAttribute("start", String.valueOf(n));
    }
    
    public String getType() {
        return this.getAttribute("type");
    }
    
    public void setType(final String s) {
        this.setAttribute("type", s);
    }
    
    public HTMLOListElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
