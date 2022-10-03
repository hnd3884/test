package org.apache.wml.dom;

import org.apache.wml.WMLPElement;

public class WMLPElementImpl extends WMLElementImpl implements WMLPElement
{
    private static final long serialVersionUID = 4263257796458499960L;
    
    public WMLPElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
    }
    
    public void setClassName(final String s) {
        this.setAttribute("class", s);
    }
    
    public String getClassName() {
        return this.getAttribute("class");
    }
    
    public void setMode(final String s) {
        this.setAttribute("mode", s);
    }
    
    public String getMode() {
        return this.getAttribute("mode");
    }
    
    public void setXmlLang(final String s) {
        this.setAttribute("xml:lang", s);
    }
    
    public String getXmlLang() {
        return this.getAttribute("xml:lang");
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public String getAlign() {
        return this.getAttribute("align");
    }
    
    public void setId(final String s) {
        this.setAttribute("id", s);
    }
    
    public String getId() {
        return this.getAttribute("id");
    }
}
