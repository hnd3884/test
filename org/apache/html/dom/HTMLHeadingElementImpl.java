package org.apache.html.dom;

import org.w3c.dom.html.HTMLHeadingElement;

public class HTMLHeadingElementImpl extends HTMLElementImpl implements HTMLHeadingElement
{
    private static final long serialVersionUID = 6605827989383069095L;
    
    public String getAlign() {
        return this.getCapitalized("align");
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public HTMLHeadingElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
