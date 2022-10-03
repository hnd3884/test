package org.apache.wml.dom;

import org.apache.wml.WMLOptionElement;

public class WMLOptionElementImpl extends WMLElementImpl implements WMLOptionElement
{
    private static final long serialVersionUID = -3432299264888771937L;
    
    public WMLOptionElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
    }
    
    public void setValue(final String s) {
        this.setAttribute("value", s);
    }
    
    public String getValue() {
        return this.getAttribute("value");
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
    
    public void setOnPick(final String s) {
        this.setAttribute("onpick", s);
    }
    
    public String getOnPick() {
        return this.getAttribute("onpick");
    }
}
