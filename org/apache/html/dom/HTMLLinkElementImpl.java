package org.apache.html.dom;

import org.w3c.dom.html.HTMLLinkElement;

public class HTMLLinkElementImpl extends HTMLElementImpl implements HTMLLinkElement
{
    private static final long serialVersionUID = 874345520063418879L;
    
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }
    
    public void setDisabled(final boolean b) {
        this.setAttribute("disabled", b);
    }
    
    public String getCharset() {
        return this.getAttribute("charset");
    }
    
    public void setCharset(final String s) {
        this.setAttribute("charset", s);
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
    
    public String getMedia() {
        return this.getAttribute("media");
    }
    
    public void setMedia(final String s) {
        this.setAttribute("media", s);
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
    
    public HTMLLinkElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
