package org.apache.html.dom;

import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLTableSectionElement;

public class HTMLTableSectionElementImpl extends HTMLElementImpl implements HTMLTableSectionElement
{
    private static final long serialVersionUID = 1016412997716618027L;
    private HTMLCollectionImpl _rows;
    
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
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
    
    public String getVAlign() {
        return this.capitalize(this.getAttribute("valign"));
    }
    
    public void setVAlign(final String s) {
        this.setAttribute("valign", s);
    }
    
    public HTMLCollection getRows() {
        if (this._rows == null) {
            this._rows = new HTMLCollectionImpl(this, (short)7);
        }
        return this._rows;
    }
    
    public HTMLElement insertRow(final int n) {
        final HTMLTableRowElementImpl htmlTableRowElementImpl = new HTMLTableRowElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "TR");
        htmlTableRowElementImpl.insertCell(0);
        if (this.insertRowX(n, htmlTableRowElementImpl) >= 0) {
            this.appendChild(htmlTableRowElementImpl);
        }
        return htmlTableRowElementImpl;
    }
    
    int insertRowX(int n, final HTMLTableRowElementImpl htmlTableRowElementImpl) {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof HTMLTableRowElement) {
                if (n == 0) {
                    this.insertBefore(htmlTableRowElementImpl, node);
                    return -1;
                }
                --n;
            }
        }
        return n;
    }
    
    public void deleteRow(final int n) {
        this.deleteRowX(n);
    }
    
    int deleteRowX(int n) {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof HTMLTableRowElement) {
                if (n == 0) {
                    this.removeChild(node);
                    return -1;
                }
                --n;
            }
        }
        return n;
    }
    
    public Node cloneNode(final boolean b) {
        final HTMLTableSectionElementImpl htmlTableSectionElementImpl = (HTMLTableSectionElementImpl)super.cloneNode(b);
        htmlTableSectionElementImpl._rows = null;
        return htmlTableSectionElementImpl;
    }
    
    public HTMLTableSectionElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
