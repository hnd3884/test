package org.w3c.tidy;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

public class DOMAttrMapImpl implements NamedNodeMap
{
    private AttVal first;
    
    protected DOMAttrMapImpl(final AttVal first) {
        this.first = first;
    }
    
    public Node getNamedItem(final String s) {
        AttVal attVal;
        for (attVal = this.first; attVal != null && !attVal.attribute.equals(s); attVal = attVal.next) {}
        if (attVal != null) {
            return attVal.getAdapter();
        }
        return null;
    }
    
    public Node item(final int n) {
        int n2;
        AttVal attVal;
        for (n2 = 0, attVal = this.first; attVal != null && n2 < n; ++n2, attVal = attVal.next) {}
        if (attVal != null) {
            return attVal.getAdapter();
        }
        return null;
    }
    
    public int getLength() {
        int n = 0;
        for (AttVal attVal = this.first; attVal != null; attVal = attVal.next) {
            ++n;
        }
        return n;
    }
    
    public Node setNamedItem(final Node node) throws DOMException {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public Node removeNamedItem(final String s) throws DOMException {
        AttVal attVal = this.first;
        AttVal attVal2 = null;
        while (attVal != null) {
            if (attVal.attribute.equals(s)) {
                if (attVal2 == null) {
                    this.first = attVal.getNext();
                    break;
                }
                attVal2.setNext(attVal.getNext());
                break;
            }
            else {
                attVal2 = attVal;
                attVal = attVal.next;
            }
        }
        if (attVal != null) {
            return attVal.getAdapter();
        }
        throw new DOMException((short)8, "Named item " + s + "Not found");
    }
    
    public Node getNamedItemNS(final String s, final String s2) {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public Node setNamedItemNS(final Node node) throws DOMException {
        throw new DOMException((short)9, "DOM method not supported");
    }
    
    public Node removeNamedItemNS(final String s, final String s2) throws DOMException {
        throw new DOMException((short)9, "DOM method not supported");
    }
}
