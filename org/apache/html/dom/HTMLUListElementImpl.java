package org.apache.html.dom;

import org.w3c.dom.html.HTMLUListElement;

public class HTMLUListElementImpl extends HTMLElementImpl implements HTMLUListElement
{
    private static final long serialVersionUID = -3220401442015109211L;
    
    public boolean getCompact() {
        return this.getBinary("compact");
    }
    
    public void setCompact(final boolean b) {
        this.setAttribute("compact", b);
    }
    
    public String getType() {
        return this.getAttribute("type");
    }
    
    public void setType(final String s) {
        this.setAttribute("type", s);
    }
    
    public HTMLUListElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
