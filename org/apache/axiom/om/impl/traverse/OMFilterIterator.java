package org.apache.axiom.om.impl.traverse;

import java.util.NoSuchElementException;
import org.apache.axiom.om.OMNode;
import java.util.Iterator;

public abstract class OMFilterIterator implements Iterator
{
    private final Iterator parent;
    private OMNode nextNode;
    private boolean noMoreNodes;
    
    public OMFilterIterator(final Iterator parent) {
        this.parent = parent;
    }
    
    protected abstract boolean matches(final OMNode p0);
    
    public boolean hasNext() {
        if (this.noMoreNodes) {
            return false;
        }
        if (this.nextNode != null) {
            return true;
        }
        while (this.parent.hasNext()) {
            final OMNode node = this.parent.next();
            if (this.matches(node)) {
                this.nextNode = node;
                return true;
            }
        }
        this.noMoreNodes = true;
        return false;
    }
    
    public Object next() {
        if (this.hasNext()) {
            final OMNode result = this.nextNode;
            this.nextNode = null;
            return result;
        }
        throw new NoSuchElementException();
    }
    
    public void remove() {
        this.parent.remove();
    }
}
