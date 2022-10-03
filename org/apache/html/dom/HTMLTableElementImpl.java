package org.apache.html.dom;

import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLTableSectionElement;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLTableCaptionElement;
import org.w3c.dom.html.HTMLTableElement;

public class HTMLTableElementImpl extends HTMLElementImpl implements HTMLTableElement
{
    private static final long serialVersionUID = -1824053099870917532L;
    private HTMLCollectionImpl _rows;
    private HTMLCollectionImpl _bodies;
    
    public synchronized HTMLTableCaptionElement getCaption() {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof HTMLTableCaptionElement && node.getNodeName().equals("CAPTION")) {
                return (HTMLTableCaptionElement)node;
            }
        }
        return null;
    }
    
    public synchronized void setCaption(final HTMLTableCaptionElement htmlTableCaptionElement) {
        if (htmlTableCaptionElement != null && !htmlTableCaptionElement.getTagName().equals("CAPTION")) {
            throw new IllegalArgumentException("HTM016 Argument 'caption' is not an element of type <CAPTION>.");
        }
        this.deleteCaption();
        if (htmlTableCaptionElement != null) {
            this.appendChild(htmlTableCaptionElement);
        }
    }
    
    public synchronized HTMLElement createCaption() {
        final HTMLTableCaptionElement caption = this.getCaption();
        if (caption != null) {
            return caption;
        }
        final HTMLTableCaptionElementImpl htmlTableCaptionElementImpl = new HTMLTableCaptionElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "CAPTION");
        this.appendChild(htmlTableCaptionElementImpl);
        return htmlTableCaptionElementImpl;
    }
    
    public synchronized void deleteCaption() {
        final HTMLTableCaptionElement caption = this.getCaption();
        if (caption != null) {
            this.removeChild(caption);
        }
    }
    
    public synchronized HTMLTableSectionElement getTHead() {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof HTMLTableSectionElement && node.getNodeName().equals("THEAD")) {
                return (HTMLTableSectionElement)node;
            }
        }
        return null;
    }
    
    public synchronized void setTHead(final HTMLTableSectionElement htmlTableSectionElement) {
        if (htmlTableSectionElement != null && !htmlTableSectionElement.getTagName().equals("THEAD")) {
            throw new IllegalArgumentException("HTM017 Argument 'tHead' is not an element of type <THEAD>.");
        }
        this.deleteTHead();
        if (htmlTableSectionElement != null) {
            this.appendChild(htmlTableSectionElement);
        }
    }
    
    public synchronized HTMLElement createTHead() {
        final HTMLTableSectionElement tHead = this.getTHead();
        if (tHead != null) {
            return tHead;
        }
        final HTMLTableSectionElementImpl htmlTableSectionElementImpl = new HTMLTableSectionElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "THEAD");
        this.appendChild(htmlTableSectionElementImpl);
        return htmlTableSectionElementImpl;
    }
    
    public synchronized void deleteTHead() {
        final HTMLTableSectionElement tHead = this.getTHead();
        if (tHead != null) {
            this.removeChild(tHead);
        }
    }
    
    public synchronized HTMLTableSectionElement getTFoot() {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof HTMLTableSectionElement && node.getNodeName().equals("TFOOT")) {
                return (HTMLTableSectionElement)node;
            }
        }
        return null;
    }
    
    public synchronized void setTFoot(final HTMLTableSectionElement htmlTableSectionElement) {
        if (htmlTableSectionElement != null && !htmlTableSectionElement.getTagName().equals("TFOOT")) {
            throw new IllegalArgumentException("HTM018 Argument 'tFoot' is not an element of type <TFOOT>.");
        }
        this.deleteTFoot();
        if (htmlTableSectionElement != null) {
            this.appendChild(htmlTableSectionElement);
        }
    }
    
    public synchronized HTMLElement createTFoot() {
        final HTMLTableSectionElement tFoot = this.getTFoot();
        if (tFoot != null) {
            return tFoot;
        }
        final HTMLTableSectionElementImpl htmlTableSectionElementImpl = new HTMLTableSectionElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "TFOOT");
        this.appendChild(htmlTableSectionElementImpl);
        return htmlTableSectionElementImpl;
    }
    
    public synchronized void deleteTFoot() {
        final HTMLTableSectionElement tFoot = this.getTFoot();
        if (tFoot != null) {
            this.removeChild(tFoot);
        }
    }
    
    public HTMLCollection getRows() {
        if (this._rows == null) {
            this._rows = new HTMLCollectionImpl(this, (short)7);
        }
        return this._rows;
    }
    
    public HTMLCollection getTBodies() {
        if (this._bodies == null) {
            this._bodies = new HTMLCollectionImpl(this, (short)(-2));
        }
        return this._bodies;
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
    
    public String getBorder() {
        return this.getAttribute("border");
    }
    
    public void setBorder(final String s) {
        this.setAttribute("border", s);
    }
    
    public String getCellPadding() {
        return this.getAttribute("cellpadding");
    }
    
    public void setCellPadding(final String s) {
        this.setAttribute("cellpadding", s);
    }
    
    public String getCellSpacing() {
        return this.getAttribute("cellspacing");
    }
    
    public void setCellSpacing(final String s) {
        this.setAttribute("cellspacing", s);
    }
    
    public String getFrame() {
        return this.capitalize(this.getAttribute("frame"));
    }
    
    public void setFrame(final String s) {
        this.setAttribute("frame", s);
    }
    
    public String getRules() {
        return this.capitalize(this.getAttribute("rules"));
    }
    
    public void setRules(final String s) {
        this.setAttribute("rules", s);
    }
    
    public String getSummary() {
        return this.getAttribute("summary");
    }
    
    public void setSummary(final String s) {
        this.setAttribute("summary", s);
    }
    
    public String getWidth() {
        return this.getAttribute("width");
    }
    
    public void setWidth(final String s) {
        this.setAttribute("width", s);
    }
    
    public HTMLElement insertRow(final int n) {
        final HTMLTableRowElementImpl htmlTableRowElementImpl = new HTMLTableRowElementImpl((HTMLDocumentImpl)this.getOwnerDocument(), "TR");
        this.insertRowX(n, htmlTableRowElementImpl);
        return htmlTableRowElementImpl;
    }
    
    void insertRowX(int insertRowX, final HTMLTableRowElementImpl htmlTableRowElementImpl) {
        Node node = null;
        for (Node node2 = this.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            if (node2 instanceof HTMLTableRowElement) {
                if (insertRowX == 0) {
                    this.insertBefore(htmlTableRowElementImpl, node2);
                    return;
                }
            }
            else if (node2 instanceof HTMLTableSectionElementImpl) {
                node = node2;
                insertRowX = ((HTMLTableSectionElementImpl)node2).insertRowX(insertRowX, htmlTableRowElementImpl);
                if (insertRowX < 0) {
                    return;
                }
            }
        }
        if (node != null) {
            node.appendChild(htmlTableRowElementImpl);
        }
        else {
            this.appendChild(htmlTableRowElementImpl);
        }
    }
    
    public synchronized void deleteRow(int deleteRowX) {
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof HTMLTableRowElement) {
                if (deleteRowX == 0) {
                    this.removeChild(node);
                    return;
                }
                --deleteRowX;
            }
            else if (node instanceof HTMLTableSectionElementImpl) {
                deleteRowX = ((HTMLTableSectionElementImpl)node).deleteRowX(deleteRowX);
                if (deleteRowX < 0) {
                    return;
                }
            }
        }
    }
    
    public Node cloneNode(final boolean b) {
        final HTMLTableElementImpl htmlTableElementImpl = (HTMLTableElementImpl)super.cloneNode(b);
        htmlTableElementImpl._rows = null;
        htmlTableElementImpl._bodies = null;
        return htmlTableElementImpl;
    }
    
    public HTMLTableElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
