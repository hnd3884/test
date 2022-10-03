package org.apache.wml.dom;

import org.apache.wml.WMLNoopElement;

public class WMLNoopElementImpl extends WMLElementImpl implements WMLNoopElement
{
    private static final long serialVersionUID = -1581314434256075931L;
    
    public WMLNoopElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
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
