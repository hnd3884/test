package org.apache.commons.collections4.collection;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.collections4.Transformer;

public class TransformedCollection<E> extends AbstractCollectionDecorator<E>
{
    private static final long serialVersionUID = 8692300188161871514L;
    protected final Transformer<? super E, ? extends E> transformer;
    
    public static <E> TransformedCollection<E> transformingCollection(final Collection<E> coll, final Transformer<? super E, ? extends E> transformer) {
        return new TransformedCollection<E>(coll, transformer);
    }
    
    public static <E> TransformedCollection<E> transformedCollection(final Collection<E> collection, final Transformer<? super E, ? extends E> transformer) {
        final TransformedCollection<E> decorated = new TransformedCollection<E>(collection, transformer);
        if (collection.size() > 0) {
            final E[] values = (E[])collection.toArray();
            collection.clear();
            for (final E value : values) {
                decorated.decorated().add(transformer.transform(value));
            }
        }
        return decorated;
    }
    
    protected TransformedCollection(final Collection<E> coll, final Transformer<? super E, ? extends E> transformer) {
        super(coll);
        if (transformer == null) {
            throw new NullPointerException("Transformer must not be null");
        }
        this.transformer = transformer;
    }
    
    protected E transform(final E object) {
        return (E)this.transformer.transform(object);
    }
    
    protected Collection<E> transform(final Collection<? extends E> coll) {
        final List<E> list = new ArrayList<E>(coll.size());
        for (final E item : coll) {
            list.add(this.transform(item));
        }
        return list;
    }
    
    @Override
    public boolean add(final E object) {
        return this.decorated().add(this.transform(object));
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        return this.decorated().addAll(this.transform(coll));
    }
}
