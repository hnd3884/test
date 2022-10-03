package org.apache.html.dom;

import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLCollection;
import org.w3c.dom.html.HTMLMapElement;

public class HTMLMapElementImpl extends HTMLElementImpl implements HTMLMapElement
{
    private static final long serialVersionUID = 7520887584251976392L;
    private HTMLCollection _areas;
    
    public HTMLCollection getAreas() {
        if (this._areas == null) {
            this._areas = new HTMLCollectionImpl(this, (short)(-1));
        }
        return this._areas;
    }
    
    public String getName() {
        return this.getAttribute("name");
    }
    
    public void setName(final String s) {
        this.setAttribute("name", s);
    }
    
    public Node cloneNode(final boolean b) {
        final HTMLMapElementImpl htmlMapElementImpl = (HTMLMapElementImpl)super.cloneNode(b);
        htmlMapElementImpl._areas = null;
        return htmlMapElementImpl;
    }
    
    public HTMLMapElementImpl(final HTMLDocumentImpl htmlDocumentImpl, final String s) {
        super(htmlDocumentImpl, s);
    }
}
