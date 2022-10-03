package org.htmlparser.util;

import org.htmlparser.Node;

public class NodeTreeWalker implements NodeIterator
{
    protected Node mRootNode;
    protected Node mCurrentNode;
    protected Node mNextNode;
    protected int mMaxDepth;
    protected boolean mDepthFirst;
    
    public NodeTreeWalker(final Node rootNode) {
        this(rootNode, true, -1);
    }
    
    public NodeTreeWalker(final Node rootNode, final boolean depthFirst) {
        this(rootNode, depthFirst, -1);
    }
    
    public NodeTreeWalker(final Node rootNode, final boolean depthFirst, final int maxDepth) {
        if (maxDepth < 1 && maxDepth != -1) {
            throw new IllegalArgumentException("Paramater maxDepth must be > 0 or equal to -1.");
        }
        this.initRootNode(rootNode);
        this.mDepthFirst = depthFirst;
        this.mMaxDepth = maxDepth;
    }
    
    public boolean isDepthFirst() {
        return this.mDepthFirst;
    }
    
    public void setDepthFirst(final boolean depthFirst) {
        if (this.mDepthFirst != depthFirst) {
            this.mNextNode = null;
        }
        this.mDepthFirst = depthFirst;
    }
    
    public int getMaxDepth() {
        return this.mMaxDepth;
    }
    
    public void removeMaxDepthRestriction() {
        this.mMaxDepth = -1;
    }
    
    public Node getRootNode() {
        return this.mRootNode;
    }
    
    public Node getCurrentNode() {
        return this.mCurrentNode;
    }
    
    public void setCurrentNodeAsRootNode() throws NullPointerException {
        if (this.mCurrentNode == null) {
            throw new NullPointerException("Current Node is null, cannot set as root Node.");
        }
        this.initRootNode(this.mCurrentNode);
    }
    
    public void setRootNode(final Node rootNode) throws NullPointerException {
        this.initRootNode(rootNode);
    }
    
    public void reset() {
        this.mCurrentNode = null;
        this.mNextNode = null;
    }
    
    public Node nextNode() {
        if (this.mNextNode != null) {
            this.mCurrentNode = this.mNextNode;
            this.mNextNode = null;
        }
        else if (this.mCurrentNode == null) {
            this.mCurrentNode = this.mRootNode.getFirstChild();
        }
        else if (this.mDepthFirst) {
            this.mCurrentNode = this.getNextNodeDepthFirst();
        }
        else {
            this.mCurrentNode = this.getNextNodeBreadthFirst();
        }
        return this.mCurrentNode;
    }
    
    public int getCurrentNodeDepth() {
        int depth = 0;
        if (this.mCurrentNode != null) {
            for (Node traverseNode = this.mCurrentNode; traverseNode != this.mRootNode; traverseNode = traverseNode.getParent()) {
                ++depth;
            }
        }
        return depth;
    }
    
    public boolean hasMoreNodes() {
        if (this.mNextNode == null) {
            if (this.mCurrentNode == null) {
                this.mNextNode = this.mRootNode.getFirstChild();
            }
            else if (this.mDepthFirst) {
                this.mNextNode = this.getNextNodeDepthFirst();
            }
            else {
                this.mNextNode = this.getNextNodeBreadthFirst();
            }
        }
        return this.mNextNode != null;
    }
    
    protected void initRootNode(final Node rootNode) throws NullPointerException {
        if (rootNode == null) {
            throw new NullPointerException("Root Node cannot be null.");
        }
        this.mRootNode = rootNode;
        this.mCurrentNode = null;
        this.mNextNode = null;
    }
    
    protected Node getNextNodeDepthFirst() {
        final int currentDepth = this.getCurrentNodeDepth();
        Node traverseNode = null;
        if (this.mMaxDepth == -1 || currentDepth < this.mMaxDepth) {
            traverseNode = this.mCurrentNode.getFirstChild();
            if (traverseNode != null) {
                return traverseNode;
            }
        }
        traverseNode = this.mCurrentNode;
        Node tempNextSibling = null;
        while (traverseNode != this.mRootNode && (tempNextSibling = traverseNode.getNextSibling()) == null) {
            traverseNode = traverseNode.getParent();
        }
        return tempNextSibling;
    }
    
    protected Node getNextNodeBreadthFirst() {
        Node traverseNode = this.mCurrentNode.getNextSibling();
        if (traverseNode != null) {
            return traverseNode;
        }
        int depth = this.getCurrentNodeDepth();
        traverseNode = this.mCurrentNode.getParent();
        int currentDepth = depth - 1;
        while (currentDepth > 0) {
            Node tempNextSibling = null;
            while ((tempNextSibling = traverseNode.getNextSibling()) == null && traverseNode != this.mRootNode) {
                traverseNode = traverseNode.getParent();
                --currentDepth;
            }
            if (traverseNode == this.mRootNode) {
                break;
            }
            traverseNode = tempNextSibling;
            if (traverseNode == null) {
                continue;
            }
            for (NodeList traverseNodeList = traverseNode.getChildren(); traverseNodeList != null && traverseNodeList.size() != 0; traverseNodeList = traverseNode.getChildren()) {
                traverseNode = traverseNode.getFirstChild();
                if (++currentDepth == depth) {
                    return traverseNode;
                }
            }
        }
        if (this.mMaxDepth != -1 && depth >= this.mMaxDepth) {
            return null;
        }
        traverseNode = this.mRootNode.getFirstChild();
        ++depth;
        currentDepth = 1;
        while (currentDepth > 0) {
            for (NodeList traverseNodeList = traverseNode.getChildren(); traverseNodeList != null && traverseNodeList.size() != 0; traverseNodeList = traverseNode.getChildren()) {
                traverseNode = traverseNode.getFirstChild();
                if (++currentDepth == depth) {
                    return traverseNode;
                }
            }
            while (traverseNode.getNextSibling() == null && traverseNode != this.mRootNode) {
                traverseNode = traverseNode.getParent();
                --currentDepth;
            }
            traverseNode = traverseNode.getNextSibling();
            if (traverseNode == null) {
                return null;
            }
        }
        return null;
    }
}
