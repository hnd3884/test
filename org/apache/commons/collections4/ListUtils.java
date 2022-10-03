package org.apache.commons.collections4;

import java.util.AbstractList;
import org.apache.commons.collections4.sequence.EditScript;
import org.apache.commons.collections4.sequence.CommandVisitor;
import org.apache.commons.collections4.sequence.SequencesComparator;
import org.apache.commons.collections4.functors.DefaultEquator;
import org.apache.commons.collections4.list.FixedSizeList;
import org.apache.commons.collections4.list.LazyList;
import org.apache.commons.collections4.list.TransformedList;
import org.apache.commons.collections4.list.PredicatedList;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.collections4.bag.HashBag;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListUtils
{
    private ListUtils() {
    }
    
    public static <T> List<T> emptyIfNull(final List<T> list) {
        return (list == null) ? Collections.emptyList() : list;
    }
    
    public static <T> List<T> defaultIfNull(final List<T> list, final List<T> defaultList) {
        return (list == null) ? defaultList : list;
    }
    
    public static <E> List<E> intersection(final List<? extends E> list1, final List<? extends E> list2) {
        final List<E> result = new ArrayList<E>();
        List<? extends E> smaller = list1;
        List<? extends E> larger = list2;
        if (list1.size() > list2.size()) {
            smaller = list2;
            larger = list1;
        }
        final HashSet<E> hashSet = new HashSet<E>(smaller);
        for (final E e : larger) {
            if (hashSet.contains(e)) {
                result.add(e);
                hashSet.remove(e);
            }
        }
        return result;
    }
    
    public static <E> List<E> subtract(final List<E> list1, final List<? extends E> list2) {
        final ArrayList<E> result = new ArrayList<E>();
        final HashBag<E> bag = new HashBag<E>(list2);
        for (final E e : list1) {
            if (!bag.remove(e, 1)) {
                result.add(e);
            }
        }
        return result;
    }
    
    public static <E> List<E> sum(final List<? extends E> list1, final List<? extends E> list2) {
        return (List<E>)subtract(union((List<?>)list1, (List<?>)list2), intersection((List<?>)list1, (List<?>)list2));
    }
    
    public static <E> List<E> union(final List<? extends E> list1, final List<? extends E> list2) {
        final ArrayList<E> result = new ArrayList<E>(list1);
        result.addAll(list2);
        return result;
    }
    
    public static <E> List<E> select(final Collection<? extends E> inputCollection, final Predicate<? super E> predicate) {
        return CollectionUtils.select((Iterable<?>)inputCollection, (Predicate<? super Object>)predicate, (ArrayList)new ArrayList(inputCollection.size()));
    }
    
    public static <E> List<E> selectRejected(final Collection<? extends E> inputCollection, final Predicate<? super E> predicate) {
        return CollectionUtils.selectRejected((Iterable<?>)inputCollection, (Predicate<? super Object>)predicate, (ArrayList)new ArrayList(inputCollection.size()));
    }
    
    public static boolean isEqualList(final Collection<?> list1, final Collection<?> list2) {
        if (list1 == list2) {
            return true;
        }
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }
        final Iterator<?> it1 = list1.iterator();
        final Iterator<?> it2 = list2.iterator();
        Object obj1 = null;
        Object obj2 = null;
        while (it1.hasNext() && it2.hasNext()) {
            obj1 = it1.next();
            obj2 = it2.next();
            if (obj1 == null) {
                if (obj2 == null) {
                    continue;
                }
            }
            else if (obj1.equals(obj2)) {
                continue;
            }
            return false;
        }
        return !it1.hasNext() && !it2.hasNext();
    }
    
    public static int hashCodeForList(final Collection<?> list) {
        if (list == null) {
            return 0;
        }
        int hashCode = 1;
        for (final Object obj : list) {
            hashCode = 31 * hashCode + ((obj == null) ? 0 : obj.hashCode());
        }
        return hashCode;
    }
    
    public static <E> List<E> retainAll(final Collection<E> collection, final Collection<?> retain) {
        final List<E> list = new ArrayList<E>(Math.min(collection.size(), retain.size()));
        for (final E obj : collection) {
            if (retain.contains(obj)) {
                list.add(obj);
            }
        }
        return list;
    }
    
    public static <E> List<E> removeAll(final Collection<E> collection, final Collection<?> remove) {
        final List<E> list = new ArrayList<E>();
        for (final E obj : collection) {
            if (!remove.contains(obj)) {
                list.add(obj);
            }
        }
        return list;
    }
    
    public static <E> List<E> synchronizedList(final List<E> list) {
        return Collections.synchronizedList(list);
    }
    
    public static <E> List<E> unmodifiableList(final List<? extends E> list) {
        return UnmodifiableList.unmodifiableList(list);
    }
    
    public static <E> List<E> predicatedList(final List<E> list, final Predicate<E> predicate) {
        return PredicatedList.predicatedList(list, predicate);
    }
    
    public static <E> List<E> transformedList(final List<E> list, final Transformer<? super E, ? extends E> transformer) {
        return TransformedList.transformingList(list, transformer);
    }
    
    public static <E> List<E> lazyList(final List<E> list, final Factory<? extends E> factory) {
        return LazyList.lazyList(list, factory);
    }
    
    public static <E> List<E> fixedSizeList(final List<E> list) {
        return FixedSizeList.fixedSizeList(list);
    }
    
    public static <E> int indexOf(final List<E> list, final Predicate<E> predicate) {
        if (list != null && predicate != null) {
            for (int i = 0; i < list.size(); ++i) {
                final E item = list.get(i);
                if (predicate.evaluate(item)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public static <E> List<E> longestCommonSubsequence(final List<E> a, final List<E> b) {
        return longestCommonSubsequence(a, b, DefaultEquator.defaultEquator());
    }
    
    public static <E> List<E> longestCommonSubsequence(final List<E> a, final List<E> b, final Equator<? super E> equator) {
        if (a == null || b == null) {
            throw new NullPointerException("List must not be null");
        }
        if (equator == null) {
            throw new NullPointerException("Equator must not be null");
        }
        final SequencesComparator<E> comparator = new SequencesComparator<E>(a, b, equator);
        final EditScript<E> script = comparator.getScript();
        final LcsVisitor<E> visitor = new LcsVisitor<E>();
        script.visit(visitor);
        return visitor.getSubSequence();
    }
    
    public static String longestCommonSubsequence(final CharSequence a, final CharSequence b) {
        if (a == null || b == null) {
            throw new NullPointerException("CharSequence must not be null");
        }
        final List<Character> lcs = longestCommonSubsequence(new CharSequenceAsList(a), (List<Character>)new CharSequenceAsList(b));
        final StringBuilder sb = new StringBuilder();
        for (final Character ch : lcs) {
            sb.append(ch);
        }
        return sb.toString();
    }
    
    public static <T> List<List<T>> partition(final List<T> list, final int size) {
        if (list == null) {
            throw new NullPointerException("List must not be null");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        return (List<List<T>>)new Partition((List)list, size);
    }
    
    private static final class LcsVisitor<E> implements CommandVisitor<E>
    {
        private ArrayList<E> sequence;
        
        public LcsVisitor() {
            this.sequence = new ArrayList<E>();
        }
        
        @Override
        public void visitInsertCommand(final E object) {
        }
        
        @Override
        public void visitDeleteCommand(final E object) {
        }
        
        @Override
        public void visitKeepCommand(final E object) {
            this.sequence.add(object);
        }
        
        public List<E> getSubSequence() {
            return this.sequence;
        }
    }
    
    private static final class CharSequenceAsList extends AbstractList<Character>
    {
        private final CharSequence sequence;
        
        public CharSequenceAsList(final CharSequence sequence) {
            this.sequence = sequence;
        }
        
        @Override
        public Character get(final int index) {
            return this.sequence.charAt(index);
        }
        
        @Override
        public int size() {
            return this.sequence.length();
        }
    }
    
    private static class Partition<T> extends AbstractList<List<T>>
    {
        private final List<T> list;
        private final int size;
        
        private Partition(final List<T> list, final int size) {
            this.list = list;
            this.size = size;
        }
        
        @Override
        public List<T> get(final int index) {
            final int listSize = this.size();
            if (listSize < 0) {
                throw new IllegalArgumentException("negative size: " + listSize);
            }
            if (index < 0) {
                throw new IndexOutOfBoundsException("Index " + index + " must not be negative");
            }
            if (index >= listSize) {
                throw new IndexOutOfBoundsException("Index " + index + " must be less than size " + listSize);
            }
            final int start = index * this.size;
            final int end = Math.min(start + this.size, this.list.size());
            return this.list.subList(start, end);
        }
        
        @Override
        public int size() {
            return (this.list.size() + this.size - 1) / this.size;
        }
        
        @Override
        public boolean isEmpty() {
            return this.list.isEmpty();
        }
    }
}
