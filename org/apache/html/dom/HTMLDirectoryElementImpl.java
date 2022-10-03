package org.apache.html.dom;

import org.w3c.dom.html.HTMLDirectoryElement;

public class HTMLDirectoryElementImpl extends HTMLElementImpl implements HTMLDirectoryElement
{
    private static final long serialVersionUID = -1010376135190194454L;
    
    public boolean getCompact() {
        return this.getBinary("compact");
    }
    
    public void setCompact(final boolean b) {
        this.setAttribute("compact", b);
    }
    
    public HTMLDirectoryElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
