package org.apache.html.dom;

import org.w3c.dom.html.HTMLHeadElement;

public class HTMLHeadElementImpl extends HTMLElementImpl implements HTMLHeadElement
{
    private static final long serialVersionUID = 6438668473721292232L;
    
    public String getProfile() {
        return this.getAttribute("profile");
    }
    
    public void setProfile(final String s) {
        this.setAttribute("profile", s);
    }
    
    public HTMLHeadElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
