package org.apache.commons.collections4;

import java.util.HashSet;
import java.util.AbstractSet;
import java.util.TreeSet;
import org.apache.commons.collections4.set.TransformedNavigableSet;
import org.apache.commons.collections4.set.PredicatedNavigableSet;
import org.apache.commons.collections4.set.UnmodifiableNavigableSet;
import java.util.NavigableSet;
import org.apache.commons.collections4.set.TransformedSortedSet;
import org.apache.commons.collections4.set.PredicatedSortedSet;
import org.apache.commons.collections4.set.UnmodifiableSortedSet;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.collections4.set.TransformedSet;
import org.apache.commons.collections4.set.PredicatedSet;
import org.apache.commons.collections4.set.UnmodifiableSet;
import java.util.Map;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;

public class SetUtils
{
    public static final SortedSet EMPTY_SORTED_SET;
    
    public static <E> Set<E> emptySet() {
        return Collections.emptySet();
    }
    
    public static <E> SortedSet<E> emptySortedSet() {
        return SetUtils.EMPTY_SORTED_SET;
    }
    
    private SetUtils() {
    }
    
    public static <T> Set<T> emptyIfNull(final Set<T> set) {
        return (set == null) ? Collections.emptySet() : set;
    }
    
    public static boolean isEqualSet(final Collection<?> set1, final Collection<?> set2) {
        return set1 == set2 || (set1 != null && set2 != null && set1.size() == set2.size() && set1.containsAll(set2));
    }
    
    public static <T> int hashCodeForSet(final Collection<T> set) {
        if (set == null) {
            return 0;
        }
        int hashCode = 0;
        for (final T obj : set) {
            if (obj != null) {
                hashCode += obj.hashCode();
            }
        }
        return hashCode;
    }
    
    public static <E> Set<E> newIdentityHashSet() {
        return Collections.newSetFromMap(new IdentityHashMap<E, Boolean>());
    }
    
    public static <E> Set<E> synchronizedSet(final Set<E> set) {
        return Collections.synchronizedSet(set);
    }
    
    public static <E> Set<E> unmodifiableSet(final Set<? extends E> set) {
        return UnmodifiableSet.unmodifiableSet(set);
    }
    
    public static <E> Set<E> predicatedSet(final Set<E> set, final Predicate<? super E> predicate) {
        return PredicatedSet.predicatedSet(set, predicate);
    }
    
    public static <E> Set<E> transformedSet(final Set<E> set, final Transformer<? super E, ? extends E> transformer) {
        return TransformedSet.transformingSet(set, transformer);
    }
    
    public static <E> Set<E> orderedSet(final Set<E> set) {
        return ListOrderedSet.listOrderedSet(set);
    }
    
    public static <E> SortedSet<E> synchronizedSortedSet(final SortedSet<E> set) {
        return Collections.synchronizedSortedSet(set);
    }
    
    public static <E> SortedSet<E> unmodifiableSortedSet(final SortedSet<E> set) {
        return UnmodifiableSortedSet.unmodifiableSortedSet(set);
    }
    
    public static <E> SortedSet<E> predicatedSortedSet(final SortedSet<E> set, final Predicate<? super E> predicate) {
        return PredicatedSortedSet.predicatedSortedSet(set, predicate);
    }
    
    public static <E> SortedSet<E> transformedSortedSet(final SortedSet<E> set, final Transformer<? super E, ? extends E> transformer) {
        return TransformedSortedSet.transformingSortedSet(set, transformer);
    }
    
    public static <E> SortedSet<E> unmodifiableNavigableSet(final NavigableSet<E> set) {
        return UnmodifiableNavigableSet.unmodifiableNavigableSet(set);
    }
    
    public static <E> SortedSet<E> predicatedNavigableSet(final NavigableSet<E> set, final Predicate<? super E> predicate) {
        return PredicatedNavigableSet.predicatedNavigableSet(set, predicate);
    }
    
