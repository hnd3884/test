package org.apache.html.dom;

import org.w3c.dom.html.HTMLAppletElement;

public class HTMLAppletElementImpl extends HTMLElementImpl implements HTMLAppletElement
{
    private static final long serialVersionUID = 8375794094117740967L;
    
    public String getAlign() {
        return this.getAttribute("align");
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public String getAlt() {
        return this.getAttribute("alt");
    }
    
    public void setAlt(final String s) {
        this.setAttribute("alt", s);
    }
    
    public String getArchive() {
        return this.getAttribute("archive");
    }
    
    public void setArchive(final String s) {
        this.setAttribute("archive", s);
    }
    
    public String getCode() {
        return this.getAttribute("code");
    }
    
    public void setCode(final String s) {
        this.setAttribute("code", s);
    }
    
    public String getCodeBase() {
        return this.getAttribute("codebase");
    }
    
    public void setCodeBase(final String s) {
        this.setAttribute("codebase", s);
    }
    
    public String getHeight() {
        return this.getAttribute("height");
    }
    
    public void setHeight(final String s) {
        this.setAttribute("height", s);
    }
    
    public String getHspace() {
        return this.getAttribute("height");
    }
    
    public void setHspace(final String s) {
        this.setAttribute("height", s);
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public String getObject() {
        return this.getAttribute("object");
    }
    
    public void setObject(final String s) {
        this.setAttribute("object", s);
    }
    
    public String getVspace() {
        return this.getAttribute("vspace");
    }
    
    public void setVspace(final String s) {
        this.setAttribute("vspace", s);
    }
    
    public String getWidth() {
        return this.getAttribute("width");
    }
    
    public void setWidth(final String s) {
        this.setAttribute("width", s);
    }
    
    public HTMLAppletElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
