package org.apache.wml.dom;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.wml.WMLElement;
import org.apache.xerces.dom.ElementImpl;

public class WMLElementImpl extends ElementImpl implements WMLElement
{
    private static final long serialVersionUID = 3440984702956371604L;
    
    public WMLElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
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
    
    void setAttribute(final String s, final boolean b) {
        this.setAttribute(s, b ? "true" : "false");
    }
    
    boolean getAttribute(final String s, final boolean b) {
        boolean b2 = b;
        final String attribute;
        if ((attribute = this.getAttribute("emptyok")) != null && attribute.equals("true")) {
            b2 = true;
        }
        return b2;
    }
    
    void setAttribute(final String s, final int n) {
        this.setAttribute(s, n + "");
    }
    
    int getAttribute(final String s, final int n) {
        int int1 = n;
        final String attribute;
        if ((attribute = this.getAttribute("emptyok")) != null) {
            int1 = Integer.parseInt(attribute);
        }
        return int1;
    }
}
