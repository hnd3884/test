package org.apache.html.dom;

import org.w3c.dom.html.HTMLAreaElement;

public class HTMLAreaElementImpl extends HTMLElementImpl implements HTMLAreaElement
{
    private static final long serialVersionUID = 7164004431531608995L;
    
    public String getAccessKey() {
        String s = this.getAttribute("accesskey");
        if (s != null && s.length() > 1) {
            s = s.substring(0, 1);
        }
        return s;
    }
    
    public void setAccessKey(String substring) {
        if (substring != null && substring.length() > 1) {
            substring = substring.substring(0, 1);
        }
        this.setAttribute("accesskey", substring);
    }
    
    public String getAlt() {
        return this.getAttribute("alt");
    }
    
    public void setAlt(final String s) {
        this.setAttribute("alt", s);
    }
    
    public String getCoords() {
        return this.getAttribute("coords");
    }
    
    public void setCoords(final String s) {
        this.setAttribute("coords", s);
    }
    
    public String getHref() {
        return this.getAttribute("href");
    }
    
    public void setHref(final String s) {
        this.setAttribute("href", s);
    }
    
    public boolean getNoHref() {
        return this.getBinary("nohref");
    }
    
    public void setNoHref(final boolean b) {
        this.setAttribute("nohref", b);
    }
    
    public String getShape() {
        return this.capitalize(this.getAttribute("shape"));
    }
    
    public void setShape(final String s) {
        this.setAttribute("shape", s);
    }
    
    public int getTabIndex() {
        return this.getInteger(this.getAttribute("tabindex"));
    }
    
    public void setTabIndex(final int n) {
        this.setAttribute("tabindex", String.valueOf(n));
    }
    
    public String getTarget() {
        return this.getAttribute("target");
    }
    
    public void setTarget(final String s) {
        this.setAttribute("target", s);
    }
    
    public HTMLAreaElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
