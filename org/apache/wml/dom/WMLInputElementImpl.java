package org.apache.wml.dom;

import org.apache.wml.WMLInputElement;

public class WMLInputElementImpl extends WMLElementImpl implements WMLInputElement
{
    private static final long serialVersionUID = 2897319793637966619L;
    
    public WMLInputElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
    }
    
    public void setSize(final int n) {
        this.setAttribute("size", n);
    }
    
    public int getSize() {
        return this.getAttribute("size", 0);
    }
    
    public void setFormat(final String s) {
        this.setAttribute("format", s);
    }
    
    public String getFormat() {
        return this.getAttribute("format");
    }
    
    public void setValue(final String s) {
        this.setAttribute("value", s);
    }
    
    public String getValue() {
        return this.getAttribute("value");
    }
    
    public void setMaxLength(final int n) {
        this.setAttribute("maxlength", n);
    }
    
    public int getMaxLength() {
        return this.getAttribute("maxlength", 0);
    }
    
    public void setTabIndex(final int n) {
        this.setAttribute("tabindex", n);
    }
    
    public int getTabIndex() {
        return this.getAttribute("tabindex", 0);
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
    
    public void setEmptyOk(final boolean b) {
        this.setAttribute("emptyok", b);
    }
    
    public boolean getEmptyOk() {
        return this.getAttribute("emptyok", false);
    }
    
    public void setTitle(final String s) {
        this.setAttribute("title", s);
    }
    
    public String getTitle() {
        return this.getAttribute("title");
    }
    
    public void setId(final String s) {
        this.setAttribute("id", s);
    }
    
    public String getId() {
        return this.getAttribute("id");
    }
    
    public void setType(final String s) {
        this.setAttribute("type", s);
    }
    
    public String getType() {
        return this.getAttribute("type");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
}
