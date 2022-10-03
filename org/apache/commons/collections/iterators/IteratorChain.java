package org.apache.commons.collections.iterators;

import org.apache.commons.collections.list.UnmodifiableList;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class IteratorChain implements Iterator
{
    protected final List iteratorChain;
    protected int currentIteratorIndex;
    protected Iterator currentIterator;
    protected Iterator lastUsedIterator;
    protected boolean isLocked;
    
    public IteratorChain() {
        this.iteratorChain = new ArrayList();
        this.currentIteratorIndex = 0;
        this.currentIterator = null;
        this.lastUsedIterator = null;
        this.isLocked = false;
    }
    
    public IteratorChain(final Iterator iterator) {
        this.iteratorChain = new ArrayList();
        this.currentIteratorIndex = 0;
        this.currentIterator = null;
        this.lastUsedIterator = null;
        this.isLocked = false;
        this.addIterator(iterator);
    }
    
    public IteratorChain(final Iterator a, final Iterator b) {
        this.iteratorChain = new ArrayList();
        this.currentIteratorIndex = 0;
        this.currentIterator = null;
        this.lastUsedIterator = null;
        this.isLocked = false;
        this.addIterator(a);
        this.addIterator(b);
    }
    
    public IteratorChain(final Iterator[] iterators) {
        this.iteratorChain = new ArrayList();
        this.currentIteratorIndex = 0;
        this.currentIterator = null;
        this.lastUsedIterator = null;
        this.isLocked = false;
        for (int i = 0; i < iterators.length; ++i) {
            this.addIterator(iterators[i]);
        }
    }
    
    public IteratorChain(final Collection iterators) {
        this.iteratorChain = new ArrayList();
        this.currentIteratorIndex = 0;
        this.currentIterator = null;
        this.lastUsedIterator = null;
        this.isLocked = false;
        final Iterator it = iterators.iterator();
        while (it.hasNext()) {
            final Iterator item = it.next();
            this.addIterator(item);
        }
    }
    
    public void addIterator(final Iterator iterator) {
        this.checkLocked();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        this.iteratorChain.add(iterator);
    }
    
    public void setIterator(final int index, final Iterator iterator) throws IndexOutOfBoundsException {
        this.checkLocked();
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        this.iteratorChain.set(index, iterator);
    }
    
    public List getIterators() {
        return UnmodifiableList.decorate(this.iteratorChain);
    }
    
    public int size() {
        return this.iteratorChain.size();
    }
    
    public boolean isLocked() {
        return this.isLocked;
    }
    
    private void checkLocked() {
        if (this.isLocked) {
            throw new UnsupportedOperationException("IteratorChain cannot be changed after the first use of a method from the Iterator interface");
        }
    }
    
    private void lockChain() {
        if (!this.isLocked) {
            this.isLocked = true;
        }
    }
    
    protected void updateCurrentIterator() {
        if (this.currentIterator == null) {
            if (this.iteratorChain.isEmpty()) {
                this.currentIterator = EmptyIterator.INSTANCE;
            }
            else {
                this.currentIterator = this.iteratorChain.get(0);
            }
            this.lastUsedIterator = this.currentIterator;
        }
        while (!this.currentIterator.hasNext() && this.currentIteratorIndex < this.iteratorChain.size() - 1) {
            ++this.currentIteratorIndex;
            this.currentIterator = this.iteratorChain.get(this.currentIteratorIndex);
        }
    }
    
    public boolean hasNext() {
        this.lockChain();
        this.updateCurrentIterator();
        this.lastUsedIterator = this.currentIterator;
        return this.currentIterator.hasNext();
    }
    
    public Object next() {
        this.lockChain();
        this.updateCurrentIterator();
        this.lastUsedIterator = this.currentIterator;
        return this.currentIterator.next();
    }
    
    public void remove() {
        this.lockChain();
        if (this.currentIterator == null) {
            this.updateCurrentIterator();
        }
        this.lastUsedIterator.remove();
    }
}
