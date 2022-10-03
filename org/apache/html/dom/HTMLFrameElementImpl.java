package org.apache.html.dom;

import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLFrameElement;

public class HTMLFrameElementImpl extends HTMLElementImpl implements HTMLFrameElement
{
    private static final long serialVersionUID = 635237057173695984L;
    
    public String getFrameBorder() {
        return this.getAttribute("frameborder");
    }
    
    public void setFrameBorder(final String s) {
        this.setAttribute("frameborder", s);
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
    
    public boolean getNoResize() {
        return this.getBinary("noresize");
    }
    
    public void setNoResize(final boolean b) {
        this.setAttribute("noresize", b);
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
    
    public Document getContentDocument() {
        return null;
    }
    
    public HTMLFrameElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
