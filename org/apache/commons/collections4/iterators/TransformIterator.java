package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.Transformer;
import java.util.Iterator;

public class TransformIterator<I, O> implements Iterator<O>
{
    private Iterator<? extends I> iterator;
    private Transformer<? super I, ? extends O> transformer;
    
    public TransformIterator() {
    }
    
    public TransformIterator(final Iterator<? extends I> iterator) {
        this.iterator = iterator;
    }
    
    public TransformIterator(final Iterator<? extends I> iterator, final Transformer<? super I, ? extends O> transformer) {
        this.iterator = iterator;
        this.transformer = transformer;
    }
    
    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
    
    @Override
    public O next() {
        return this.transform(this.iterator.next());
    }
    
    @Override
    public void remove() {
        this.iterator.remove();
    }
    
    public Iterator<? extends I> getIterator() {
        return this.iterator;
    }
    
    public void setIterator(final Iterator<? extends I> iterator) {
        this.iterator = iterator;
    }
    
    public Transformer<? super I, ? extends O> getTransformer() {
        return this.transformer;
    }
    
    public void setTransformer(final Transformer<? super I, ? extends O> transformer) {
        this.transformer = transformer;
    }
    
    protected O transform(final I source) {
        return (O)this.transformer.transform(source);
    }
}
