package org.apache.html.dom;

import org.w3c.dom.html.HTMLBodyElement;

public class HTMLBodyElementImpl extends HTMLElementImpl implements HTMLBodyElement
{
    private static final long serialVersionUID = 9058852459426595202L;
    
    public String getALink() {
        return this.getAttribute("alink");
    }
    
    public void setALink(final String s) {
        this.setAttribute("alink", s);
    }
    
    public String getBackground() {
        return this.getAttribute("background");
    }
    
    public void setBackground(final String s) {
        this.setAttribute("background", s);
    }
    
    public String getBgColor() {
        return this.getAttribute("bgcolor");
    }
    
    public void setBgColor(final String s) {
        this.setAttribute("bgcolor", s);
    }
    
    public String getLink() {
        return this.getAttribute("link");
    }
    
    public void setLink(final String s) {
        this.setAttribute("link", s);
    }
    
    public String getText() {
        return this.getAttribute("text");
    }
    
    public void setText(final String s) {
        this.setAttribute("text", s);
    }
    
    public String getVLink() {
        return this.getAttribute("vlink");
    }
    
    public void setVLink(final String s) {
        this.setAttribute("vlink", s);
    }
    
    public HTMLBodyElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
