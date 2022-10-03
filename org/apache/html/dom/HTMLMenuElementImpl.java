package org.apache.html.dom;

import org.w3c.dom.html.HTMLMenuElement;

public class HTMLMenuElementImpl extends HTMLElementImpl implements HTMLMenuElement
{
    private static final long serialVersionUID = -1489696654903916901L;
    
    public boolean getCompact() {
        return this.getBinary("compact");
    }
    
    public void setCompact(final boolean b) {
        this.setAttribute("compact", b);
    }
    
    public HTMLMenuElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
