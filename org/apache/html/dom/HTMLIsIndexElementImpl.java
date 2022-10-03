package org.apache.html.dom;

import org.w3c.dom.html.HTMLIsIndexElement;

public class HTMLIsIndexElementImpl extends HTMLElementImpl implements HTMLIsIndexElement
{
    private static final long serialVersionUID = 3073521742049689699L;
    
    public String getPrompt() {
        return this.getAttribute("prompt");
    }
    
    public void setPrompt(final String s) {
        this.setAttribute("prompt", s);
    }
    
    public HTMLIsIndexElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
