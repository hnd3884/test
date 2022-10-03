package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;
import java.util.Vector;
import org.w3c.dom.NodeList;

public class DeepNodeListImpl implements NodeList
{
    protected NodeImpl rootNode;
    protected String tagName;
    protected int changes;
    protected Vector nodes;
    protected String nsName;
    protected boolean enableNS;
    
    public DeepNodeListImpl(final NodeImpl rootNode, final String tagName) {
        this.changes = 0;
        this.enableNS = false;
        this.rootNode = rootNode;
        this.tagName = tagName;
        this.nodes = new Vector();
    }
    
    public DeepNodeListImpl(final NodeImpl rootNode, final String nsName, final String tagName) {
        this(rootNode, tagName);
        this.nsName = ((nsName != null && !nsName.equals("")) ? nsName : null);
        this.enableNS = true;
    }
    
    @Override
    public int getLength() {
        this.item(Integer.MAX_VALUE);
        return this.nodes.size();
    }
    
    @Override
    public Node item(final int index) {
        if (this.rootNode.changes() != this.changes) {
            this.nodes = new Vector();
            this.changes = this.rootNode.changes();
        }
        if (index < this.nodes.size()) {
            return this.nodes.elementAt(index);
        }
        Node thisNode;
        if (this.nodes.size() == 0) {
            thisNode = this.rootNode;
        }
        else {
            thisNode = this.nodes.lastElement();
        }
        while (thisNode != null && index >= this.nodes.size()) {
            thisNode = this.nextMatchingElementAfter(thisNode);
            if (thisNode != null) {
                this.nodes.addElement(thisNode);
            }
        }
        return thisNode;
    }
    
    protected Node nextMatchingElementAfter(Node current) {
        while (current != null) {
            if (current.hasChildNodes()) {
                current = current.getFirstChild();
            }
            else {
                Node next;
                if (current != this.rootNode && null != (next = current.getNextSibling())) {
                    current = next;
                }
                else {
                    next = null;
                    while (current != this.rootNode) {
                        next = current.getNextSibling();
                        if (next != null) {
                            break;
                        }
                        current = current.getParentNode();
                    }
                    current = next;
                }
            }
            if (current != this.rootNode && current != null && current.getNodeType() == 1) {
                if (!this.enableNS) {
                    if (this.tagName.equals("*") || ((ElementImpl)current).getTagName().equals(this.tagName)) {
                        return current;
                    }
                    continue;
                }
                else if (this.tagName.equals("*")) {
                    if (this.nsName != null && this.nsName.equals("*")) {
                        return current;
                    }
                    final ElementImpl el = (ElementImpl)current;
                    if ((this.nsName == null && el.getNamespaceURI() == null) || (this.nsName != null && this.nsName.equals(el.getNamespaceURI()))) {
                        return current;
                    }
                    continue;
                }
                else {
                    final ElementImpl el = (ElementImpl)current;
                    if (el.getLocalName() == null || !el.getLocalName().equals(this.tagName)) {
                        continue;
                    }
                    if (this.nsName != null && this.nsName.equals("*")) {
                        return current;
                    }
                    if ((this.nsName == null && el.getNamespaceURI() == null) || (this.nsName != null && this.nsName.equals(el.getNamespaceURI()))) {
                        return current;
                    }
                    continue;
                }
            }
        }
        return null;
    }
}
