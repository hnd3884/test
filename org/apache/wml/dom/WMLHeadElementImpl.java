package org.apache.wml.dom;

import org.apache.wml.WMLHeadElement;

public class WMLHeadElementImpl extends WMLElementImpl implements WMLHeadElement
{
    private static final long serialVersionUID = 3311307374813188908L;
    
    public WMLHeadElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
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
}
