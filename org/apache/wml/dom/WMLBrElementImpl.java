package org.apache.wml.dom;

import org.apache.wml.WMLBrElement;

public class WMLBrElementImpl extends WMLElementImpl implements WMLBrElement
{
    private static final long serialVersionUID = -5047802409550691268L;
    
    public WMLBrElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
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
    
    public void setId(final String s) {
        this.setAttribute("id", s);
    }
    
    public String getId() {
        return this.getAttribute("id");
    }
}
