package org.apache.wml.dom;

import org.apache.wml.WMLImgElement;

public class WMLImgElementImpl extends WMLElementImpl implements WMLImgElement
{
    private static final long serialVersionUID = -500092034867051550L;
    
    public WMLImgElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
    }
    
    public void setWidth(final String s) {
        this.setAttribute("width", s);
    }
    
    public String getWidth() {
        return this.getAttribute("width");
    }
    
    public void setClassName(final String s) {
        this.setAttribute("class", s);
    }
    
    public String getClassName() {
        return this.getAttribute("class");
    }
    
    public void setXmlLang(final String s) {
        this.setAttribute("xml:lang", s);
    }
    
    public String getXmlLang() {
        return this.getAttribute("xml:lang");
    }
    
    public void setLocalSrc(final String s) {
        this.setAttribute("localsrc", s);
    }
    
    public String getLocalSrc() {
        return this.getAttribute("localsrc");
    }
    
    public void setHeight(final String s) {
        this.setAttribute("height", s);
    }
    
    public String getHeight() {
        return this.getAttribute("height");
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public String getAlign() {
        return this.getAttribute("align");
    }
    
    public void setVspace(final String s) {
        this.setAttribute("vspace", s);
    }
    
    public String getVspace() {
        return this.getAttribute("vspace");
    }
    
    public void setAlt(final String s) {
        this.setAttribute("alt", s);
    }
    
    public String getAlt() {
        return this.getAttribute("alt");
    }
    
    public void setId(final String s) {
        this.setAttribute("id", s);
    }
    
    public String getId() {
        return this.getAttribute("id");
    }
    
    public void setHspace(final String s) {
        this.setAttribute("hspace", s);
    }
    
    public String getHspace() {
        return this.getAttribute("hspace");
    }
    
    public void setSrc(final String s) {
        this.setAttribute("src", s);
    }
    
    public String getSrc() {
        return this.getAttribute("src");
    }
}
