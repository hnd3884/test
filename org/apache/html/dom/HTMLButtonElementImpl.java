package org.apache.html.dom;

import org.w3c.dom.html.HTMLButtonElement;

public class HTMLButtonElementImpl extends HTMLElementImpl implements HTMLButtonElement, HTMLFormControl
{
    private static final long serialVersionUID = -753685852948076730L;
    
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
    
    public int getTabIndex() {
        try {
            return Integer.parseInt(this.getAttribute("tabindex"));
        }
        catch (final NumberFormatException ex) {
            return 0;
        }
    }
    
    public void setTabIndex(final int n) {
        this.setAttribute("tabindex", String.valueOf(n));
    }
    
    public String getType() {
        return this.capitalize(this.getAttribute("type"));
    }
    
    public String getValue() {
        return this.getAttribute("value");
    }
    
    public void setValue(final String s) {
        this.setAttribute("value", s);
    }
    
    public HTMLButtonElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
