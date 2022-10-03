package org.apache.html.dom;

import org.w3c.dom.html.HTMLTableColElement;

public class HTMLTableColElementImpl extends HTMLElementImpl implements HTMLTableColElement
{
    private static final long serialVersionUID = -6189626162811911792L;
    
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public String getCh() {
        String s = this.getAttribute("char");
        if (s != null && s.length() > 1) {
            s = s.substring(0, 1);
        }
        return s;
    }
    
    public void setCh(String substring) {
        if (substring != null && substring.length() > 1) {
            substring = substring.substring(0, 1);
        }
        this.setAttribute("char", substring);
    }
    
    public String getChOff() {
        return this.getAttribute("charoff");
    }
    
    public void setChOff(final String s) {
        this.setAttribute("charoff", s);
    }
    
    public int getSpan() {
        return this.getInteger(this.getAttribute("span"));
    }
    
    public void setSpan(final int n) {
        this.setAttribute("span", String.valueOf(n));
    }
    
    public String getVAlign() {
        return this.capitalize(this.getAttribute("valign"));
    }
    
    public void setVAlign(final String s) {
        this.setAttribute("valign", s);
    }
    
    public String getWidth() {
        return this.getAttribute("width");
    }
    
    public void setWidth(final String s) {
        this.setAttribute("width", s);
    }
    
    public HTMLTableColElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
