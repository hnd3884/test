package org.apache.commons.collections;

import java.util.TreeSet;
import java.util.Collections;
import org.apache.commons.collections.set.TransformedSortedSet;
import org.apache.commons.collections.set.TypedSortedSet;
import org.apache.commons.collections.set.PredicatedSortedSet;
import org.apache.commons.collections.set.UnmodifiableSortedSet;
import org.apache.commons.collections.set.SynchronizedSortedSet;
import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.collections.set.TransformedSet;
import org.apache.commons.collections.set.TypedSet;
import org.apache.commons.collections.set.PredicatedSet;
import org.apache.commons.collections.set.UnmodifiableSet;
import org.apache.commons.collections.set.SynchronizedSet;
import java.util.Iterator;
import java.util.Collection;
import java.util.SortedSet;
import java.util.Set;

public class SetUtils
{
    public static final Set EMPTY_SET;
    public static final SortedSet EMPTY_SORTED_SET;
    
    public static boolean isEqualSet(final Collection set1, final Collection set2) {
        return set1 == set2 || (set1 != null && set2 != null && set1.size() == set2.size() && set1.containsAll(set2));
    }
    
    public static int hashCodeForSet(final Collection set) {
        if (set == null) {
            return 0;
        }
        int hashCode = 0;
        final Iterator it = set.iterator();
        Object obj = null;
        while (it.hasNext()) {
            obj = it.next();
            if (obj != null) {
                hashCode += obj.hashCode();
            }
        }
        return hashCode;
    }
    
    public static Set synchronizedSet(final Set set) {
        return SynchronizedSet.decorate(set);
    }
    
    public static Set unmodifiableSet(final Set set) {
        return UnmodifiableSet.decorate(set);
    }
    
    public static Set predicatedSet(final Set set, final Predicate predicate) {
        return PredicatedSet.decorate(set, predicate);
    }
    
    public static Set typedSet(final Set set, final Class type) {
        return TypedSet.decorate(set, type);
    }
    
    public static Set transformedSet(final Set set, final Transformer transformer) {
        return TransformedSet.decorate(set, transformer);
    }
    
    public static Set orderedSet(final Set set) {
        return ListOrderedSet.decorate(set);
    }
    
    public static SortedSet synchronizedSortedSet(final SortedSet set) {
        return SynchronizedSortedSet.decorate(set);
    }
    
    public static SortedSet unmodifiableSortedSet(final SortedSet set) {
        return UnmodifiableSortedSet.decorate(set);
    }
    
    public static SortedSet predicatedSortedSet(final SortedSet set, final Predicate predicate) {
        return PredicatedSortedSet.decorate(set, predicate);
    }
    
    public static SortedSet typedSortedSet(final SortedSet set, final Class type) {
        return TypedSortedSet.decorate(set, type);
    }
    
    public static SortedSet transformedSortedSet(final SortedSet set, final Transformer transformer) {
        return TransformedSortedSet.decorate(set, transformer);
    }
    
    static {
        EMPTY_SET = Collections.EMPTY_SET;
        EMPTY_SORTED_SET = UnmodifiableSortedSet.decorate(new TreeSet());
    }
}
