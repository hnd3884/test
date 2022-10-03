package org.apache.commons.collections4;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Set;
import org.apache.commons.collections4.functors.EqualPredicate;
import java.util.Collection;
import org.apache.commons.collections4.iterators.UniqueFilterIterator;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import java.util.List;
import java.util.Comparator;
import org.apache.commons.collections4.iterators.LazyIteratorChain;
import java.util.Iterator;

public class IterableUtils
{
    static final FluentIterable EMPTY_ITERABLE;
    
    public static <E> Iterable<E> emptyIterable() {
        return IterableUtils.EMPTY_ITERABLE;
    }
    
    public static <E> Iterable<E> chainedIterable(final Iterable<? extends E> a, final Iterable<? extends E> b) {
        return chainedIterable((Iterable<? extends E>[])new Iterable[] { a, b });
    }
    
    public static <E> Iterable<E> chainedIterable(final Iterable<? extends E> a, final Iterable<? extends E> b, final Iterable<? extends E> c) {
        return chainedIterable((Iterable<? extends E>[])new Iterable[] { a, b, c });
    }
    
    public static <E> Iterable<E> chainedIterable(final Iterable<? extends E> a, final Iterable<? extends E> b, final Iterable<? extends E> c, final Iterable<? extends E> d) {
        return chainedIterable((Iterable<? extends E>[])new Iterable[] { a, b, c, d });
    }
    
