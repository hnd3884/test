package org.apache.commons.collections;

import org.apache.commons.collections.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections.iterators.EmptyMapIterator;
import org.apache.commons.collections.iterators.EmptyOrderedIterator;
import org.apache.commons.collections.iterators.EmptyListIterator;
import org.apache.commons.collections.iterators.EmptyIterator;
import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.Map;
import java.util.ArrayList;
import java.lang.reflect.Array;
import org.apache.commons.collections.iterators.ListIteratorWrapper;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.commons.collections.iterators.EnumerationIterator;
import java.util.Enumeration;
import org.apache.commons.collections.iterators.LoopingListIterator;
import java.util.List;
import org.apache.commons.collections.iterators.LoopingIterator;
import org.apache.commons.collections.iterators.FilterListIterator;
import org.apache.commons.collections.iterators.FilterIterator;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.collections.iterators.ObjectGraphIterator;
import org.apache.commons.collections.iterators.CollatingIterator;
import java.util.Comparator;
import java.util.Collection;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.collections.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections.iterators.UnmodifiableListIterator;
import org.apache.commons.collections.iterators.UnmodifiableIterator;
import java.util.Iterator;
import org.apache.commons.collections.iterators.ArrayListIterator;
import org.apache.commons.collections.iterators.ObjectArrayListIterator;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.collections.iterators.ObjectArrayIterator;
import org.apache.commons.collections.iterators.SingletonListIterator;
import java.util.ListIterator;
import org.apache.commons.collections.iterators.SingletonIterator;

public class IteratorUtils
{
    public static final ResettableIterator EMPTY_ITERATOR;
    public static final ResettableListIterator EMPTY_LIST_ITERATOR;
    public static final OrderedIterator EMPTY_ORDERED_ITERATOR;
    public static final MapIterator EMPTY_MAP_ITERATOR;
    public static final OrderedMapIterator EMPTY_ORDERED_MAP_ITERATOR;
    
    public static ResettableIterator emptyIterator() {
        return IteratorUtils.EMPTY_ITERATOR;
    }
    
    public static ResettableListIterator emptyListIterator() {
        return IteratorUtils.EMPTY_LIST_ITERATOR;
    }
    
    public static OrderedIterator emptyOrderedIterator() {
        return IteratorUtils.EMPTY_ORDERED_ITERATOR;
    }
    
    public static MapIterator emptyMapIterator() {
        return IteratorUtils.EMPTY_MAP_ITERATOR;
    }
    
    public static OrderedMapIterator emptyOrderedMapIterator() {
        return IteratorUtils.EMPTY_ORDERED_MAP_ITERATOR;
    }
    
    public static ResettableIterator singletonIterator(final Object object) {
        return new SingletonIterator(object);
    }
    
    public static ListIterator singletonListIterator(final Object object) {
        return new SingletonListIterator(object);
    }
    
    public static ResettableIterator arrayIterator(final Object[] array) {
        return new ObjectArrayIterator(array);
    }
    
    public static ResettableIterator arrayIterator(final Object array) {
        return new ArrayIterator(array);
    }
    
    public static ResettableIterator arrayIterator(final Object[] array, final int start) {
        return new ObjectArrayIterator(array, start);
    }
    
    public static ResettableIterator arrayIterator(final Object array, final int start) {
        return new ArrayIterator(array, start);
    }
    
    public static ResettableIterator arrayIterator(final Object[] array, final int start, final int end) {
        return new ObjectArrayIterator(array, start, end);
    }
    
    public static ResettableIterator arrayIterator(final Object array, final int start, final int end) {
        return new ArrayIterator(array, start, end);
    }
    
    public static ResettableListIterator arrayListIterator(final Object[] array) {
        return new ObjectArrayListIterator(array);
    }
    
    public static ResettableListIterator arrayListIterator(final Object array) {
        return new ArrayListIterator(array);
    }
    
    public static ResettableListIterator arrayListIterator(final Object[] array, final int start) {
        return new ObjectArrayListIterator(array, start);
    }
    
    public static ResettableListIterator arrayListIterator(final Object array, final int start) {
        return new ArrayListIterator(array, start);
    }
    
    public static ResettableListIterator arrayListIterator(final Object[] array, final int start, final int end) {
        return new ObjectArrayListIterator(array, start, end);
    }
    
    public static ResettableListIterator arrayListIterator(final Object array, final int start, final int end) {
        return new ArrayListIterator(array, start, end);
    }
    
    public static Iterator unmodifiableIterator(final Iterator iterator) {
        return UnmodifiableIterator.decorate(iterator);
    }
    
    public static ListIterator unmodifiableListIterator(final ListIterator listIterator) {
        return UnmodifiableListIterator.decorate(listIterator);
    }
    
    public static MapIterator unmodifiableMapIterator(final MapIterator mapIterator) {
        return UnmodifiableMapIterator.decorate(mapIterator);
    }
    
    public static Iterator chainedIterator(final Iterator iterator1, final Iterator iterator2) {
        return new IteratorChain(iterator1, iterator2);
    }
    
