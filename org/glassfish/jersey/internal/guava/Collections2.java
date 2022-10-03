package org.glassfish.jersey.internal.guava;

import java.util.Iterator;
import java.util.AbstractCollection;
import java.util.function.Function;
import java.util.Collection;

final class Collections2
{
    static final Joiner STANDARD_JOINER;
    
    private Collections2() {
    }
    
    static boolean safeContains(final Collection<?> collection, final Object object) {
        Preconditions.checkNotNull(collection);
        try {
            return collection.contains(object);
        }
        catch (final ClassCastException e) {
            return false;
        }
        catch (final NullPointerException e2) {
            return false;
        }
    }
    
    static boolean safeRemove(final Collection<?> collection, final Object object) {
        Preconditions.checkNotNull(collection);
        try {
            return collection.remove(object);
        }
        catch (final ClassCastException e) {
            return false;
        }
        catch (final NullPointerException e2) {
            return false;
        }
    }
    
    public static <F, T> Collection<T> transform(final Collection<F> fromCollection, final Function<? super F, T> function) {
        return (Collection<T>)new TransformedCollection((Collection<Object>)fromCollection, (Function<? super Object, ?>)function);
    }
    
    static StringBuilder newStringBuilderForCollection(final int size) {
        CollectPreconditions.checkNonnegative(size, "size");
        return new StringBuilder((int)Math.min(size * 8L, 1073741824L));
    }
    
    static <T> Collection<T> cast(final Iterable<T> iterable) {
        return (Collection)iterable;
    }
    
    static {
        STANDARD_JOINER = Joiner.on();
    }
    
    static class TransformedCollection<F, T> extends AbstractCollection<T>
    {
        final Collection<F> fromCollection;
        final Function<? super F, ? extends T> function;
        
        TransformedCollection(final Collection<F> fromCollection, final Function<? super F, ? extends T> function) {
            this.fromCollection = Preconditions.checkNotNull(fromCollection);
            this.function = Preconditions.checkNotNull(function);
        }
        
        @Override
        public void clear() {
            this.fromCollection.clear();
        }
        
        @Override
        public boolean isEmpty() {
            return this.fromCollection.isEmpty();
        }
        
        @Override
        public Iterator<T> iterator() {
            return Iterators.transform(this.fromCollection.iterator(), this.function);
        }
        
        @Override
        public int size() {
            return this.fromCollection.size();
        }
    }
}
