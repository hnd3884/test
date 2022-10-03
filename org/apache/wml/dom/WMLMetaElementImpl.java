package org.apache.wml.dom;

import org.apache.wml.WMLMetaElement;

public class WMLMetaElementImpl extends WMLElementImpl implements WMLMetaElement
{
    private static final long serialVersionUID = -2791663042188681846L;
    
    public WMLMetaElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
    }
    
    public void setForua(final boolean b) {
        this.setAttribute("forua", b);
    }
    
    public boolean getForua() {
        return this.getAttribute("forua", false);
    }
    
    public void setScheme(final String s) {
        this.setAttribute("scheme", s);
    }
    
    public String getScheme() {
        return this.getAttribute("scheme");
    }
    
    public void setClassName(final String s) {
        this.setAttribute("class", s);
    }
    
    public String getClassName() {
        return this.getAttribute("class");
    }
    
    public void setHttpEquiv(final String s) {
        this.setAttribute("http-equiv", s);
    }
    
    public String getHttpEquiv() {
        return this.getAttribute("http-equiv");
    }
    
    public void setId(final String s) {
        this.setAttribute("id", s);
    }
    
    public String getId() {
        return this.getAttribute("id");
    }
    
    public void setContent(final String s) {
        this.setAttribute("content", s);
    }
    
    public String getContent() {
        return this.getAttribute("content");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
}