    public static <E> Iterable<E> chainedIterable(final Iterable<? extends E>... iterables) {
        checkNotNull((Iterable<?>[])iterables);
        return new FluentIterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return new LazyIteratorChain<E>() {
                    @Override
                    protected Iterator<? extends E> nextIterator(final int count) {
                        if (count > iterables.length) {
                            return null;
                        }
                        return iterables[count - 1].iterator();
                    }
                };
            }
        };
    }
    
    public static <E> Iterable<E> collatedIterable(final Iterable<? extends E> a, final Iterable<? extends E> b) {
        checkNotNull(a, b);
        return new FluentIterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return IteratorUtils.collatedIterator((Comparator<? super E>)null, a.iterator(), b.iterator());
            }
        };
    }
    
    public static <E> Iterable<E> collatedIterable(final Comparator<? super E> comparator, final Iterable<? extends E> a, final Iterable<? extends E> b) {
        checkNotNull(a, b);
        return new FluentIterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return IteratorUtils.collatedIterator((Comparator<? super E>)comparator, a.iterator(), b.iterator());
            }
        };
    }
    
    public static <E> Iterable<E> filteredIterable(final Iterable<E> iterable, final Predicate<? super E> predicate) {
        checkNotNull(iterable);
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null.");
        }
        return new FluentIterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return IteratorUtils.filteredIterator((Iterator<? extends E>)emptyIteratorIfNull((Iterable<Object>)iterable), (Predicate<? super E>)predicate);
            }
        };
    }
    
    public static <E> Iterable<E> boundedIterable(final Iterable<E> iterable, final long maxSize) {
        checkNotNull(iterable);
        if (maxSize < 0L) {
            throw new IllegalArgumentException("MaxSize parameter must not be negative.");
        }
        return new FluentIterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return (Iterator<E>)IteratorUtils.boundedIterator(iterable.iterator(), maxSize);
            }
        };
    }
    
    public static <E> Iterable<E> loopingIterable(final Iterable<E> iterable) {
        checkNotNull(iterable);
        return new FluentIterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return new LazyIteratorChain<E>() {
                    @Override
                    protected Iterator<? extends E> nextIterator(final int count) {
                        if (IterableUtils.isEmpty(iterable)) {
                            return null;
                        }
                        return iterable.iterator();
                    }
                };
            }
        };
    }
    
    public static <E> Iterable<E> reversedIterable(final Iterable<E> iterable) {
        checkNotNull(iterable);
        return new FluentIterable<E>() {
            @Override
            public Iterator<E> iterator() {
                final List<E> list = (iterable instanceof List) ? ((List)iterable) : IteratorUtils.toList(iterable.iterator());
                return new ReverseListIterator<E>(list);
            }
        };
    }
    
    public static <E> Iterable<E> skippingIterable(final Iterable<E> iterable, final long elementsToSkip) {
        checkNotNull(iterable);
        if (elementsToSkip < 0L) {
            throw new IllegalArgumentException("ElementsToSkip parameter must not be negative.");
        }
        return new FluentIterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return (Iterator<E>)IteratorUtils.skippingIterator(iterable.iterator(), elementsToSkip);
            }
        };
    }
    
    public static <I, O> Iterable<O> transformedIterable(final Iterable<I> iterable, final Transformer<? super I, ? extends O> transformer) {
        checkNotNull(iterable);
        if (transformer == null) {
            throw new NullPointerException("Transformer must not be null.");
        }
        return new FluentIterable<O>() {
            @Override
            public Iterator<O> iterator() {
                return IteratorUtils.transformedIterator(iterable.iterator(), (Transformer<? super Object, ? extends O>)transformer);
            }
        };
    }
    
    public static <E> Iterable<E> uniqueIterable(final Iterable<E> iterable) {
        checkNotNull(iterable);
        return new FluentIterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return new UniqueFilterIterator<E>(iterable.iterator());
            }
        };
    }
    
    public static <E> Iterable<E> unmodifiableIterable(final Iterable<E> iterable) {
        checkNotNull(iterable);
        if (iterable instanceof UnmodifiableIterable) {
            return iterable;
        }
        return new UnmodifiableIterable<E>(iterable);
    }
    
    public static <E> Iterable<E> zippingIterable(final Iterable<? extends E> a, final Iterable<? extends E> b) {
        checkNotNull(a);
        checkNotNull(b);
        return new FluentIterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return (Iterator<E>)IteratorUtils.zippingIterator(a.iterator(), b.iterator());
            }
        };
    }
    
    public static <E> Iterable<E> zippingIterable(final Iterable<? extends E> first, final Iterable<? extends E>... others) {
        checkNotNull(first);
        checkNotNull((Iterable<?>[])others);
        return new FluentIterable<E>() {
            @Override
            public Iterator<E> iterator() {
                final Iterator<? extends E>[] iterators = new Iterator[others.length + 1];
                iterators[0] = first.iterator();
                for (int i = 0; i < others.length; ++i) {
                    iterators[i + 1] = others[i].iterator();
                }
                return (Iterator<E>)IteratorUtils.zippingIterator((Iterator<?>[])iterators);
            }
        };
    }
    
    public static <E> Iterable<E> emptyIfNull(final Iterable<E> iterable) {
        return (iterable == null) ? emptyIterable() : iterable;
    }
    
    public static <E> void forEach(final Iterable<E> iterable, final Closure<? super E> closure) {
        IteratorUtils.forEach((Iterator<Object>)emptyIteratorIfNull((Iterable<E>)iterable), (Closure<? super Object>)closure);
    }
    
    public static <E> E forEachButLast(final Iterable<E> iterable, final Closure<? super E> closure) {
        return IteratorUtils.forEachButLast((Iterator<E>)emptyIteratorIfNull((Iterable<E>)iterable), closure);
    }
    
    public static <E> E find(final Iterable<E> iterable, final Predicate<? super E> predicate) {
        return IteratorUtils.find((Iterator<E>)emptyIteratorIfNull((Iterable<E>)iterable), predicate);
    }
    
    public static <E> int indexOf(final Iterable<E> iterable, final Predicate<? super E> predicate) {
        return IteratorUtils.indexOf((Iterator<Object>)emptyIteratorIfNull((Iterable<E>)iterable), (Predicate<? super Object>)predicate);
    }
    
    public static <E> boolean matchesAll(final Iterable<E> iterable, final Predicate<? super E> predicate) {
        return IteratorUtils.matchesAll((Iterator<Object>)emptyIteratorIfNull((Iterable<E>)iterable), (Predicate<? super Object>)predicate);
    }
    
    public static <E> boolean matchesAny(final Iterable<E> iterable, final Predicate<? super E> predicate) {
        return IteratorUtils.matchesAny((Iterator<Object>)emptyIteratorIfNull((Iterable<E>)iterable), (Predicate<? super Object>)predicate);
    }
    
    public static <E> long countMatches(final Iterable<E> input, final Predicate<? super E> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null.");
        }
        return size(filteredIterable((Iterable<Object>)emptyIfNull((Iterable<E>)input), (Predicate<? super Object>)predicate));
    }
    
    public static boolean isEmpty(final Iterable<?> iterable) {
        if (iterable instanceof Collection) {
            return ((Collection)iterable).isEmpty();
        }
        return IteratorUtils.isEmpty(emptyIteratorIfNull(iterable));
    }
    
    public static <E> boolean contains(final Iterable<E> iterable, final Object object) {
        if (iterable instanceof Collection) {
            return ((Collection)iterable).contains(object);
        }
        return IteratorUtils.contains((Iterator<Object>)emptyIteratorIfNull((Iterable<E>)iterable), object);
    }
    
    public static <E> boolean contains(final Iterable<? extends E> iterable, final E object, final Equator<? super E> equator) {
        if (equator == null) {
            throw new NullPointerException("Equator must not be null.");
        }
        return matchesAny(iterable, (Predicate<? super E>)EqualPredicate.equalPredicate((Object)object, (Equator<? super E>)equator));
    }
    
    public static <E, T extends E> int frequency(final Iterable<E> iterable, final T obj) {
        if (iterable instanceof Set) {
            return ((Set)iterable).contains(obj) ? 1 : 0;
        }
        if (iterable instanceof Bag) {
            return ((Bag)iterable).getCount(obj);
        }
        return size(filteredIterable((Iterable<Object>)emptyIfNull((Iterable<E>)iterable), (Predicate<? super Object>)EqualPredicate.equalPredicate(obj)));
    }
    
    public static <T> T get(final Iterable<T> iterable, final int index) {
        CollectionUtils.checkIndexBounds(index);
        if (iterable instanceof List) {
            return ((List)iterable).get(index);
        }
        return IteratorUtils.get((Iterator<T>)emptyIteratorIfNull((Iterable<E>)iterable), index);
    }
    
    public static int size(final Iterable<?> iterable) {
        if (iterable instanceof Collection) {
            return ((Collection)iterable).size();
        }
        return IteratorUtils.size(emptyIteratorIfNull(iterable));
    }
    
    public static <O> List<List<O>> partition(final Iterable<? extends O> iterable, final Predicate<? super O> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null.");
        }
        final Factory<List<O>> factory = (Factory<List<O>>)FactoryUtils.instantiateFactory(ArrayList.class);
        final Predicate<? super O>[] predicates = { predicate };
        return partition((Iterable<?>)iterable, factory, (Predicate<? super Object>[])predicates);
    }
    
    public static <O> List<List<O>> partition(final Iterable<? extends O> iterable, final Predicate<? super O>... predicates) {
        final Factory<List<O>> factory = (Factory<List<O>>)FactoryUtils.instantiateFactory(ArrayList.class);
        return partition((Iterable<?>)iterable, factory, (Predicate<? super Object>[])predicates);
    }
    
    public static <O, R extends Collection<O>> List<R> partition(final Iterable<? extends O> iterable, final Factory<R> partitionFactory, final Predicate<? super O>... predicates) {
        if (iterable == null) {
            final Iterable<O> empty = emptyIterable();
            return (List<R>)partition((Iterable<?>)empty, (Factory<Collection>)partitionFactory, (Predicate<? super Object>[])predicates);
        }
        if (predicates == null) {
            throw new NullPointerException("Predicates must not be null.");
        }
        for (final Predicate<?> p : predicates) {
            if (p == null) {
                throw new NullPointerException("Predicate must not be null.");
            }
        }
        if (predicates.length < 1) {
            final R singlePartition = partitionFactory.create();
            CollectionUtils.addAll((Collection<Object>)singlePartition, iterable);
            return Collections.singletonList(singlePartition);
        }
        final int numberOfPredicates = predicates.length;
        final int numberOfPartitions = numberOfPredicates + 1;
        final List<R> partitions = new ArrayList<R>(numberOfPartitions);
        for (int i = 0; i < numberOfPartitions; ++i) {
            partitions.add(partitionFactory.create());
        }
        for (final O element : iterable) {
            boolean elementAssigned = false;
            for (int j = 0; j < numberOfPredicates; ++j) {
                if (predicates[j].evaluate(element)) {
                    partitions.get(j).add(element);
                    elementAssigned = true;
                    break;
                }
            }
            if (!elementAssigned) {
                partitions.get(numberOfPredicates).add(element);
            }
        }
        return partitions;
    }
    
    public static <E> List<E> toList(final Iterable<E> iterable) {
        return IteratorUtils.toList((Iterator<? extends E>)emptyIteratorIfNull((Iterable<? extends E>)iterable));
    }
    
    public static <E> String toString(final Iterable<E> iterable) {
        return IteratorUtils.toString((Iterator<Object>)emptyIteratorIfNull((Iterable<E>)iterable));
    }
    
    public static <E> String toString(final Iterable<E> iterable, final Transformer<? super E, String> transformer) {
        if (transformer == null) {
            throw new NullPointerException("Transformer must not be null.");
        }
        return IteratorUtils.toString((Iterator<Object>)emptyIteratorIfNull((Iterable<E>)iterable), (Transformer<? super Object, String>)transformer);
    }
    
    public static <E> String toString(final Iterable<E> iterable, final Transformer<? super E, String> transformer, final String delimiter, final String prefix, final String suffix) {
        return IteratorUtils.toString((Iterator<Object>)emptyIteratorIfNull((Iterable<E>)iterable), (Transformer<? super Object, String>)transformer, delimiter, prefix, suffix);
    }
    
    static void checkNotNull(final Iterable<?> iterable) {
        if (iterable == null) {
            throw new NullPointerException("Iterable must not be null.");
        }
    }
    
    static void checkNotNull(final Iterable<?>... iterables) {
        if (iterables == null) {
            throw new NullPointerException("Iterables must not be null.");
        }
        for (final Iterable<?> iterable : iterables) {
            checkNotNull(iterable);
        }
    }
    
    private static <E> Iterator<E> emptyIteratorIfNull(final Iterable<E> iterable) {
        return (Iterator<E>)((iterable != null) ? iterable.iterator() : IteratorUtils.emptyIterator());
    }
    
    static {
        EMPTY_ITERABLE = new FluentIterable<Object>() {
            @Override
            public Iterator<Object> iterator() {
                return IteratorUtils.emptyIterator();
            }
        };
    }
    
    private static final class UnmodifiableIterable<E> extends FluentIterable<E>
    {
        private final Iterable<E> unmodifiable;
        
        public UnmodifiableIterable(final Iterable<E> iterable) {
            this.unmodifiable = iterable;
        }
        
        @Override
        public Iterator<E> iterator() {
            return IteratorUtils.unmodifiableIterator(this.unmodifiable.iterator());
        }
    }
}
