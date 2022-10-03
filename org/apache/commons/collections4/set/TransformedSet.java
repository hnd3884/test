package org.apache.commons.collections4.set;

import java.util.Collection;
import org.apache.commons.collections4.Transformer;
import java.util.Set;
import org.apache.commons.collections4.collection.TransformedCollection;

public class TransformedSet<E> extends TransformedCollection<E> implements Set<E>
{
    private static final long serialVersionUID = 306127383500410386L;
    
    public static <E> TransformedSet<E> transformingSet(final Set<E> set, final Transformer<? super E, ? extends E> transformer) {
        return new TransformedSet<E>(set, transformer);
    }
    
    public static <E> Set<E> transformedSet(final Set<E> set, final Transformer<? super E, ? extends E> transformer) {
        final TransformedSet<E> decorated = new TransformedSet<E>(set, transformer);
        if (set.size() > 0) {
            final E[] values = (E[])set.toArray();
            set.clear();
            for (final E value : values) {
                decorated.decorated().add(transformer.transform(value));
            }
        }
        return decorated;
    }
    
    protected TransformedSet(final Set<E> set, final Transformer<? super E, ? extends E> transformer) {
        super(set, transformer);
    }
    
    @Override
    public boolean equals(final Object object) {
        return object == this || this.decorated().equals(object);
    }
    
    @Override
    public int hashCode() {
        return this.decorated().hashCode();
    }
}
