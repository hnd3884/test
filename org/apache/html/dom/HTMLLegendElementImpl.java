package org.apache.html.dom;

import org.w3c.dom.html.HTMLLegendElement;

public class HTMLLegendElementImpl extends HTMLElementImpl implements HTMLLegendElement
{
    private static final long serialVersionUID = -621849164029630762L;
    
    public String getAccessKey() {
        String s = this.getAttribute("accesskey");
        if (s != null && s.length() > 1) {
            s = s.substring(0, 1);
        }
        return s;
    }
    
    public void setAccessKey(String substring) {
        if (substring != null && substring.length() > 1) {
            substring = substring.substring(0, 1);
        }
        this.setAttribute("accesskey", substring);
    }
    
    public String getAlign() {
        return this.getAttribute("align");
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public HTMLLegendElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
