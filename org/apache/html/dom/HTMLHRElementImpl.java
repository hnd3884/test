package org.apache.html.dom;

import org.w3c.dom.html.HTMLHRElement;

public class HTMLHRElementImpl extends HTMLElementImpl implements HTMLHRElement
{
    private static final long serialVersionUID = -4210053417678939270L;
    
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public boolean getNoShade() {
        return this.getBinary("noshade");
    }
    
    public void setNoShade(final boolean b) {
        this.setAttribute("noshade", b);
    }
    
    public String getSize() {
        return this.getAttribute("size");
    }
    
    public void setSize(final String s) {
        this.setAttribute("size", s);
    }
    
    public String getWidth() {
        return this.getAttribute("width");
    }
    
    public void setWidth(final String s) {
        this.setAttribute("width", s);
    }
    
    public HTMLHRElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
