package org.apache.html.dom;

import org.w3c.dom.html.HTMLTextAreaElement;

public class HTMLTextAreaElementImpl extends HTMLElementImpl implements HTMLTextAreaElement, HTMLFormControl
{
    private static final long serialVersionUID = -6737778308542678104L;
    
    public String getDefaultValue() {
        return this.getAttribute("default-value");
    }
    
    public void setDefaultValue(final String s) {
        this.setAttribute("default-value", s);
    }
    
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
    
    public int getCols() {
        return this.getInteger(this.getAttribute("cols"));
    }
    
    public void setCols(final int n) {
        this.setAttribute("cols", String.valueOf(n));
    }
    
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }
    
    public void setDisabled(final boolean b) {
        this.setAttribute("disabled", b);
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public boolean getReadOnly() {
        return this.getBinary("readonly");
    }
    
    public void setReadOnly(final boolean b) {
        this.setAttribute("readonly", b);
    }
    
    public int getRows() {
        return this.getInteger(this.getAttribute("rows"));
    }
    
    public void setRows(final int n) {
        this.setAttribute("rows", String.valueOf(n));
    }
    
    public int getTabIndex() {
        return this.getInteger(this.getAttribute("tabindex"));
    }
    
    public void setTabIndex(final int n) {
        this.setAttribute("tabindex", String.valueOf(n));
    }
    
    public String getType() {
        return this.getAttribute("type");
    }
    
    public String getValue() {
        return this.getAttribute("value");
    }
    
    public void setValue(final String s) {
        this.setAttribute("value", s);
    }
    
    public void blur() {
    }
    
    public void focus() {
    }
    
    public void select() {
    }
    
    public HTMLTextAreaElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
