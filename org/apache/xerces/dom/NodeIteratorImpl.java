package org.apache.xerces.dom;

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
    
    public NodeIteratorImpl(final DocumentImpl fDocument, final Node fRoot, final int fWhatToShow, final NodeFilter fNodeFilter, final boolean fEntityReferenceExpansion) {
        this.fWhatToShow = -1;
        this.fDetach = false;
        this.fForward = true;
        this.fDocument = fDocument;
        this.fRoot = fRoot;
        this.fCurrentNode = null;
        this.fWhatToShow = fWhatToShow;
        this.fNodeFilter = fNodeFilter;
        this.fEntityReferenceExpansion = fEntityReferenceExpansion;
    }
    
    public Node getRoot() {
        return this.fRoot;
    }
    
    public int getWhatToShow() {
        return this.fWhatToShow;
    }
    
    public NodeFilter getFilter() {
        return this.fNodeFilter;
    }
    
    public boolean getExpandEntityReferences() {
        return this.fEntityReferenceExpansion;
    }
    
    public Node nextNode() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        if (this.fRoot == null) {
            return null;
        }
        Node fCurrentNode = this.fCurrentNode;
        boolean acceptNode = false;
        while (!acceptNode) {
            if (!this.fForward && fCurrentNode != null) {
                fCurrentNode = this.fCurrentNode;
            }
            else if (!this.fEntityReferenceExpansion && fCurrentNode != null && fCurrentNode.getNodeType() == 5) {
                fCurrentNode = this.nextNode(fCurrentNode, false);
            }
            else {
                fCurrentNode = this.nextNode(fCurrentNode, true);
            }
            this.fForward = true;
            if (fCurrentNode == null) {
                return null;
            }
            acceptNode = this.acceptNode(fCurrentNode);
            if (acceptNode) {
                return this.fCurrentNode = fCurrentNode;
            }
        }
        return null;
    }
    
    public Node previousNode() {
        if (this.fDetach) {
            throw new DOMException((short)11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        if (this.fRoot == null || this.fCurrentNode == null) {
            return null;
        }
        Node fCurrentNode = this.fCurrentNode;
        boolean acceptNode = false;
        while (!acceptNode) {
            if (this.fForward && fCurrentNode != null) {
                fCurrentNode = this.fCurrentNode;
            }
            else {
                fCurrentNode = this.previousNode(fCurrentNode);
            }
            this.fForward = false;
            if (fCurrentNode == null) {
                return null;
            }
            acceptNode = this.acceptNode(fCurrentNode);
            if (acceptNode) {
                return this.fCurrentNode = fCurrentNode;
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
        for (Node node2 = this.fCurrentNode; node2 != this.fRoot; node2 = node2.getParentNode()) {
            if (node == node2) {
                return node2;
            }
        }
        return null;
    }
    
    Node nextNode(final Node node, final boolean b) {
        if (node == null) {
            return this.fRoot;
        }
        if (b && node.hasChildNodes()) {
            return node.getFirstChild();
        }
        if (node == this.fRoot) {
            return null;
        }
        final Node nextSibling = node.getNextSibling();
        if (nextSibling != null) {
            return nextSibling;
        }
        for (Node node2 = node.getParentNode(); node2 != null && node2 != this.fRoot; node2 = node2.getParentNode()) {
            final Node nextSibling2 = node2.getNextSibling();
            if (nextSibling2 != null) {
                return nextSibling2;
            }
        }
        return null;
    }
    
    Node previousNode(final Node node) {
        if (node == this.fRoot) {
            return null;
        }
        Node node2 = node.getPreviousSibling();
        if (node2 == null) {
            return node.getParentNode();
        }
        if (node2.hasChildNodes() && (this.fEntityReferenceExpansion || node2 == null || node2.getNodeType() != 5)) {
            while (node2.hasChildNodes()) {
                node2 = node2.getLastChild();
            }
        }
        return node2;
    }
    
    public void removeNode(final Node node) {
        if (node == null) {
            return;
        }
        final Node matchNodeOrParent = this.matchNodeOrParent(node);
        if (matchNodeOrParent == null) {
            return;
        }
        if (this.fForward) {
            this.fCurrentNode = this.previousNode(matchNodeOrParent);
        }
        else {
            final Node nextNode = this.nextNode(matchNodeOrParent, false);
            if (nextNode != null) {
                this.fCurrentNode = nextNode;
            }
            else {
                this.fCurrentNode = this.previousNode(matchNodeOrParent);
                this.fForward = true;
            }
        }
    }
    
    public void detach() {
        this.fDetach = true;
        this.fDocument.removeNodeIterator(this);
    }
}
