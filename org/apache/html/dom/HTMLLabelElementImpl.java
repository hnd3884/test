package org.apache.html.dom;

import org.w3c.dom.html.HTMLLabelElement;

public class HTMLLabelElementImpl extends HTMLElementImpl implements HTMLLabelElement, HTMLFormControl
{
    private static final long serialVersionUID = 5774388295313199380L;
    
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
    
    public String getHtmlFor() {
        return this.getAttribute("for");
    }
    
    public void setHtmlFor(final String s) {
        this.setAttribute("for", s);
    }
    
    public HTMLLabelElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
