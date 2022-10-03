package org.apache.html.dom;

import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.html.HTMLScriptElement;

public class HTMLScriptElementImpl extends HTMLElementImpl implements HTMLScriptElement
{
    private static final long serialVersionUID = 5090330049085326558L;
    
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
    
    public String getHtmlFor() {
        return this.getAttribute("for");
    }
    
    public void setHtmlFor(final String s) {
        this.setAttribute("for", s);
    }
    
    public String getEvent() {
        return this.getAttribute("event");
    }
    
    public void setEvent(final String s) {
        this.setAttribute("event", s);
    }
    
    public String getCharset() {
        return this.getAttribute("charset");
    }
    
    public void setCharset(final String s) {
        this.setAttribute("charset", s);
    }
    
    public boolean getDefer() {
        return this.getBinary("defer");
    }
    
    public void setDefer(final boolean b) {
        this.setAttribute("defer", b);
    }
    
    public String getSrc() {
        return this.getAttribute("src");
    }
    
    public void setSrc(final String s) {
        this.setAttribute("src", s);
    }
    
    public String getType() {
        return this.getAttribute("type");
    }
    
    public void setType(final String s) {
        this.setAttribute("type", s);
    }
    
    public HTMLScriptElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
