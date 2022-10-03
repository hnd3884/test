package org.apache.xerces.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

public class NamedNodeMapImpl implements NamedNodeMap
{
    Attr[] attrs;
    
    public NamedNodeMapImpl(final Attr[] attrs) {
        this.attrs = attrs;
    }
    
    public Node getNamedItem(final String s) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(s)) {
                return this.attrs[i];
            }
        }
        return null;
    }
    
    public Node item(final int n) {
        if (n < 0 && n > this.getLength()) {
            return null;
        }
        return this.attrs[n];
    }
    
    public int getLength() {
        return this.attrs.length;
    }
    
    public Node getNamedItemNS(final String s, final String s2) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(s2) && this.attrs[i].getNamespaceURI().equals(s)) {
                return this.attrs[i];
            }
        }
        return null;
    }
    
    public Node setNamedItemNS(final Node node) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Node setNamedItem(final Node node) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Node removeNamedItem(final String s) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    public Node removeNamedItemNS(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
}
