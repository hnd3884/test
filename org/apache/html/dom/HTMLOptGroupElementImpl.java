package org.apache.html.dom;

import org.w3c.dom.html.HTMLOptGroupElement;

public class HTMLOptGroupElementImpl extends HTMLElementImpl implements HTMLOptGroupElement
{
    private static final long serialVersionUID = -8807098641226171501L;
    
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }
    
    public void setDisabled(final boolean b) {
        this.setAttribute("disabled", b);
    }
    
    public String getLabel() {
        return this.capitalize(this.getAttribute("label"));
    }
    
    public void setLabel(final String s) {
        this.setAttribute("label", s);
    }
    
    public HTMLOptGroupElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
