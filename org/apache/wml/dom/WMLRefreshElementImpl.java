package org.apache.wml.dom;

import org.apache.wml.WMLRefreshElement;

public class WMLRefreshElementImpl extends WMLElementImpl implements WMLRefreshElement
{
    private static final long serialVersionUID = 8781837880806459398L;
    
    public WMLRefreshElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
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
