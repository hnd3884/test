package org.w3c.tidy;

import org.w3c.dom.NodeList;

public class DOMNodeListImpl implements NodeList
{
    private Node parent;
    
    protected DOMNodeListImpl(final Node parent) {
        this.parent = parent;
    }
    
    public org.w3c.dom.Node item(final int n) {
        if (this.parent == null) {
            return null;
        }
        int n2;
        Node node;
        for (n2 = 0, node = this.parent.content; node != null && n2 < n; ++n2, node = node.next) {}
        if (node != null) {
            return node.getAdapter();
        }
        return null;
    }
    
    public int getLength() {
        if (this.parent == null) {
            return 0;
        }
        int n = 0;
        for (Node node = this.parent.content; node != null; node = node.next) {
            ++n;
        }
        return n;
    }
}
