package org.apache.wml.dom;

import org.apache.wml.WMLSetvarElement;

public class WMLSetvarElementImpl extends WMLElementImpl implements WMLSetvarElement
{
    private static final long serialVersionUID = -1944519734782236471L;
    
    public WMLSetvarElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
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
    
    public void setId(final String s) {
        this.setAttribute("id", s);
    }
    
    public String getId() {
        return this.getAttribute("id");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
}
