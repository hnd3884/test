package org.apache.html.dom;

import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLTitleElement;

public class HTMLTitleElementImpl extends HTMLElementImpl implements HTMLTitleElement
{
    private static final long serialVersionUID = 879646303512367875L;
    
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
    
    public HTMLTitleElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
