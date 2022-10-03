package org.apache.commons.collections4;

import java.util.List;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.commons.collections4.iterators.SingletonIterator;

public class FluentIterable<E> implements Iterable<E>
{
    private final Iterable<E> iterable;
    
    public static <T> FluentIterable<T> empty() {
        return IterableUtils.EMPTY_ITERABLE;
    }
    
    public static <T> FluentIterable<T> of(final T singleton) {
        return of(IteratorUtils.asIterable((Iterator<? extends T>)new SingletonIterator<T>((T)singleton, false)));
    }
    
    public static <T> FluentIterable<T> of(final T... elements) {
        return of((Iterable<T>)Arrays.asList(elements));
    }
    
    public static <T> FluentIterable<T> of(final Iterable<T> iterable) {
        IterableUtils.checkNotNull(iterable);
        if (iterable instanceof FluentIterable) {
            return (FluentIterable)iterable;
        }
        return new FluentIterable<T>(iterable);
    }
    
    FluentIterable() {
        this.iterable = this;
    }
    
    private FluentIterable(final Iterable<E> iterable) {
        this.iterable = iterable;
    }
    
    public FluentIterable<E> append(final E... elements) {
        return this.append((Iterable<? extends E>)Arrays.asList(elements));
    }
    
    public FluentIterable<E> append(final Iterable<? extends E> other) {
        return of(IterableUtils.chainedIterable((Iterable<? extends E>)this.iterable, other));
    }
    
    public FluentIterable<E> collate(final Iterable<? extends E> other) {
        return of(IterableUtils.collatedIterable((Iterable<? extends E>)this.iterable, other));
    }
    
    public FluentIterable<E> collate(final Iterable<? extends E> other, final Comparator<? super E> comparator) {
        return of(IterableUtils.collatedIterable(comparator, (Iterable<? extends E>)this.iterable, other));
    }
    
    public FluentIterable<E> eval() {
        return of((Iterable<E>)this.toList());
    }
    
    public FluentIterable<E> filter(final Predicate<? super E> predicate) {
        return of((Iterable<E>)IterableUtils.filteredIterable((Iterable<T>)this.iterable, (Predicate<? super T>)predicate));
    }
    
    public FluentIterable<E> limit(final long maxSize) {
        return of((Iterable<E>)IterableUtils.boundedIterable((Iterable<T>)this.iterable, maxSize));
    }
    
    public FluentIterable<E> loop() {
        return of((Iterable<E>)IterableUtils.loopingIterable((Iterable<T>)this.iterable));
    }
    
    public FluentIterable<E> reverse() {
        return of((Iterable<E>)IterableUtils.reversedIterable((Iterable<T>)this.iterable));
    }
    
    public FluentIterable<E> skip(final long elementsToSkip) {
        return of((Iterable<E>)IterableUtils.skippingIterable((Iterable<T>)this.iterable, elementsToSkip));
    }
    
    public <O> FluentIterable<O> transform(final Transformer<? super E, ? extends O> transformer) {
        return of(IterableUtils.transformedIterable(this.iterable, transformer));
    }
    
    public FluentIterable<E> unique() {
        return of((Iterable<E>)IterableUtils.uniqueIterable((Iterable<T>)this.iterable));
    }
    
    public FluentIterable<E> unmodifiable() {
        return of((Iterable<E>)IterableUtils.unmodifiableIterable((Iterable<T>)this.iterable));
    }
    
    public FluentIterable<E> zip(final Iterable<? extends E> other) {
        return of(IterableUtils.zippingIterable((Iterable<? extends E>)this.iterable, other));
    }
    
    public FluentIterable<E> zip(final Iterable<? extends E>... others) {
        return of(IterableUtils.zippingIterable((Iterable<? extends E>)this.iterable, others));
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.iterable.iterator();
    }
    
    public Enumeration<E> asEnumeration() {
        return IteratorUtils.asEnumeration(this.iterator());
    }
    
    public boolean allMatch(final Predicate<? super E> predicate) {
        return IterableUtils.matchesAll(this.iterable, predicate);
    }
    
    public boolean anyMatch(final Predicate<? super E> predicate) {
        return IterableUtils.matchesAny(this.iterable, predicate);
    }
    
    public boolean isEmpty() {
        return IterableUtils.isEmpty(this.iterable);
    }
    
    public boolean contains(final Object object) {
        return IterableUtils.contains(this.iterable, object);
    }
    
    public void forEach(final Closure<? super E> closure) {
        IterableUtils.forEach(this.iterable, closure);
    }
    
    public E get(final int position) {
        return IterableUtils.get(this.iterable, position);
    }
    
    public int size() {
        return IterableUtils.size(this.iterable);
    }
    
    public void copyInto(final Collection<? super E> collection) {
        if (collection == null) {
            throw new NullPointerException("Collection must not be null");
        }
        CollectionUtils.addAll(collection, (Iterable<? extends E>)this.iterable);
    }
    
    public E[] toArray(final Class<E> arrayClass) {
        return IteratorUtils.toArray(this.iterator(), arrayClass);
    }
    
    public List<E> toList() {
        return IterableUtils.toList(this.iterable);
    }
    
    @Override
    public String toString() {
        return IterableUtils.toString(this.iterable);
    }
}
