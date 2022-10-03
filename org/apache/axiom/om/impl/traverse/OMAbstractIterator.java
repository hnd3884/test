package org.apache.axiom.om.impl.traverse;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;
import java.util.Iterator;

public abstract class OMAbstractIterator implements Iterator
{
    private OMNode currentNode;
    private OMContainer currentParent;
    private OMNode nextNode;
    private boolean noMoreNodes;
    private boolean nextCalled;
    
    public OMAbstractIterator(final OMNode firstNode) {
        if (firstNode == null) {
            this.noMoreNodes = true;
        }
        else {
            this.nextNode = firstNode;
        }
    }
    
    protected abstract OMNode getNextNode(final OMNode p0);
    
    public boolean hasNext() {
        if (this.noMoreNodes) {
            return false;
        }
        if (this.nextNode != null) {
            return true;
        }
        if (this.currentNode.getParent() != this.currentParent) {
            throw new ConcurrentModificationException("The current node has been removed using a method other than Iterator#remove()");
        }
        this.nextNode = this.getNextNode(this.currentNode);
        this.noMoreNodes = (this.nextNode == null);
        return !this.noMoreNodes;
    }
    
    public Object next() {
        if (this.hasNext()) {
            this.currentNode = this.nextNode;
            this.currentParent = this.currentNode.getParent();
            this.nextNode = null;
            this.nextCalled = true;
            return this.currentNode;
        }
        throw new NoSuchElementException();
    }
    
    public void remove() {
        if (!this.nextCalled) {
            throw new IllegalStateException("next() has not yet been called");
        }
        this.hasNext();
        this.currentNode.detach();
        this.nextCalled = false;
    }
}