    public static Iterator chainedIterator(final Iterator[] iterators) {
        return new IteratorChain(iterators);
    }
    
    public static Iterator chainedIterator(final Collection iterators) {
        return new IteratorChain(iterators);
    }
    
    public static Iterator collatedIterator(final Comparator comparator, final Iterator iterator1, final Iterator iterator2) {
        return new CollatingIterator(comparator, iterator1, iterator2);
    }
    
    public static Iterator collatedIterator(final Comparator comparator, final Iterator[] iterators) {
        return new CollatingIterator(comparator, iterators);
    }
    
    public static Iterator collatedIterator(final Comparator comparator, final Collection iterators) {
        return new CollatingIterator(comparator, iterators);
    }
    
    public static Iterator objectGraphIterator(final Object root, final Transformer transformer) {
        return new ObjectGraphIterator(root, transformer);
    }
    
    public static Iterator transformedIterator(final Iterator iterator, final Transformer transform) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (transform == null) {
            throw new NullPointerException("Transformer must not be null");
        }
        return new TransformIterator(iterator, transform);
    }
    
    public static Iterator filteredIterator(final Iterator iterator, final Predicate predicate) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new FilterIterator(iterator, predicate);
    }
    
    public static ListIterator filteredListIterator(final ListIterator listIterator, final Predicate predicate) {
        if (listIterator == null) {
            throw new NullPointerException("ListIterator must not be null");
        }
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new FilterListIterator(listIterator, predicate);
    }
    
    public static ResettableIterator loopingIterator(final Collection coll) {
        if (coll == null) {
            throw new NullPointerException("Collection must not be null");
        }
        return new LoopingIterator(coll);
    }
    
    public static ResettableListIterator loopingListIterator(final List list) {
        if (list == null) {
            throw new NullPointerException("List must not be null");
        }
        return new LoopingListIterator(list);
    }
    
    public static Iterator asIterator(final Enumeration enumeration) {
        if (enumeration == null) {
            throw new NullPointerException("Enumeration must not be null");
        }
        return new EnumerationIterator(enumeration);
    }
    
    public static Iterator asIterator(final Enumeration enumeration, final Collection removeCollection) {
        if (enumeration == null) {
            throw new NullPointerException("Enumeration must not be null");
        }
        if (removeCollection == null) {
            throw new NullPointerException("Collection must not be null");
        }
        return new EnumerationIterator(enumeration, removeCollection);
    }
    
    public static Enumeration asEnumeration(final Iterator iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new IteratorEnumeration(iterator);
    }
    
    public static ListIterator toListIterator(final Iterator iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new ListIteratorWrapper(iterator);
    }
    
    public static Object[] toArray(final Iterator iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        final List list = toList(iterator, 100);
        return list.toArray();
    }
    
    public static Object[] toArray(final Iterator iterator, final Class arrayClass) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (arrayClass == null) {
            throw new NullPointerException("Array class must not be null");
        }
        final List list = toList(iterator, 100);
        return list.toArray((Object[])Array.newInstance(arrayClass, list.size()));
    }
    
    public static List toList(final Iterator iterator) {
        return toList(iterator, 10);
    }
    
    public static List toList(final Iterator iterator, final int estimatedSize) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (estimatedSize < 1) {
            throw new IllegalArgumentException("Estimated size must be greater than 0");
        }
        final List list = new ArrayList(estimatedSize);
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }
    
    public static Iterator getIterator(final Object obj) {
        if (obj == null) {
            return emptyIterator();
        }
        if (obj instanceof Iterator) {
            return (Iterator)obj;
        }
        if (obj instanceof Collection) {
            return ((Collection)obj).iterator();
        }
        if (obj instanceof Object[]) {
            return new ObjectArrayIterator((Object[])obj);
        }
        if (obj instanceof Enumeration) {
            return new EnumerationIterator((Enumeration)obj);
        }
        if (obj instanceof Map) {
            return ((Map)obj).values().iterator();
        }
        if (obj instanceof Dictionary) {
            return new EnumerationIterator(((Dictionary)obj).elements());
        }
        if (obj != null && obj.getClass().isArray()) {
            return new ArrayIterator(obj);
        }
        try {
            final Method method = obj.getClass().getMethod("iterator", (Class<?>[])null);
            if (Iterator.class.isAssignableFrom(method.getReturnType())) {
                final Iterator it = (Iterator)method.invoke(obj, (Object[])null);
                if (it != null) {
                    return it;
                }
            }
        }
        catch (final Exception ex) {}
        return singletonIterator(obj);
    }
    
    static {
        EMPTY_ITERATOR = EmptyIterator.RESETTABLE_INSTANCE;
        EMPTY_LIST_ITERATOR = EmptyListIterator.RESETTABLE_INSTANCE;
        EMPTY_ORDERED_ITERATOR = EmptyOrderedIterator.INSTANCE;
        EMPTY_MAP_ITERATOR = EmptyMapIterator.INSTANCE;
        EMPTY_ORDERED_MAP_ITERATOR = EmptyOrderedMapIterator.INSTANCE;
    }
}
