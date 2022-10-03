package org.apache.html.dom;

import org.w3c.dom.Document;
import org.w3c.dom.html.HTMLObjectElement;

public class HTMLObjectElementImpl extends HTMLElementImpl implements HTMLObjectElement, HTMLFormControl
{
    private static final long serialVersionUID = 2276953229932965067L;
    
    public String getCode() {
        return this.getAttribute("code");
    }
    
    public void setCode(final String s) {
        this.setAttribute("code", s);
    }
    
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public String getArchive() {
        return this.getAttribute("archive");
    }
    
    public void setArchive(final String s) {
        this.setAttribute("archive", s);
    }
    
    public String getBorder() {
        return this.getAttribute("border");
    }
    
    public void setBorder(final String s) {
        this.setAttribute("border", s);
    }
    
    public String getCodeBase() {
        return this.getAttribute("codebase");
    }
    
    public void setCodeBase(final String s) {
        this.setAttribute("codebase", s);
    }
    
    public String getCodeType() {
        return this.getAttribute("codetype");
    }
    
    public void setCodeType(final String s) {
        this.setAttribute("codetype", s);
    }
    
    public String getData() {
        return this.getAttribute("data");
    }
    
    public void setData(final String s) {
        this.setAttribute("data", s);
    }
    
    public boolean getDeclare() {
        return this.getBinary("declare");
    }
    
    public void setDeclare(final boolean b) {
        this.setAttribute("declare", b);
    }
    
    public String getHeight() {
        return this.getAttribute("height");
    }
    
    public void setHeight(final String s) {
        this.setAttribute("height", s);
    }
    
    public String getHspace() {
        return this.getAttribute("hspace");
    }
    
    public void setHspace(final String s) {
        this.setAttribute("hspace", s);
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public String getStandby() {
        return this.getAttribute("standby");
    }
    
    public void setStandby(final String s) {
        this.setAttribute("standby", s);
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
    
    public void setType(final String s) {
        this.setAttribute("type", s);
    }
    
    public String getUseMap() {
        return this.getAttribute("useMap");
    }
    
    public void setUseMap(final String s) {
        this.setAttribute("useMap", s);
    }
    
    public String getVspace() {
        return this.getAttribute("vspace");
    }
    
    public void setVspace(final String s) {
        this.setAttribute("vspace", s);
    }
    
    public String getWidth() {
        return this.getAttribute("width");
    }
    
    public void setWidth(final String s) {
        this.setAttribute("width", s);
    }
    
    public Document getContentDocument() {
        return null;
    }
    
    public HTMLObjectElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
