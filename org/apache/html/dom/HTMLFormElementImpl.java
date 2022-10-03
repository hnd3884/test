package org.apache.html.dom;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLFormElement;

public class HTMLFormElementImpl extends HTMLElementImpl implements HTMLFormElement
{
    private static final long serialVersionUID = -7324749629151493210L;
    private HTMLCollectionImpl _elements;
    
    public HTMLCollection getElements() {
        if (this._elements == null) {
            this._elements = new HTMLCollectionImpl(this, (short)8);
        }
        return this._elements;
    }
    
    public int getLength() {
        return this.getElements().getLength();
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public String getAcceptCharset() {
        return this.getAttribute("accept-charset");
    }
    
    public void setAcceptCharset(final String s) {
        this.setAttribute("accept-charset", s);
    }
    
    public String getAction() {
        return this.getAttribute("action");
    }
    
    public void setAction(final String s) {
        this.setAttribute("action", s);
    }
    
    public String getEnctype() {
        return this.getAttribute("enctype");
    }
    
    public void setEnctype(final String s) {
        this.setAttribute("enctype", s);
    }
    
    public String getMethod() {
        return this.capitalize(this.getAttribute("method"));
    }
    
    public void setMethod(final String s) {
        this.setAttribute("method", s);
    }
    
    public String getTarget() {
        return this.getAttribute("target");
    }
    
    public void setTarget(final String s) {
        this.setAttribute("target", s);
    }
    
    public void submit() {
    }
    
    public void reset() {
    }
    
    public NodeList getChildNodes() {
        return this.getChildNodesUnoptimized();
    }
    
    public Node cloneNode(final boolean b) {
        final HTMLFormElementImpl htmlFormElementImpl = (HTMLFormElementImpl)super.cloneNode(b);
        htmlFormElementImpl._elements = null;
        return htmlFormElementImpl;
    }
    
    public HTMLFormElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
