package org.apache.wml.dom;

import org.apache.wml.WMLDoElement;

public class WMLDoElementImpl extends WMLElementImpl implements WMLDoElement
{
    private static final long serialVersionUID = 7755861458464251322L;
    
    public WMLDoElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
    }
    
    public void setOptional(final String s) {
        this.setAttribute("optional", s);
    }
    
    public String getOptional() {
        return this.getAttribute("optional");
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
    
    public void setLabel(final String s) {
        this.setAttribute("label", s);
    }
    
    public String getLabel() {
        return this.getAttribute("label");
    }
    
    public void setType(final String s) {
        this.setAttribute("type", s);
    }
    
    public String getType() {
        return this.getAttribute("type");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
}
