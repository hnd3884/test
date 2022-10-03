package org.apache.html.dom;

import org.w3c.dom.html.HTMLParamElement;

public class HTMLParamElementImpl extends HTMLElementImpl implements HTMLParamElement
{
    private static final long serialVersionUID = -8513050483880341412L;
    
    public String getName() {
        return this.getAttribute("name");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public String getType() {
        return this.getAttribute("type");
    }
    
    public void setType(final String s) {
        this.setAttribute("type", s);
    }
    
    public String getValue() {
        return this.getAttribute("value");
    }
    
    public void setValue(final String s) {
        this.setAttribute("value", s);
    }
    
    public String getValueType() {
        return this.capitalize(this.getAttribute("valuetype"));
    }
    
    public void setValueType(final String s) {
        this.setAttribute("valuetype", s);
    }
    
    public HTMLParamElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