    public static <E> SortedSet<E> transformedNavigableSet(final NavigableSet<E> set, final Transformer<? super E, ? extends E> transformer) {
        return TransformedNavigableSet.transformingNavigableSet(set, transformer);
    }
    
    public static <E> SetView<E> union(final Set<? extends E> a, final Set<? extends E> b) {
        if (a == null || b == null) {
            throw new NullPointerException("Sets must not be null.");
        }
        final SetView<E> bMinusA = (SetView<E>)difference((Set<?>)b, (Set<?>)a);
        return new SetView<E>() {
            @Override
            public boolean contains(final Object o) {
                return a.contains(o) || b.contains(o);
            }
            
            public Iterator<E> createIterator() {
                return IteratorUtils.chainedIterator(a.iterator(), bMinusA.iterator());
            }
            
            @Override
            public boolean isEmpty() {
                return a.isEmpty() && b.isEmpty();
            }
            
            @Override
            public int size() {
                return a.size() + bMinusA.size();
            }
        };
    }
    
    public static <E> SetView<E> difference(final Set<? extends E> a, final Set<? extends E> b) {
        if (a == null || b == null) {
            throw new NullPointerException("Sets must not be null.");
        }
        final Predicate<E> notContainedInB = new Predicate<E>() {
            @Override
            public boolean evaluate(final E object) {
                return !b.contains(object);
            }
        };
        return new SetView<E>() {
            @Override
            public boolean contains(final Object o) {
                return a.contains(o) && !b.contains(o);
            }
            
            public Iterator<E> createIterator() {
                return IteratorUtils.filteredIterator(a.iterator(), (Predicate<? super E>)notContainedInB);
            }
        };
    }
    
    public static <E> SetView<E> intersection(final Set<? extends E> a, final Set<? extends E> b) {
        if (a == null || b == null) {
            throw new NullPointerException("Sets must not be null.");
        }
        final Predicate<E> containedInB = new Predicate<E>() {
            @Override
            public boolean evaluate(final E object) {
                return b.contains(object);
            }
        };
        return new SetView<E>() {
            @Override
            public boolean contains(final Object o) {
                return a.contains(o) && b.contains(o);
            }
            
            public Iterator<E> createIterator() {
                return IteratorUtils.filteredIterator(a.iterator(), (Predicate<? super E>)containedInB);
            }
        };
    }
    
    public static <E> SetView<E> disjunction(final Set<? extends E> a, final Set<? extends E> b) {
        if (a == null || b == null) {
            throw new NullPointerException("Sets must not be null.");
        }
        final SetView<E> aMinusB = (SetView<E>)difference((Set<?>)a, (Set<?>)b);
        final SetView<E> bMinusA = (SetView<E>)difference((Set<?>)b, (Set<?>)a);
        return new SetView<E>() {
            @Override
            public boolean contains(final Object o) {
                return a.contains(o) ^ b.contains(o);
            }
            
            public Iterator<E> createIterator() {
                return IteratorUtils.chainedIterator(aMinusB.iterator(), bMinusA.iterator());
            }
            
            @Override
            public boolean isEmpty() {
                return aMinusB.isEmpty() && bMinusA.isEmpty();
            }
            
            @Override
            public int size() {
                return aMinusB.size() + bMinusA.size();
            }
        };
    }
    
    static {
        EMPTY_SORTED_SET = UnmodifiableSortedSet.unmodifiableSortedSet(new TreeSet<Object>());
    }
    
    public abstract static class SetView<E> extends AbstractSet<E>
    {
        @Override
        public Iterator<E> iterator() {
            return IteratorUtils.unmodifiableIterator(this.createIterator());
        }
        
        protected abstract Iterator<E> createIterator();
        
        @Override
        public int size() {
            return IteratorUtils.size(this.iterator());
        }
        
        public <S extends Set<E>> void copyInto(final S set) {
            CollectionUtils.addAll((Collection<Object>)set, this);
        }
        
        public Set<E> toSet() {
            final Set<E> set = new HashSet<E>(this.size());
            this.copyInto(set);
            return set;
        }
    }
}
