package org.apache.wml.dom;

import org.apache.wml.WMLAElement;

public class WMLAElementImpl extends WMLElementImpl implements WMLAElement
{
    private static final long serialVersionUID = 2628169803370301255L;
    
    public WMLAElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
    }
    
    public void setHref(final String s) {
        this.setAttribute("href", s);
    }
    
    public String getHref() {
        return this.getAttribute("href");
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
    
    public void setTitle(final String s) {
        this.setAttribute("title", s);
    }
    
    public String getTitle() {
        return this.getAttribute("title");
    }
    
    public void setId(final String s) {
        this.setAttribute("id", s);
    }
    
    public String getId() {
        return this.getAttribute("id");
    }
}
