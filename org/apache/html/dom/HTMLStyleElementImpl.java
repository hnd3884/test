package org.apache.html.dom;

import org.w3c.dom.html.HTMLStyleElement;

public class HTMLStyleElementImpl extends HTMLElementImpl implements HTMLStyleElement
{
    private static final long serialVersionUID = -9001815754196124532L;
    
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }
    
    public void setDisabled(final boolean b) {
        this.setAttribute("disabled", b);
    }
    
    public String getMedia() {
        return this.getAttribute("media");
    }
    
    public void setMedia(final String s) {
        this.setAttribute("media", s);
    }
    
    public String getType() {
        return this.getAttribute("type");
    }
    
    public void setType(final String s) {
        this.setAttribute("type", s);
    }
    
    public HTMLStyleElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
