package com.sun.org.apache.xerces.internal.impl.xs.opti;

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
    
    @Override
    public Node getNamedItem(final String name) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(name)) {
                return this.attrs[i];
            }
        }
        return null;
    }
    
    @Override
    public Node item(final int index) {
        if (index < 0 && index > this.getLength()) {
            return null;
        }
        return this.attrs[index];
    }
    
    @Override
    public int getLength() {
        return this.attrs.length;
    }
    
    @Override
    public Node getNamedItemNS(final String namespaceURI, final String localName) {
        for (int i = 0; i < this.attrs.length; ++i) {
            if (this.attrs[i].getName().equals(localName) && this.attrs[i].getNamespaceURI().equals(namespaceURI)) {
                return this.attrs[i];
            }
        }
        return null;
    }
    
    @Override
    public Node setNamedItemNS(final Node arg) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Node setNamedItem(final Node arg) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Node removeNamedItem(final String name) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
    
    @Override
    public Node removeNamedItemNS(final String namespaceURI, final String localName) throws DOMException {
        throw new DOMException((short)9, "Method not supported");
    }
}
