package org.apache.html.dom;

import org.w3c.dom.html.HTMLInputElement;

public class HTMLInputElementImpl extends HTMLElementImpl implements HTMLInputElement, HTMLFormControl
{
    private static final long serialVersionUID = 640139325394332007L;
    
    public String getDefaultValue() {
        return this.getAttribute("defaultValue");
    }
    
    public void setDefaultValue(final String s) {
        this.setAttribute("defaultValue", s);
    }
    
    public boolean getDefaultChecked() {
        return this.getBinary("defaultChecked");
    }
    
    public void setDefaultChecked(final boolean b) {
        this.setAttribute("defaultChecked", b);
    }
    
    public String getAccept() {
        return this.getAttribute("accept");
    }
    
    public void setAccept(final String s) {
        this.setAttribute("accept", s);
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
    
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public String getAlt() {
        return this.getAttribute("alt");
    }
    
    public void setAlt(final String s) {
        this.setAttribute("alt", s);
    }
    
    public boolean getChecked() {
        return this.getBinary("checked");
    }
    
    public void setChecked(final boolean b) {
        this.setAttribute("checked", b);
    }
    
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }
    
    public void setDisabled(final boolean b) {
        this.setAttribute("disabled", b);
    }
    
    public int getMaxLength() {
        return this.getInteger(this.getAttribute("maxlength"));
    }
    
    public void setMaxLength(final int n) {
        this.setAttribute("maxlength", String.valueOf(n));
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
    
    public String getSize() {
        return this.getAttribute("size");
    }
    
    public void setSize(final String s) {
        this.setAttribute("size", s);
    }
    
    public String getSrc() {
        return this.getAttribute("src");
    }
    
    public void setSrc(final String s) {
        this.setAttribute("src", s);
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
        return this.getAttribute("type");
    }
    
    public String getUseMap() {
        return this.getAttribute("useMap");
    }
    
    public void setUseMap(final String s) {
        this.setAttribute("useMap", s);
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
    
    public void click() {
    }
    
    public HTMLInputElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
