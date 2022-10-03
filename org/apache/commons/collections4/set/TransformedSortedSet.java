package org.apache.commons.collections4.set;

import java.util.Comparator;
import java.util.Set;
import org.apache.commons.collections4.Transformer;
import java.util.SortedSet;

public class TransformedSortedSet<E> extends TransformedSet<E> implements SortedSet<E>
{
    private static final long serialVersionUID = -1675486811351124386L;
    
    public static <E> TransformedSortedSet<E> transformingSortedSet(final SortedSet<E> set, final Transformer<? super E, ? extends E> transformer) {
        return new TransformedSortedSet<E>(set, transformer);
    }
    
    public static <E> TransformedSortedSet<E> transformedSortedSet(final SortedSet<E> set, final Transformer<? super E, ? extends E> transformer) {
        final TransformedSortedSet<E> decorated = new TransformedSortedSet<E>(set, transformer);
        if (set.size() > 0) {
            final E[] values = (E[])set.toArray();
            set.clear();
            for (final E value : values) {
                decorated.decorated().add(transformer.transform(value));
            }
        }
        return decorated;
    }
    
    protected TransformedSortedSet(final SortedSet<E> set, final Transformer<? super E, ? extends E> transformer) {
        super(set, transformer);
    }
    
    protected SortedSet<E> getSortedSet() {
        return (SortedSet)this.decorated();
    }
    
    @Override
    public E first() {
        return this.getSortedSet().first();
    }
    
    @Override
    public E last() {
        return this.getSortedSet().last();
    }
    
    @Override
    public Comparator<? super E> comparator() {
        return this.getSortedSet().comparator();
    }
    
    @Override
    public SortedSet<E> subSet(final E fromElement, final E toElement) {
        final SortedSet<E> set = this.getSortedSet().subSet(fromElement, toElement);
        return new TransformedSortedSet((SortedSet<Object>)set, (Transformer<? super Object, ?>)this.transformer);
    }
    
    @Override
    public SortedSet<E> headSet(final E toElement) {
        final SortedSet<E> set = this.getSortedSet().headSet(toElement);
        return new TransformedSortedSet((SortedSet<Object>)set, (Transformer<? super Object, ?>)this.transformer);
    }
    
    @Override
    public SortedSet<E> tailSet(final E fromElement) {
        final SortedSet<E> set = this.getSortedSet().tailSet(fromElement);
        return new TransformedSortedSet((SortedSet<Object>)set, (Transformer<? super Object, ?>)this.transformer);
    }
}
