package org.apache.html.dom;

import org.w3c.dom.html.HTMLTableCellElement;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLTableElement;
import org.w3c.dom.html.HTMLTableSectionElement;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLTableRowElement;

public class HTMLTableRowElementImpl extends HTMLElementImpl implements HTMLTableRowElement
{
    private static final long serialVersionUID = 5409562635656244263L;
    HTMLCollection _cells;
    
    public int getRowIndex() {
        Node node = this.getParentNode();
        if (node instanceof HTMLTableSectionElement) {
            node = node.getParentNode();
        }
        if (node instanceof HTMLTableElement) {
            return this.getRowIndex(node);
        }
        return -1;
    }
    
    public void setRowIndex(final int n) {
        Node node = this.getParentNode();
        if (node instanceof HTMLTableSectionElement) {
            node = node.getParentNode();
        }
        if (node instanceof HTMLTableElement) {
            ((HTMLTableElementImpl)node).insertRowX(n, this);
        }
    }
    
    public int getSectionRowIndex() {
        final Node parentNode = this.getParentNode();
        if (parentNode instanceof HTMLTableSectionElement) {
            return this.getRowIndex(parentNode);
        }
        return -1;
    }
    
    public void setSectionRowIndex(final int n) {
        final Node parentNode = this.getParentNode();
        if (parentNode instanceof HTMLTableSectionElement) {
            ((HTMLTableSectionElementImpl)parentNode).insertRowX(n, this);
        }
    }
    
    int getRowIndex(final Node node) {
        final NodeList elementsByTagName = ((HTMLElement)node).getElementsByTagName("TR");
        for (int i = 0; i < elementsByTagName.getLength(); ++i) {
            if (elementsByTagName.item(i) == this) {
                return i;
            }
        }
        return -1;
    }
    
    public HTMLCollection getCells() {
        if (this._cells == null) {
            this._cells = new HTMLCollectionImpl(this, (short)(-3));
        }
        return this._cells;
    }
    
    public void setCells(final HTMLCollection collection) {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            this.removeChild(node);
        }
        int n = 0;
        for (Node node2 = collection.item(n); node2 != null; node2 = collection.item(n)) {
            this.appendChild(node2);
            ++n;
        }
    }
    
    public HTMLElement insertCell(int n) {
        final HTMLTableCellElementImpl htmlTableCellElementImpl = new HTMLTableCellElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "TD");
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof HTMLTableCellElement) {
                if (n == 0) {
                    this.insertBefore(htmlTableCellElementImpl, node);
                    return htmlTableCellElementImpl;
                }
                --n;
            }
        }
        this.appendChild(htmlTableCellElementImpl);
        return htmlTableCellElementImpl;
    }
    
    public void deleteCell(int n) {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof HTMLTableCellElement) {
                if (n == 0) {
                    this.removeChild(node);
                    return;
                }
                --n;
            }
        }
    }
    
    public String getAlign() {
        return this.capitalize(this.getAttribute("align"));
    }
    
    public void setAlign(final String s) {
        this.setAttribute("align", s);
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
    
    public String getVAlign() {
        return this.capitalize(this.getAttribute("valign"));
    }
    
    public void setVAlign(final String s) {
        this.setAttribute("valign", s);
    }
    
    public Node cloneNode(final boolean b) {
        final HTMLTableRowElementImpl htmlTableRowElementImpl = (HTMLTableRowElementImpl)super.cloneNode(b);
        htmlTableRowElementImpl._cells = null;
        return htmlTableRowElementImpl;
    }
    
    public HTMLTableRowElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
