package org.apache.html.dom;

import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLSelectElement;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLOptionElement;

public class HTMLOptionElementImpl extends HTMLElementImpl implements HTMLOptionElement
{
    private static final long serialVersionUID = -4486774554137530907L;
    
    public boolean getDefaultSelected() {
        return this.getBinary("default-selected");
    }
    
    public void setDefaultSelected(final boolean b) {
        this.setAttribute("default-selected", b);
    }
    
    public String getText() {
        final StringBuffer sb = new StringBuffer();
        for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Text) {
                sb.append(((Text)node).getData());
            }
        }
        return sb.toString();
    }
    
    public void setText(final String s) {
        Node nextSibling;
        for (Node firstChild = this.getFirstChild(); firstChild != null; firstChild = nextSibling) {
            nextSibling = firstChild.getNextSibling();
            this.removeChild(firstChild);
        }
        this.insertBefore(this.getOwnerDocument().createTextNode(s), this.getFirstChild());
    }
    
    public int getIndex() {
        Node node;
        for (node = this.getParentNode(); node != null && !(node instanceof HTMLSelectElement); node = node.getParentNode()) {}
        if (node != null) {
            final NodeList elementsByTagName = ((HTMLElement)node).getElementsByTagName("OPTION");
            for (int i = 0; i < elementsByTagName.getLength(); ++i) {
                if (elementsByTagName.item(i) == this) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public void setIndex(final int n) {
        Node node;
        for (node = this.getParentNode(); node != null && !(node instanceof HTMLSelectElement); node = node.getParentNode()) {}
        if (node != null) {
            final NodeList elementsByTagName = ((HTMLElement)node).getElementsByTagName("OPTION");
            if (elementsByTagName.item(n) != this) {
                this.getParentNode().removeChild(this);
                final Node item = elementsByTagName.item(n);
                item.getParentNode().insertBefore(this, item);
            }
        }
    }
    
    public boolean getDisabled() {
        return this.getBinary("disabled");
    }
    
    public void setDisabled(final boolean b) {
        this.setAttribute("disabled", b);
    }
    
    public String getLabel() {
        return this.capitalize(this.getAttribute("label"));
    }
    
    public void setLabel(final String s) {
        this.setAttribute("label", s);
    }
    
    public boolean getSelected() {
        return this.getBinary("selected");
    }
    
    public void setSelected(final boolean b) {
        this.setAttribute("selected", b);
    }
    
    public String getValue() {
        return this.getAttribute("value");
    }
    
    public void setValue(final String s) {
        this.setAttribute("value", s);
    }
    
    public HTMLOptionElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
