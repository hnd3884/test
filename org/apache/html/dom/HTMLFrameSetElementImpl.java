package org.apache.html.dom;

import org.w3c.dom.html.HTMLFrameSetElement;

public class HTMLFrameSetElementImpl extends HTMLElementImpl implements HTMLFrameSetElement
{
    private static final long serialVersionUID = 8403143821972586708L;
    
    public String getCols() {
        return this.getAttribute("cols");
    }
    
    public void setCols(final String s) {
        this.setAttribute("cols", s);
    }
    
    public String getRows() {
        return this.getAttribute("rows");
    }
    
    public void setRows(final String s) {
        this.setAttribute("rows", s);
    }
    
    public HTMLFrameSetElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
