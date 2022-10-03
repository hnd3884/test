package org.apache.xerces.dom;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

public class TreeWalkerImpl implements TreeWalker
{
    private boolean fEntityReferenceExpansion;
    int fWhatToShow;
    NodeFilter fNodeFilter;
    Node fCurrentNode;
    Node fRoot;
    private boolean fUseIsSameNode;
    
    public TreeWalkerImpl(final Node node, final int fWhatToShow, final NodeFilter fNodeFilter, final boolean fEntityReferenceExpansion) {
        this.fEntityReferenceExpansion = false;
        this.fWhatToShow = -1;
        this.fCurrentNode = node;
        this.fRoot = node;
        this.fUseIsSameNode = this.useIsSameNode(node);
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
    
    public void setWhatShow(final int fWhatToShow) {
        this.fWhatToShow = fWhatToShow;
    }
    
    public NodeFilter getFilter() {
        return this.fNodeFilter;
    }
    
    public boolean getExpandEntityReferences() {
        return this.fEntityReferenceExpansion;
    }
    
    public Node getCurrentNode() {
        return this.fCurrentNode;
    }
    
    public void setCurrentNode(final Node fCurrentNode) {
        if (fCurrentNode == null) {
            throw new DOMException((short)9, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null));
        }
        this.fCurrentNode = fCurrentNode;
    }
    
    public Node parentNode() {
        if (this.fCurrentNode == null) {
            return null;
        }
        final Node parentNode = this.getParentNode(this.fCurrentNode);
        if (parentNode != null) {
            this.fCurrentNode = parentNode;
        }
        return parentNode;
    }
    
    public Node firstChild() {
        if (this.fCurrentNode == null) {
            return null;
        }
        final Node firstChild = this.getFirstChild(this.fCurrentNode);
        if (firstChild != null) {
            this.fCurrentNode = firstChild;
        }
        return firstChild;
    }
    
    public Node lastChild() {
        if (this.fCurrentNode == null) {
            return null;
        }
        final Node lastChild = this.getLastChild(this.fCurrentNode);
        if (lastChild != null) {
            this.fCurrentNode = lastChild;
        }
        return lastChild;
    }
    
    public Node previousSibling() {
        if (this.fCurrentNode == null) {
            return null;
        }
        final Node previousSibling = this.getPreviousSibling(this.fCurrentNode);
        if (previousSibling != null) {
            this.fCurrentNode = previousSibling;
        }
        return previousSibling;
    }
    
    public Node nextSibling() {
        if (this.fCurrentNode == null) {
            return null;
        }
        final Node nextSibling = this.getNextSibling(this.fCurrentNode);
        if (nextSibling != null) {
            this.fCurrentNode = nextSibling;
        }
        return nextSibling;
    }
    
    public Node previousNode() {
        if (this.fCurrentNode == null) {
            return null;
        }
        final Node previousSibling = this.getPreviousSibling(this.fCurrentNode);
        if (previousSibling == null) {
            final Node parentNode = this.getParentNode(this.fCurrentNode);
            if (parentNode != null) {
                return this.fCurrentNode = parentNode;
            }
            return null;
        }
        else {
            Node lastChild2;
            for (Node lastChild = lastChild2 = this.getLastChild(previousSibling); lastChild != null; lastChild = this.getLastChild(lastChild2)) {
                lastChild2 = lastChild;
            }
            final Node fCurrentNode = lastChild2;
            if (fCurrentNode != null) {
                return this.fCurrentNode = fCurrentNode;
            }
            if (previousSibling != null) {
                return this.fCurrentNode = previousSibling;
            }
            return null;
        }
    }
    
    public Node nextNode() {
        if (this.fCurrentNode == null) {
            return null;
        }
        final Node firstChild = this.getFirstChild(this.fCurrentNode);
        if (firstChild != null) {
            return this.fCurrentNode = firstChild;
        }
        final Node nextSibling = this.getNextSibling(this.fCurrentNode);
        if (nextSibling != null) {
            return this.fCurrentNode = nextSibling;
        }
        for (Node node = this.getParentNode(this.fCurrentNode); node != null; node = this.getParentNode(node)) {
            final Node nextSibling2 = this.getNextSibling(node);
            if (nextSibling2 != null) {
                return this.fCurrentNode = nextSibling2;
            }
        }
        return null;
    }
    
    Node getParentNode(final Node node) {
        if (node == null || this.isSameNode(node, this.fRoot)) {
            return null;
        }
        final Node parentNode = node.getParentNode();
        if (parentNode == null) {
            return null;
        }
        if (this.acceptNode(parentNode) == 1) {
            return parentNode;
        }
        return this.getParentNode(parentNode);
    }
    
