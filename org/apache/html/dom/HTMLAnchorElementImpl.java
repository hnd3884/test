package org.apache.html.dom;

import org.w3c.dom.html.HTMLAnchorElement;

public class HTMLAnchorElementImpl extends HTMLElementImpl implements HTMLAnchorElement
{
    private static final long serialVersionUID = -140558580924061847L;
    
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
    
    public String getCharset() {
        return this.getAttribute("charset");
    }
    
    public void setCharset(final String s) {
        this.setAttribute("charset", s);
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
    
    public String getHreflang() {
        return this.getAttribute("hreflang");
    }
    
    public void setHreflang(final String s) {
        this.setAttribute("hreflang", s);
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public String getRel() {
        return this.getAttribute("rel");
    }
    
    public void setRel(final String s) {
        this.setAttribute("rel", s);
    }
    
    public String getRev() {
        return this.getAttribute("rev");
    }
    
    public void setRev(final String s) {
        this.setAttribute("rev", s);
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
    
    public String getType() {
        return this.getAttribute("type");
    }
    
    public void setType(final String s) {
        this.setAttribute("type", s);
    }
    
    public void blur() {
    }
    
    public void focus() {
    }
    
    public HTMLAnchorElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
