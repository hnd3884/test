package org.apache.wml.dom;

import org.apache.wml.WMLAccessElement;

public class WMLAccessElementImpl extends WMLElementImpl implements WMLAccessElement
{
    private static final long serialVersionUID = -235627181817012806L;
    
    public WMLAccessElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
    }
    
    public void setClassName(final String s) {
        this.setAttribute("class", s);
    }
    
    public String getClassName() {
        return this.getAttribute("class");
    }
    
    public void setDomain(final String s) {
        this.setAttribute("domain", s);
    }
    
    public String getDomain() {
        return this.getAttribute("domain");
    }
    
    public void setId(final String s) {
        this.setAttribute("id", s);
    }
    
    public String getId() {
        return this.getAttribute("id");
    }
    
    public void setPath(final String s) {
        this.setAttribute("path", s);
    }
    
    public String getPath() {
        return this.getAttribute("path");
    }
}
