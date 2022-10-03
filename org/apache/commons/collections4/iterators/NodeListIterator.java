package org.apache.commons.collections4.iterators;

import java.util.NoSuchElementException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.Iterator;

public class NodeListIterator implements Iterator<Node>
{
    private final NodeList nodeList;
    private int index;
    
    public NodeListIterator(final Node node) {
        this.index = 0;
        if (node == null) {
            throw new NullPointerException("Node must not be null.");
        }
        this.nodeList = node.getChildNodes();
    }
    
    public NodeListIterator(final NodeList nodeList) {
        this.index = 0;
        if (nodeList == null) {
            throw new NullPointerException("NodeList must not be null.");
        }
        this.nodeList = nodeList;
    }
    
    @Override
    public boolean hasNext() {
        return this.nodeList != null && this.index < this.nodeList.getLength();
    }
    
    @Override
    public Node next() {
        if (this.nodeList != null && this.index < this.nodeList.getLength()) {
            return this.nodeList.item(this.index++);
        }
        throw new NoSuchElementException("underlying nodeList has no more elements");
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() method not supported for a NodeListIterator.");
    }
}
