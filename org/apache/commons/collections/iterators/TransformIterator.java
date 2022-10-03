package org.apache.commons.collections.iterators;

import org.apache.commons.collections.Transformer;
import java.util.Iterator;

public class TransformIterator implements Iterator
{
    private Iterator iterator;
    private Transformer transformer;
    
    public TransformIterator() {
    }
    
    public TransformIterator(final Iterator iterator) {
        this.iterator = iterator;
    }
    
    public TransformIterator(final Iterator iterator, final Transformer transformer) {
        this.iterator = iterator;
        this.transformer = transformer;
    }
    
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
    
    public Object next() {
        return this.transform(this.iterator.next());
    }
    
    public void remove() {
        this.iterator.remove();
    }
    
    public Iterator getIterator() {
        return this.iterator;
    }
    
    public void setIterator(final Iterator iterator) {
        this.iterator = iterator;
    }
    
    public Transformer getTransformer() {
        return this.transformer;
    }
    
    public void setTransformer(final Transformer transformer) {
        this.transformer = transformer;
    }
    
    protected Object transform(final Object source) {
        if (this.transformer != null) {
            return this.transformer.transform(source);
        }
        return source;
    }
}
