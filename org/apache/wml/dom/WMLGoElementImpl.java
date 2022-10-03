package org.apache.wml.dom;

import org.apache.wml.WMLGoElement;

public class WMLGoElementImpl extends WMLElementImpl implements WMLGoElement
{
    private static final long serialVersionUID = -2052250142899797905L;
    
    public WMLGoElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
    }
    
    public void setSendreferer(final String s) {
        this.setAttribute("sendreferer", s);
    }
    
    public String getSendreferer() {
        return this.getAttribute("sendreferer");
    }
    
    public void setAcceptCharset(final String s) {
        this.setAttribute("accept-charset", s);
    }
    
    public String getAcceptCharset() {
        return this.getAttribute("accept-charset");
    }
    
    public void setHref(final String s) {
        this.setAttribute("href", s);
    }
    
    public String getHref() {
        return this.getAttribute("href");
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
    
    public void setMethod(final String s) {
        this.setAttribute("method", s);
    }
    
    public String getMethod() {
        return this.getAttribute("method");
    }
}
