package org.apache.wml.dom;

import org.apache.wml.WMLSelectElement;

public class WMLSelectElementImpl extends WMLElementImpl implements WMLSelectElement
{
    private static final long serialVersionUID = 6489112443257247261L;
    
    public WMLSelectElementImpl(final WMLDocumentImpl wmlDocumentImpl, final String s) {
        super(wmlDocumentImpl, s);
    }
    
    public void setMultiple(final boolean b) {
        this.setAttribute("multiple", b);
    }
    
    public boolean getMultiple() {
        return this.getAttribute("multiple", false);
    }
    
    public void setValue(final String s) {
        this.setAttribute("value", s);
    }
    
    public String getValue() {
        return this.getAttribute("value");
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
    
    public void setTitle(final String s) {
        this.setAttribute("title", s);
    }
    
    public String getTitle() {
        return this.getAttribute("title");
    }
    
    public void setIValue(final String s) {
        this.setAttribute("ivalue", s);
    }
    
    public String getIValue() {
        return this.getAttribute("ivalue");
    }
    
    public void setId(final String s) {
        this.setAttribute("id", s);
    }
    
    public String getId() {
        return this.getAttribute("id");
    }
    
    public void setIName(final String s) {
        this.setAttribute("iname", s);
    }
    
    public String getIName() {
        return this.getAttribute("iname");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
}
