package org.apache.html.dom;

import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLIFrameElement;

public class HTMLIFrameElementImpl extends HTMLElementImpl implements HTMLIFrameElement
{
    private static final long serialVersionUID = 2393622754706230429L;
    
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public String getFrameBorder() {
        return this.getAttribute("frameborder");
    }
    
    public void setFrameBorder(final String s) {
        this.setAttribute("frameborder", s);
    }
    
    public String getHeight() {
        return this.getAttribute("height");
    }
    
    public void setHeight(final String s) {
        this.setAttribute("height", s);
    }
    
    public String getLongDesc() {
        return this.getAttribute("longdesc");
    }
    
    public void setLongDesc(final String s) {
        this.setAttribute("longdesc", s);
    }
    
    public String getMarginHeight() {
        return this.getAttribute("marginheight");
    }
    
    public void setMarginHeight(final String s) {
        this.setAttribute("marginheight", s);
    }
    
    public String getMarginWidth() {
        return this.getAttribute("marginwidth");
    }
    
    public void setMarginWidth(final String s) {
        this.setAttribute("marginwidth", s);
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public String getScrolling() {
        return this.capitalize(this.getAttribute("scrolling"));
    }
    
    public void setScrolling(final String s) {
        this.setAttribute("scrolling", s);
    }
    
    public String getSrc() {
        return this.getAttribute("src");
    }
    
    public void setSrc(final String s) {
        this.setAttribute("src", s);
    }
    
    public String getWidth() {
        return this.getAttribute("width");
    }
    
    public void setWidth(final String s) {
        this.setAttribute("width", s);
    }
    
    public Document getContentDocument() {
        return null;
    }
    
    public HTMLIFrameElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
