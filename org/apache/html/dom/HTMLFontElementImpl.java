package org.apache.html.dom;

import org.w3c.dom.html.HTMLFontElement;

public class HTMLFontElementImpl extends HTMLElementImpl implements HTMLFontElement
{
    private static final long serialVersionUID = -415914342045846318L;
    
    public String getColor() {
        return this.capitalize(this.getAttribute("color"));
    }
    
    public void setColor(final String s) {
        this.setAttribute("color", s);
    }
    
    public String getFace() {
        return this.capitalize(this.getAttribute("face"));
    }
    
    public void setFace(final String s) {
        this.setAttribute("face", s);
    }
    
    public String getSize() {
        return this.getAttribute("size");
    }
    
    public void setSize(final String s) {
        this.setAttribute("size", s);
    }
    
    public HTMLFontElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
