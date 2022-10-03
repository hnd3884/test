package org.apache.html.dom;

import org.w3c.dom.html.HTMLImageElement;

public class HTMLImageElementImpl extends HTMLElementImpl implements HTMLImageElement
{
    private static final long serialVersionUID = 1424360710977241315L;
    
    public String getLowSrc() {
        return this.getAttribute("lowsrc");
    }
    
    public void setLowSrc(final String s) {
        this.setAttribute("lowsrc", s);
    }
    
    public String getSrc() {
        return this.getAttribute("src");
    }
    
    public void setSrc(final String s) {
        this.setAttribute("src", s);
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
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
    
    public String getBorder() {
        return this.getAttribute("border");
    }
    
    public void setBorder(final String s) {
        this.setAttribute("border", s);
    }
    
    public String getHeight() {
        return this.getAttribute("height");
    }
    
    public void setHeight(final String s) {
        this.setAttribute("height", s);
    }
    
    public String getHspace() {
        return this.getAttribute("hspace");
    }
    
    public void setHspace(final String s) {
        this.setAttribute("hspace", s);
    }
    
    public boolean getIsMap() {
        return this.getBinary("ismap");
    }
    
    public void setIsMap(final boolean b) {
        this.setAttribute("ismap", b);
    }
    
    public String getLongDesc() {
        return this.getAttribute("longdesc");
    }
    
    public void setLongDesc(final String s) {
        this.setAttribute("longdesc", s);
    }
    
    public String getUseMap() {
        return this.getAttribute("useMap");
    }
    
    public void setUseMap(final String s) {
        this.setAttribute("useMap", s);
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
    
    public HTMLImageElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
