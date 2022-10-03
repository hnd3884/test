package org.apache.html.dom;

import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.html.HTMLTableCellElement;

public class HTMLTableCellElementImpl extends HTMLElementImpl implements HTMLTableCellElement
{
    private static final long serialVersionUID = -2406518157464313922L;
    
    public int getCellIndex() {
        final Node parentNode = this.getParentNode();
        int n = 0;
        if (parentNode instanceof HTMLTableRowElement) {
            for (Node node = parentNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node instanceof HTMLTableCellElement) {
                    if (node == this) {
                        return n;
                    }
                    ++n;
                }
            }
        }
        return -1;
    }
    
    public void setCellIndex(int n) {
        final Node parentNode = this.getParentNode();
        if (parentNode instanceof HTMLTableRowElement) {
            for (Node node = parentNode.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node instanceof HTMLTableCellElement) {
                    if (n == 0) {
                        if (this != node) {
                            parentNode.insertBefore(this, node);
                        }
                        return;
                    }
                    --n;
                }
            }
        }
        parentNode.appendChild(this);
    }
    
    public String getAbbr() {
        return this.getAttribute("abbr");
    }
    
    public void setAbbr(final String s) {
        this.setAttribute("abbr", s);
    }
    
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
    }
    
    public String getAxis() {
        return this.getAttribute("axis");
    }
    
    public void setAxis(final String s) {
        this.setAttribute("axis", s);
    }
    
    public String getBgColor() {
        return this.getAttribute("bgcolor");
    }
    
    public void setBgColor(final String s) {
        this.setAttribute("bgcolor", s);
    }
    
    public String getCh() {
        String s = this.getAttribute("char");
        if (s != null && s.length() > 1) {
            s = s.substring(0, 1);
        }
        return s;
    }
    
    public void setCh(String substring) {
        if (substring != null && substring.length() > 1) {
            substring = substring.substring(0, 1);
        }
        this.setAttribute("char", substring);
    }
    
    public String getChOff() {
        return this.getAttribute("charoff");
    }
    
    public void setChOff(final String s) {
        this.setAttribute("charoff", s);
    }
    
    public int getColSpan() {
        return this.getInteger(this.getAttribute("colspan"));
    }
    
    public void setColSpan(final int n) {
        this.setAttribute("colspan", String.valueOf(n));
    }
    
    public String getHeaders() {
        return this.getAttribute("headers");
    }
    
    public void setHeaders(final String s) {
        this.setAttribute("headers", s);
    }
    
    public String getHeight() {
        return this.getAttribute("height");
    }
    
    public void setHeight(final String s) {
        this.setAttribute("height", s);
    }
    
    public boolean getNoWrap() {
        return this.getBinary("nowrap");
    }
    
    public void setNoWrap(final boolean b) {
        this.setAttribute("nowrap", b);
    }
    
    public int getRowSpan() {
        return this.getInteger(this.getAttribute("rowspan"));
    }
    
    public void setRowSpan(final int n) {
        this.setAttribute("rowspan", String.valueOf(n));
    }
    
    public String getScope() {
        return this.getAttribute("scope");
    }
    
    public void setScope(final String s) {
        this.setAttribute("scope", s);
    }
    
    public String getVAlign() {
        return this.capitalize(this.getAttribute("valign"));
    }
    
    public void setVAlign(final String s) {
        this.setAttribute("valign", s);
    }
    
    public String getWidth() {
        return this.getAttribute("width");
    }
    
    public void setWidth(final String s) {
        this.setAttribute("width", s);
    }
    
    public HTMLTableCellElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