    Node getNextSibling(final Node node) {
        return this.getNextSibling(node, this.fRoot);
    }
    
    Node getNextSibling(final Node node, final Node node2) {
        if (node == null || this.isSameNode(node, node2)) {
            return null;
        }
        final Node nextSibling = node.getNextSibling();
        if (nextSibling == null) {
            final Node parentNode = node.getParentNode();
            if (parentNode == null || this.isSameNode(parentNode, node2)) {
                return null;
            }
            if (this.acceptNode(parentNode) == 3) {
                return this.getNextSibling(parentNode, node2);
            }
            return null;
        }
        else {
            final short acceptNode = this.acceptNode(nextSibling);
            if (acceptNode == 1) {
                return nextSibling;
            }
            if (acceptNode != 3) {
                return this.getNextSibling(nextSibling, node2);
            }
            final Node firstChild = this.getFirstChild(nextSibling);
            if (firstChild == null) {
                return this.getNextSibling(nextSibling, node2);
            }
            return firstChild;
        }
    }
    
    Node getPreviousSibling(final Node node) {
        return this.getPreviousSibling(node, this.fRoot);
    }
    
    Node getPreviousSibling(final Node node, final Node node2) {
        if (node == null || this.isSameNode(node, node2)) {
            return null;
        }
        final Node previousSibling = node.getPreviousSibling();
        if (previousSibling == null) {
            final Node parentNode = node.getParentNode();
            if (parentNode == null || this.isSameNode(parentNode, node2)) {
                return null;
            }
            if (this.acceptNode(parentNode) == 3) {
                return this.getPreviousSibling(parentNode, node2);
            }
            return null;
        }
        else {
            final short acceptNode = this.acceptNode(previousSibling);
            if (acceptNode == 1) {
                return previousSibling;
            }
            if (acceptNode != 3) {
                return this.getPreviousSibling(previousSibling, node2);
            }
            final Node lastChild = this.getLastChild(previousSibling);
            if (lastChild == null) {
                return this.getPreviousSibling(previousSibling, node2);
            }
            return lastChild;
        }
    }
    
    Node getFirstChild(final Node node) {
        if (node == null) {
            return null;
        }
        if (!this.fEntityReferenceExpansion && node.getNodeType() == 5) {
            return null;
        }
        final Node firstChild = node.getFirstChild();
        if (firstChild == null) {
            return null;
        }
        final short acceptNode = this.acceptNode(firstChild);
        if (acceptNode == 1) {
            return firstChild;
        }
        if (acceptNode != 3 || !firstChild.hasChildNodes()) {
            return this.getNextSibling(firstChild, node);
        }
        final Node firstChild2 = this.getFirstChild(firstChild);
        if (firstChild2 == null) {
            return this.getNextSibling(firstChild, node);
        }
        return firstChild2;
    }
    
    Node getLastChild(final Node node) {
        if (node == null) {
            return null;
        }
        if (!this.fEntityReferenceExpansion && node.getNodeType() == 5) {
            return null;
        }
        final Node lastChild = node.getLastChild();
        if (lastChild == null) {
            return null;
        }
        final short acceptNode = this.acceptNode(lastChild);
        if (acceptNode == 1) {
            return lastChild;
        }
        if (acceptNode != 3 || !lastChild.hasChildNodes()) {
            return this.getPreviousSibling(lastChild, node);
        }
        final Node lastChild2 = this.getLastChild(lastChild);
        if (lastChild2 == null) {
            return this.getPreviousSibling(lastChild, node);
        }
        return lastChild2;
    }
    
    short acceptNode(final Node node) {
        if (this.fNodeFilter == null) {
            if ((this.fWhatToShow & 1 << node.getNodeType() - 1) != 0x0) {
                return 1;
            }
            return 3;
        }
        else {
            if ((this.fWhatToShow & 1 << node.getNodeType() - 1) != 0x0) {
                return this.fNodeFilter.acceptNode(node);
            }
            return 3;
        }
    }
    
    private boolean useIsSameNode(final Node node) {
        if (node instanceof NodeImpl) {
            return false;
        }
        final Document document = (Document)((node.getNodeType() == 9) ? node : node.getOwnerDocument());
        return document != null && document.getImplementation().hasFeature("Core", "3.0");
    }
    
    private boolean isSameNode(final Node node, final Node node2) {
        return this.fUseIsSameNode ? node.isSameNode(node2) : (node == node2);
    }
}
