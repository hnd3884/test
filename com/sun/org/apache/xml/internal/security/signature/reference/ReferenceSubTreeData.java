package com.sun.org.apache.xml.internal.security.signature.reference;

import org.w3c.dom.NamedNodeMap;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.ListIterator;
import java.util.List;
import java.util.Iterator;
import org.w3c.dom.Node;

public class ReferenceSubTreeData implements ReferenceNodeSetData
{
    private boolean excludeComments;
    private Node root;
    
    public ReferenceSubTreeData(final Node root, final boolean excludeComments) {
        this.root = root;
        this.excludeComments = excludeComments;
    }
    
    @Override
    public Iterator<Node> iterator() {
        return new DelayedNodeIterator(this.root, this.excludeComments);
    }
    
    public Node getRoot() {
        return this.root;
    }
    
    public boolean excludeComments() {
        return this.excludeComments;
    }
    
    static class DelayedNodeIterator implements Iterator<Node>
    {
        private Node root;
        private List<Node> nodeSet;
        private ListIterator<Node> li;
        private boolean withComments;
        
        DelayedNodeIterator(final Node root, final boolean b) {
            this.root = root;
            this.withComments = !b;
        }
        
        @Override
        public boolean hasNext() {
            if (this.nodeSet == null) {
                this.nodeSet = this.dereferenceSameDocumentURI(this.root);
                this.li = this.nodeSet.listIterator();
            }
            return this.li.hasNext();
        }
        
        @Override
        public Node next() {
            if (this.nodeSet == null) {
                this.nodeSet = this.dereferenceSameDocumentURI(this.root);
                this.li = this.nodeSet.listIterator();
            }
            if (this.li.hasNext()) {
                return this.li.next();
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        private List<Node> dereferenceSameDocumentURI(final Node node) {
            final ArrayList list = new ArrayList();
            if (node != null) {
                this.nodeSetMinusCommentNodes(node, list, null);
            }
            return list;
        }
        
        private void nodeSetMinusCommentNodes(final Node node, final List<Node> list, final Node node2) {
            switch (node.getNodeType()) {
                case 1: {
                    list.add(node);
                    final NamedNodeMap attributes = node.getAttributes();
                    if (attributes != null) {
                        for (int i = 0; i < attributes.getLength(); ++i) {
                            list.add(attributes.item(i));
                        }
                    }
                    Node node3 = null;
                    for (Node node4 = node.getFirstChild(); node4 != null; node4 = node4.getNextSibling()) {
                        this.nodeSetMinusCommentNodes(node4, list, node3);
                        node3 = node4;
                    }
                    break;
                }
                case 9: {
                    Node node5 = null;
                    for (Node node6 = node.getFirstChild(); node6 != null; node6 = node6.getNextSibling()) {
                        this.nodeSetMinusCommentNodes(node6, list, node5);
                        node5 = node6;
                    }
                    break;
                }
                case 3:
                case 4: {
                    if (node2 != null && (node2.getNodeType() == 3 || node2.getNodeType() == 4)) {
                        return;
                    }
                    list.add(node);
                    break;
                }
                case 7: {
                    list.add(node);
                    break;
                }
                case 8: {
                    if (this.withComments) {
                        list.add(node);
                        break;
                    }
                    break;
                }
            }
        }
    }
}
