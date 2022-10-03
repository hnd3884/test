package org.apache.wml.dom;

import org.apache.wml.WMLTrElement;

public class WMLTrElementImpl extends WMLElementImpl implements WMLTrElement
{
    private static final long serialVersionUID = -4304021232051604343L;
    
    public WMLTrElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
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
