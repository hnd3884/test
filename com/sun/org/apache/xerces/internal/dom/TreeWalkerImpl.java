package com.sun.org.apache.xerces.internal.dom;

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
    
    public TreeWalkerImpl(final Node root, final int whatToShow, final NodeFilter nodeFilter, final boolean entityReferenceExpansion) {
        this.fEntityReferenceExpansion = false;
        this.fWhatToShow = -1;
        this.fCurrentNode = root;
        this.fRoot = root;
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
    
    public void setWhatShow(final int whatToShow) {
        this.fWhatToShow = whatToShow;
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
    public Node getCurrentNode() {
        return this.fCurrentNode;
    }
    
    @Override
    public void setCurrentNode(final Node node) {
        if (node == null) {
            final String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException((short)9, msg);
        }
        this.fCurrentNode = node;
    }
    
    @Override
    public Node parentNode() {
        if (this.fCurrentNode == null) {
            return null;
        }
        final Node node = this.getParentNode(this.fCurrentNode);
        if (node != null) {
            this.fCurrentNode = node;
        }
        return node;
    }
    
    @Override
    public Node firstChild() {
        if (this.fCurrentNode == null) {
            return null;
        }
        final Node node = this.getFirstChild(this.fCurrentNode);
        if (node != null) {
            this.fCurrentNode = node;
        }
        return node;
    }
    
    @Override
    public Node lastChild() {
        if (this.fCurrentNode == null) {
            return null;
        }
        final Node node = this.getLastChild(this.fCurrentNode);
        if (node != null) {
            this.fCurrentNode = node;
        }
        return node;
    }
    
    @Override
    public Node previousSibling() {
        if (this.fCurrentNode == null) {
            return null;
        }
        final Node node = this.getPreviousSibling(this.fCurrentNode);
        if (node != null) {
            this.fCurrentNode = node;
        }
        return node;
    }
    
    @Override
    public Node nextSibling() {
        if (this.fCurrentNode == null) {
            return null;
        }
        final Node node = this.getNextSibling(this.fCurrentNode);
        if (node != null) {
            this.fCurrentNode = node;
        }
        return node;
    }
    
    @Override
    public Node previousNode() {
        if (this.fCurrentNode == null) {
            return null;
        }
        Node result = this.getPreviousSibling(this.fCurrentNode);
        if (result == null) {
            result = this.getParentNode(this.fCurrentNode);
            if (result != null) {
                return this.fCurrentNode = result;
            }
            return null;
        }
        else {
            Node prev;
            for (Node lastChild = prev = this.getLastChild(result); lastChild != null; lastChild = this.getLastChild(prev)) {
                prev = lastChild;
            }
            Node lastChild = prev;
            if (lastChild != null) {
                return this.fCurrentNode = lastChild;
            }
            if (result != null) {
                return this.fCurrentNode = result;
            }
            return null;
        }
    }
    
    @Override
    public Node nextNode() {
        if (this.fCurrentNode == null) {
            return null;
        }
        Node result = this.getFirstChild(this.fCurrentNode);
        if (result != null) {
            return this.fCurrentNode = result;
        }
        result = this.getNextSibling(this.fCurrentNode);
        if (result != null) {
            return this.fCurrentNode = result;
        }
        for (Node parent = this.getParentNode(this.fCurrentNode); parent != null; parent = this.getParentNode(parent)) {
            result = this.getNextSibling(parent);
            if (result != null) {
                return this.fCurrentNode = result;
            }
        }
        return null;
    }
    
    Node getParentNode(final Node node) {
        if (node == null || node == this.fRoot) {
            return null;
        }
        final Node newNode = node.getParentNode();
        if (newNode == null) {
            return null;
        }
        final int accept = this.acceptNode(newNode);
        if (accept == 1) {
            return newNode;
        }
        return this.getParentNode(newNode);
    }
    
    Node getNextSibling(final Node node) {
        return this.getNextSibling(node, this.fRoot);
    }
    
    Node getNextSibling(final Node node, final Node root) {
        if (node == null || node == root) {
            return null;
        }
        Node newNode = node.getNextSibling();
        if (newNode == null) {
            newNode = node.getParentNode();
            if (newNode == null || newNode == root) {
                return null;
            }
            final int parentAccept = this.acceptNode(newNode);
            if (parentAccept == 3) {
                return this.getNextSibling(newNode, root);
            }
            return null;
        }
        else {
            final int accept = this.acceptNode(newNode);
            if (accept == 1) {
                return newNode;
            }
            if (accept != 3) {
                return this.getNextSibling(newNode, root);
            }
            final Node fChild = this.getFirstChild(newNode);
            if (fChild == null) {
                return this.getNextSibling(newNode, root);
            }
            return fChild;
        }
    }
    
    Node getPreviousSibling(final Node node) {
        return this.getPreviousSibling(node, this.fRoot);
    }
    
    Node getPreviousSibling(final Node node, final Node root) {
        if (node == null || node == root) {
            return null;
        }
        Node newNode = node.getPreviousSibling();
        if (newNode == null) {
            newNode = node.getParentNode();
            if (newNode == null || newNode == root) {
                return null;
            }
            final int parentAccept = this.acceptNode(newNode);
            if (parentAccept == 3) {
                return this.getPreviousSibling(newNode, root);
            }
            return null;
        }
        else {
            final int accept = this.acceptNode(newNode);
            if (accept == 1) {
                return newNode;
            }
            if (accept != 3) {
                return this.getPreviousSibling(newNode, root);
            }
            final Node fChild = this.getLastChild(newNode);
            if (fChild == null) {
                return this.getPreviousSibling(newNode, root);
            }
            return fChild;
        }
    }
    
    Node getFirstChild(final Node node) {
        if (node == null) {
            return null;
        }
        if (!this.fEntityReferenceExpansion && node.getNodeType() == 5) {
            return null;
        }
        final Node newNode = node.getFirstChild();
        if (newNode == null) {
            return null;
        }
        final int accept = this.acceptNode(newNode);
        if (accept == 1) {
            return newNode;
        }
        if (accept != 3 || !newNode.hasChildNodes()) {
            return this.getNextSibling(newNode, node);
        }
        final Node fChild = this.getFirstChild(newNode);
        if (fChild == null) {
            return this.getNextSibling(newNode, node);
        }
        return fChild;
    }
    
    Node getLastChild(final Node node) {
        if (node == null) {
            return null;
        }
        if (!this.fEntityReferenceExpansion && node.getNodeType() == 5) {
            return null;
        }
        final Node newNode = node.getLastChild();
        if (newNode == null) {
            return null;
        }
        final int accept = this.acceptNode(newNode);
        if (accept == 1) {
            return newNode;
        }
        if (accept != 3 || !newNode.hasChildNodes()) {
            return this.getPreviousSibling(newNode, node);
        }
        final Node lChild = this.getLastChild(newNode);
        if (lChild == null) {
            return this.getPreviousSibling(newNode, node);
        }
        return lChild;
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
}
