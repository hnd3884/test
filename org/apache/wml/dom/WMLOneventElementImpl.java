package org.apache.wml.dom;

import org.apache.wml.WMLOneventElement;

public class WMLOneventElementImpl extends WMLElementImpl implements WMLOneventElement
{
    private static final long serialVersionUID = -4279215241146570871L;
    
    public WMLOneventElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
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
    
    public void setType(final String s) {
        this.setAttribute("type", s);
    }
    
    public String getType() {
        return this.getAttribute("type");
    }
}
