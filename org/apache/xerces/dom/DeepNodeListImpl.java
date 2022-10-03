package org.apache.xerces.dom;

import org.w3c.dom.Node;
import java.util.ArrayList;
import org.w3c.dom.NodeList;

public class DeepNodeListImpl implements NodeList
{
    protected NodeImpl rootNode;
    protected String tagName;
    protected int changes;
    protected ArrayList nodes;
    protected String nsName;
    protected boolean enableNS;
    
    public DeepNodeListImpl(final NodeImpl rootNode, final String tagName) {
        this.changes = 0;
        this.enableNS = false;
        this.rootNode = rootNode;
        this.tagName = tagName;
        this.nodes = new ArrayList();
    }
    
    public DeepNodeListImpl(final NodeImpl nodeImpl, final String s, final String s2) {
        this(nodeImpl, s2);
        this.nsName = ((s != null && s.length() != 0) ? s : null);
        this.enableNS = true;
    }
    
    public int getLength() {
        this.item(Integer.MAX_VALUE);
        return this.nodes.size();
    }
    
    public Node item(final int n) {
        if (this.rootNode.changes() != this.changes) {
            this.nodes = new ArrayList();
            this.changes = this.rootNode.changes();
        }
        final int size = this.nodes.size();
        if (n < size) {
            return this.nodes.get(n);
        }
        Node node;
        if (size == 0) {
            node = this.rootNode;
        }
        else {
            node = this.nodes.get(size - 1);
        }
        while (node != null && n >= this.nodes.size()) {
            node = this.nextMatchingElementAfter(node);
            if (node != null) {
                this.nodes.add(node);
            }
        }
        return node;
    }
    
    protected Node nextMatchingElementAfter(Node node) {
        while (node != null) {
            if (node.hasChildNodes()) {
                node = node.getFirstChild();
            }
            else {
                final Node nextSibling;
                if (node != this.rootNode && null != (nextSibling = node.getNextSibling())) {
                    node = nextSibling;
                }
                else {
                    Node nextSibling2 = null;
                    while (node != this.rootNode) {
                        nextSibling2 = node.getNextSibling();
                        if (nextSibling2 != null) {
                            break;
                        }
                        node = node.getParentNode();
                    }
                    node = nextSibling2;
                }
            }
            if (node != this.rootNode && node != null && node.getNodeType() == 1) {
                if (!this.enableNS) {
                    if (this.tagName.equals("*") || ((ElementImpl)node).getTagName().equals(this.tagName)) {
                        return node;
                    }
                    continue;
                }
                else if (this.tagName.equals("*")) {
                    if (this.nsName != null && this.nsName.equals("*")) {
                        return node;
                    }
                    final ElementImpl elementImpl = (ElementImpl)node;
                    if ((this.nsName == null && elementImpl.getNamespaceURI() == null) || (this.nsName != null && this.nsName.equals(elementImpl.getNamespaceURI()))) {
                        return node;
                    }
                    continue;
                }
                else {
                    final ElementImpl elementImpl2 = (ElementImpl)node;
                    if (elementImpl2.getLocalName() == null || !elementImpl2.getLocalName().equals(this.tagName)) {
                        continue;
                    }
                    if (this.nsName != null && this.nsName.equals("*")) {
                        return node;
                    }
                    if ((this.nsName == null && elementImpl2.getNamespaceURI() == null) || (this.nsName != null && this.nsName.equals(elementImpl2.getNamespaceURI()))) {
                        return node;
                    }
                    continue;
                }
            }
        }
        return null;
    }
}
