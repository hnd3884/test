package org.glassfish.jersey.internal.guava;

import java.util.Collections;
import java.util.SortedSet;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.NavigableSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

public final class Sets
{
    private Sets() {
    }
    
    public static <E> HashSet<E> newHashSet() {
        return new HashSet<E>();
    }
    
    public static <E> HashSet<E> newHashSetWithExpectedSize(final int expectedSize) {
        return new HashSet<E>(Maps.capacity(expectedSize));
    }
    
    static int hashCodeImpl(final Set<?> s) {
        int hashCode = 0;
        for (final Object o : s) {
            hashCode += ((o != null) ? o.hashCode() : 0);
            hashCode = ~(~hashCode);
        }
        return hashCode;
    }
    
    static boolean equalsImpl(final Set<?> s, final Object object) {
        if (s == object) {
            return true;
        }
        if (object instanceof Set) {
            final Set<?> o = (Set<?>)object;
            try {
                return s.size() == o.size() && s.containsAll(o);
            }
            catch (final NullPointerException ignored) {
                return false;
            }
            catch (final ClassCastException ignored2) {
                return false;
            }
        }
        return false;
    }
    
    public static <E> NavigableSet<E> unmodifiableNavigableSet(final NavigableSet<E> set) {
        return new UnmodifiableNavigableSet<E>(set);
    }
    
    static boolean removeAllImpl(final Set<?> set, final Iterator<?> iterator) {
        boolean changed = false;
        while (iterator.hasNext()) {
            changed |= set.remove(iterator.next());
        }
        return changed;
    }
    
    static boolean removeAllImpl(final Set<?> set, final Collection<?> collection) {
        Preconditions.checkNotNull(collection);
        if (collection instanceof Set && collection.size() > set.size()) {
            return Iterators.removeAll(set.iterator(), collection);
        }
        return removeAllImpl(set, collection.iterator());
    }
    
    abstract static class ImprovedAbstractSet<E> extends AbstractSet<E>
    {
        @Override
        public boolean removeAll(final Collection<?> c) {
            return Sets.removeAllImpl(this, c);
        }
        
        @Override
        public boolean retainAll(final Collection<?> c) {
            return super.retainAll(Preconditions.checkNotNull(c));
        }
    }
    
    static final class UnmodifiableNavigableSet<E> extends ForwardingSortedSet<E> implements NavigableSet<E>, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final NavigableSet<E> delegate;
        private transient UnmodifiableNavigableSet<E> descendingSet;
        
        UnmodifiableNavigableSet(final NavigableSet<E> delegate) {
            this.delegate = Preconditions.checkNotNull(delegate);
        }
        
        @Override
        protected SortedSet<E> delegate() {
            return Collections.unmodifiableSortedSet(this.delegate);
        }
        
        @Override
        public E lower(final E e) {
            return this.delegate.lower(e);
        }
        
        @Override
        public E floor(final E e) {
            return this.delegate.floor(e);
        }
        
        @Override
        public E ceiling(final E e) {
            return this.delegate.ceiling(e);
        }
        
        @Override
        public E higher(final E e) {
            return this.delegate.higher(e);
        }
        
        @Override
        public E pollFirst() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public E pollLast() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public NavigableSet<E> descendingSet() {
            UnmodifiableNavigableSet<E> result = this.descendingSet;
            if (result == null) {
                final UnmodifiableNavigableSet descendingSet = new UnmodifiableNavigableSet((NavigableSet<Object>)this.delegate.descendingSet());
                this.descendingSet = descendingSet;
                result = descendingSet;
                result.descendingSet = this;
            }
            return result;
        }
        
        @Override
        public Iterator<E> descendingIterator() {
            return Iterators.unmodifiableIterator(this.delegate.descendingIterator());
        }
        
        @Override
        public NavigableSet<E> subSet(final E fromElement, final boolean fromInclusive, final E toElement, final boolean toInclusive) {
            return Sets.unmodifiableNavigableSet(this.delegate.subSet(fromElement, fromInclusive, toElement, toInclusive));
        }
        
        @Override
        public NavigableSet<E> headSet(final E toElement, final boolean inclusive) {
            return Sets.unmodifiableNavigableSet(this.delegate.headSet(toElement, inclusive));
        }
        
        @Override
        public NavigableSet<E> tailSet(final E fromElement, final boolean inclusive) {
            return Sets.unmodifiableNavigableSet(this.delegate.tailSet(fromElement, inclusive));
        }
    }
}
