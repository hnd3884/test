package org.jcp.xml.dsig.internal.dom;

import org.w3c.dom.NamedNodeMap;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.ListIterator;
import java.util.List;
import org.w3c.dom.Node;
import java.util.Iterator;
import javax.xml.crypto.NodeSetData;

public class DOMSubTreeData implements NodeSetData
{
    private boolean excludeComments;
    private Iterator ni;
    private Node root;
    
    public DOMSubTreeData(final Node root, final boolean excludeComments) {
        this.root = root;
        this.ni = new DelayedNodeIterator(root, excludeComments);
        this.excludeComments = excludeComments;
    }
    
    public Iterator iterator() {
        return this.ni;
    }
    
    public Node getRoot() {
        return this.root;
    }
    
    public boolean excludeComments() {
        return this.excludeComments;
    }
    
    static class DelayedNodeIterator implements Iterator
    {
        private Node root;
        private List nodeSet;
        private ListIterator li;
        private boolean withComments;
        
        DelayedNodeIterator(final Node root, final boolean b) {
            this.root = root;
            this.withComments = !b;
        }
        
        public boolean hasNext() {
            if (this.nodeSet == null) {
                this.nodeSet = this.dereferenceSameDocumentURI(this.root);
                this.li = this.nodeSet.listIterator();
            }
            return this.li.hasNext();
        }
        
        public Object next() {
            if (this.nodeSet == null) {
                this.nodeSet = this.dereferenceSameDocumentURI(this.root);
                this.li = this.nodeSet.listIterator();
            }
            if (this.li.hasNext()) {
                return this.li.next();
            }
            throw new NoSuchElementException();
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        private List dereferenceSameDocumentURI(final Node node) {
            final ArrayList list = new ArrayList();
            if (node != null) {
                this.nodeSetMinusCommentNodes(node, list, null);
            }
            return list;
        }
        
        private void nodeSetMinusCommentNodes(final Node node, final List list, final Node node2) {
            switch (node.getNodeType()) {
                case 1: {
                    final NamedNodeMap attributes = node.getAttributes();
                    if (attributes != null) {
                        for (int i = 0; i < attributes.getLength(); ++i) {
                            list.add(attributes.item(i));
                        }
                    }
                    list.add(node);
                }
                case 9: {
                    Node node3 = null;
                    for (Node node4 = node.getFirstChild(); node4 != null; node4 = node4.getNextSibling()) {
                        this.nodeSetMinusCommentNodes(node4, list, node3);
                        node3 = node4;
                    }
                    break;
                }
                case 3:
                case 4: {
                    if (node2 != null && (node2.getNodeType() == 3 || node2.getNodeType() == 4)) {
                        return;
                    }
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
