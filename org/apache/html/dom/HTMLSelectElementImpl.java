package org.apache.html.dom;

import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLOptionElement;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLSelectElement;

public class HTMLSelectElementImpl extends HTMLElementImpl implements HTMLSelectElement, HTMLFormControl
{
    private static final long serialVersionUID = -6998282711006968187L;
    private HTMLCollection _options;
    
    public String getType() {
        return this.getAttribute("type");
    }
    
    public String getValue() {
        return this.getAttribute("value");
    }
    
    public void setValue(final String s) {
        this.setAttribute("value", s);
    }
    
    public int getSelectedIndex() {
        final NodeList elementsByTagName = this.getElementsByTagName("OPTION");
        for (int i = 0; i < elementsByTagName.getLength(); ++i) {
            if (((HTMLOptionElement)elementsByTagName.item(i)).getSelected()) {
                return i;
            }
        }
        return -1;
    }
    
    public void setSelectedIndex(final int n) {
        final NodeList elementsByTagName = this.getElementsByTagName("OPTION");
        for (int i = 0; i < elementsByTagName.getLength(); ++i) {
            ((HTMLOptionElementImpl)elementsByTagName.item(i)).setSelected(i == n);
        }
    }
    
    public HTMLCollection getOptions() {
        if (this._options == null) {
            this._options = new HTMLCollectionImpl(this, (short)6);
        }
        return this._options;
    }
    
    public int getLength() {
        return this.getOptions().getLength();
    }
    
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }
    
    public void setDisabled(final boolean b) {
        this.setAttribute("disabled", b);
    }
    
    public boolean getMultiple() {
        return this.getBinary("multiple");
    }
    
    public void setMultiple(final boolean b) {
        this.setAttribute("multiple", b);
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public int getSize() {
        return this.getInteger(this.getAttribute("size"));
    }
    
    public void setSize(final int n) {
        this.setAttribute("size", String.valueOf(n));
    }
    
    public int getTabIndex() {
        return this.getInteger(this.getAttribute("tabindex"));
    }
    
    public void setTabIndex(final int n) {
        this.setAttribute("tabindex", String.valueOf(n));
    }
    
    public void add(final HTMLElement htmlElement, final HTMLElement htmlElement2) {
        this.insertBefore(htmlElement, htmlElement2);
    }
    
    public void remove(final int n) {
        final Node item = this.getElementsByTagName("OPTION").item(n);
        if (item != null) {
            item.getParentNode().removeChild(item);
        }
    }
    
    public void blur() {
    }
    
    public void focus() {
    }
    
    public NodeList getChildNodes() {
        return this.getChildNodesUnoptimized();
    }
    
    public Node cloneNode(final boolean b) {
        final HTMLSelectElementImpl htmlSelectElementImpl = (HTMLSelectElementImpl)super.cloneNode(b);
        htmlSelectElementImpl._options = null;
        return htmlSelectElementImpl;
    }
    
    public HTMLSelectElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
