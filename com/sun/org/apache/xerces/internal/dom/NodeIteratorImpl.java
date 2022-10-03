package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

public class NodeIteratorImpl implements NodeIterator
{
    private DocumentImpl fDocument;
    private Node fRoot;
    private int fWhatToShow;
    private NodeFilter fNodeFilter;
    private boolean fDetach;
    private Node fCurrentNode;
    private boolean fForward;
    private boolean fEntityReferenceExpansion;
    
    public NodeIteratorImpl(final DocumentImpl document, final Node root, final int whatToShow, final NodeFilter nodeFilter, final boolean entityReferenceExpansion) {
        this.fWhatToShow = -1;
        this.fDetach = false;
        this.fForward = true;
        this.fDocument = document;
        this.fRoot = root;
        this.fCurrentNode = null;
        this.fWhatToShow = whatToShow;
        this.fNodeFilter = nodeFilter;
        this.fEntityReferenceExpansion = entityReferenceExpansion;
    }
    
    @Override
    public Node getRoot() {
        return this.fRoot;
    }
    
    @Override
    public int getWhatToShow() {
        return this.fWhatToShow;
    }
    
    @Override
    public NodeFilter getFilter() {
        return this.fNodeFilter;
    }
    
    @Override
    public boolean getExpandEntityReferences() {
        return this.fEntityReferenceExpansion;
    }
    
    @Override
    public Node nextNode() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        if (this.fRoot == null) {
            return null;
        }
        Node nextNode = this.fCurrentNode;
        boolean accepted = false;
        while (!accepted) {
            if (!this.fForward && nextNode != null) {
                nextNode = this.fCurrentNode;
            }
            else if (!this.fEntityReferenceExpansion && nextNode != null && nextNode.getNodeType() == 5) {
                nextNode = this.nextNode(nextNode, false);
            }
            else {
                nextNode = this.nextNode(nextNode, true);
            }
            this.fForward = true;
            if (nextNode == null) {
                return null;
            }
            accepted = this.acceptNode(nextNode);
            if (accepted) {
                return this.fCurrentNode = nextNode;
            }
        }
        return null;
    }
    
    @Override
    public Node previousNode() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        if (this.fRoot == null || this.fCurrentNode == null) {
            return null;
        }
        Node previousNode = this.fCurrentNode;
        boolean accepted = false;
        while (!accepted) {
            if (this.fForward && previousNode != null) {
                previousNode = this.fCurrentNode;
            }
            else {
                previousNode = this.previousNode(previousNode);
            }
            this.fForward = false;
            if (previousNode == null) {
                return null;
            }
            accepted = this.acceptNode(previousNode);
            if (accepted) {
                return this.fCurrentNode = previousNode;
            }
        }
        return null;
    }
    
    boolean acceptNode(final Node node) {
        if (this.fNodeFilter == null) {
            return (this.fWhatToShow & 1 << node.getNodeType() - 1) != 0x0;
        }
        return (this.fWhatToShow & 1 << node.getNodeType() - 1) != 0x0 && this.fNodeFilter.acceptNode(node) == 1;
    }
    
    Node matchNodeOrParent(final Node node) {
        if (this.fCurrentNode == null) {
            return null;
        }
        for (Node n = this.fCurrentNode; n != this.fRoot; n = n.getParentNode()) {
            if (node == n) {
                return n;
            }
        }
        return null;
    }
    
    Node nextNode(final Node node, final boolean visitChildren) {
        if (node == null) {
            return this.fRoot;
        }
        if (visitChildren && node.hasChildNodes()) {
            final Node result = node.getFirstChild();
            return result;
        }
        if (node == this.fRoot) {
            return null;
        }
        Node result = node.getNextSibling();
        if (result != null) {
            return result;
        }
        for (Node parent = node.getParentNode(); parent != null && parent != this.fRoot; parent = parent.getParentNode()) {
            result = parent.getNextSibling();
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    Node previousNode(final Node node) {
        if (node == this.fRoot) {
            return null;
        }
        Node result = node.getPreviousSibling();
        if (result == null) {
            result = node.getParentNode();
            return result;
        }
        if (result.hasChildNodes() && (this.fEntityReferenceExpansion || result == null || result.getNodeType() != 5)) {
            while (result.hasChildNodes()) {
                result = result.getLastChild();
            }
        }
        return result;
    }
    
    public void removeNode(final Node node) {
        if (node == null) {
            return;
        }
        final Node deleted = this.matchNodeOrParent(node);
        if (deleted == null) {
            return;
        }
        if (this.fForward) {
            this.fCurrentNode = this.previousNode(deleted);
        }
        else {
            final Node next = this.nextNode(deleted, false);
            if (next != null) {
                this.fCurrentNode = next;
            }
            else {
                this.fCurrentNode = this.previousNode(deleted);
                this.fForward = true;
            }
        }
    }
    
    @Override
    public void detach() {
        this.fDetach = true;
        this.fDocument.removeNodeIterator(this);
    }
}
