package org.w3c.tidy;

import org.w3c.dom.NodeList;

public class DOMNodeListByTagNameImpl implements NodeList
{
    private Node first;
    private String tagName;
    private int currIndex;
    private int maxIndex;
    private Node currNode;
    
    protected DOMNodeListByTagNameImpl(final Node first, final String tagName) {
        this.first = first;
        this.tagName = tagName;
    }
    
    public org.w3c.dom.Node item(final int maxIndex) {
        this.currIndex = 0;
        this.maxIndex = maxIndex;
        this.preTraverse(this.first);
        if (this.currIndex > this.maxIndex && this.currNode != null) {
            return this.currNode.getAdapter();
        }
        return null;
    }
    
    public int getLength() {
        this.currIndex = 0;
        this.maxIndex = Integer.MAX_VALUE;
        this.preTraverse(this.first);
        return this.currIndex;
    }
    
    protected void preTraverse(Node currNode) {
        if (currNode == null) {
            return;
        }
        if ((currNode.type == 5 || currNode.type == 7) && this.currIndex <= this.maxIndex && (this.tagName.equals("*") || this.tagName.equals(currNode.element))) {
            ++this.currIndex;
            this.currNode = currNode;
        }
        if (this.currIndex > this.maxIndex) {
            return;
        }
        for (currNode = currNode.content; currNode != null; currNode = currNode.next) {
            this.preTraverse(currNode);
        }
    }
}
