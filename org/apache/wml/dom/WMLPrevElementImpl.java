package org.apache.wml.dom;

import org.apache.wml.WMLPrevElement;

public class WMLPrevElementImpl extends WMLElementImpl implements WMLPrevElement
{
    private static final long serialVersionUID = -1545713716925433554L;
    
    public WMLPrevElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
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
