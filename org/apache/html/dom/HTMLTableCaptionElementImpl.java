package org.apache.html.dom;

import org.w3c.dom.html.HTMLTableCaptionElement;

public class HTMLTableCaptionElementImpl extends HTMLElementImpl implements HTMLTableCaptionElement
{
    private static final long serialVersionUID = 183703024771848940L;
    
    public String getAlign() {
        return this.getAttribute("align");
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public HTMLTableCaptionElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
