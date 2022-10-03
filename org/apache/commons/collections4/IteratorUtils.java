package org.apache.commons.collections4;

import org.apache.commons.collections4.functors.EqualPredicate;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Dictionary;
import java.util.Map;
import java.util.ArrayList;
import java.lang.reflect.Array;
import org.apache.commons.collections4.iterators.ListIteratorWrapper;
import org.apache.commons.collections4.iterators.IteratorIterable;
import org.apache.commons.collections4.iterators.IteratorEnumeration;
import org.apache.commons.collections4.iterators.EnumerationIterator;
import java.util.Enumeration;
import org.apache.commons.collections4.iterators.ZippingIterator;
import org.apache.commons.collections4.iterators.SkippingIterator;
import org.apache.commons.collections4.iterators.PushbackIterator;
import org.apache.commons.collections4.iterators.PeekingIterator;
import org.w3c.dom.Node;
import org.apache.commons.collections4.iterators.NodeListIterator;
import org.w3c.dom.NodeList;
import org.apache.commons.collections4.iterators.LoopingListIterator;
import java.util.List;
import org.apache.commons.collections4.iterators.LoopingIterator;
import org.apache.commons.collections4.iterators.FilterListIterator;
import org.apache.commons.collections4.iterators.FilterIterator;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.collections4.iterators.ObjectGraphIterator;
import org.apache.commons.collections4.iterators.CollatingIterator;
import java.util.Comparator;
import java.util.Collection;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections4.iterators.UnmodifiableListIterator;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import org.apache.commons.collections4.iterators.BoundedIterator;
import java.util.Iterator;
import org.apache.commons.collections4.iterators.ArrayListIterator;
import org.apache.commons.collections4.iterators.ObjectArrayListIterator;
import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.collections4.iterators.SingletonListIterator;
import java.util.ListIterator;
import org.apache.commons.collections4.iterators.SingletonIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections4.iterators.EmptyMapIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedIterator;
import org.apache.commons.collections4.iterators.EmptyListIterator;
import org.apache.commons.collections4.iterators.EmptyIterator;

public class IteratorUtils
{
    public static final ResettableIterator EMPTY_ITERATOR;
    public static final ResettableListIterator EMPTY_LIST_ITERATOR;
    public static final OrderedIterator EMPTY_ORDERED_ITERATOR;
    public static final MapIterator EMPTY_MAP_ITERATOR;
    public static final OrderedMapIterator EMPTY_ORDERED_MAP_ITERATOR;
    private static final String DEFAULT_TOSTRING_PREFIX = "[";
    private static final String DEFAULT_TOSTRING_SUFFIX = "]";
    private static final String DEFAULT_TOSTRING_DELIMITER = ", ";
    
    private IteratorUtils() {
    }
    
    public static <E> ResettableIterator<E> emptyIterator() {
        return EmptyIterator.resettableEmptyIterator();
    }
    
    public static <E> ResettableListIterator<E> emptyListIterator() {
        return EmptyListIterator.resettableEmptyListIterator();
    }
    
    public static <E> OrderedIterator<E> emptyOrderedIterator() {
        return EmptyOrderedIterator.emptyOrderedIterator();
    }
    
    public static <K, V> MapIterator<K, V> emptyMapIterator() {
        return EmptyMapIterator.emptyMapIterator();
    }
    
    public static <K, V> OrderedMapIterator<K, V> emptyOrderedMapIterator() {
        return EmptyOrderedMapIterator.emptyOrderedMapIterator();
    }
    
    public static <E> ResettableIterator<E> singletonIterator(final E object) {
        return new SingletonIterator<E>(object);
    }
    
    public static <E> ListIterator<E> singletonListIterator(final E object) {
        return new SingletonListIterator<E>(object);
    }
    
    public static <E> ResettableIterator<E> arrayIterator(final E... array) {
        return new ObjectArrayIterator<E>(array);
    }
    
    public static <E> ResettableIterator<E> arrayIterator(final Object array) {
        return new ArrayIterator<E>(array);
    }
    
    public static <E> ResettableIterator<E> arrayIterator(final E[] array, final int start) {
        return new ObjectArrayIterator<E>(array, start);
    }
    
    public static <E> ResettableIterator<E> arrayIterator(final Object array, final int start) {
        return new ArrayIterator<E>(array, start);
    }
    
    public static <E> ResettableIterator<E> arrayIterator(final E[] array, final int start, final int end) {
        return new ObjectArrayIterator<E>(array, start, end);
    }
    
    public static <E> ResettableIterator<E> arrayIterator(final Object array, final int start, final int end) {
        return new ArrayIterator<E>(array, start, end);
    }
    
    public static <E> ResettableListIterator<E> arrayListIterator(final E... array) {
        return new ObjectArrayListIterator<E>(array);
    }
    
    public static <E> ResettableListIterator<E> arrayListIterator(final Object array) {
        return new ArrayListIterator<E>(array);
    }
    
    public static <E> ResettableListIterator<E> arrayListIterator(final E[] array, final int start) {
        return new ObjectArrayListIterator<E>(array, start);
    }
    
    public static <E> ResettableListIterator<E> arrayListIterator(final Object array, final int start) {
        return new ArrayListIterator<E>(array, start);
    }
    
    public static <E> ResettableListIterator<E> arrayListIterator(final E[] array, final int start, final int end) {
        return new ObjectArrayListIterator<E>(array, start, end);
    }
    
    public static <E> ResettableListIterator<E> arrayListIterator(final Object array, final int start, final int end) {
        return new ArrayListIterator<E>(array, start, end);
    }
    
    public static <E> BoundedIterator<E> boundedIterator(final Iterator<? extends E> iterator, final long max) {
        return boundedIterator(iterator, 0L, max);
    }
    
    public static <E> BoundedIterator<E> boundedIterator(final Iterator<? extends E> iterator, final long offset, final long max) {
        return new BoundedIterator<E>(iterator, offset, max);
    }
    
    public static <E> Iterator<E> unmodifiableIterator(final Iterator<E> iterator) {
        return UnmodifiableIterator.unmodifiableIterator((Iterator<? extends E>)iterator);
    }
    
    public static <E> ListIterator<E> unmodifiableListIterator(final ListIterator<E> listIterator) {
        return UnmodifiableListIterator.umodifiableListIterator((ListIterator<? extends E>)listIterator);
    }
    
    public static <K, V> MapIterator<K, V> unmodifiableMapIterator(final MapIterator<K, V> mapIterator) {
        return UnmodifiableMapIterator.unmodifiableMapIterator((MapIterator<? extends K, ? extends V>)mapIterator);
    }
    
    public static <E> Iterator<E> chainedIterator(final Iterator<? extends E> iterator1, final Iterator<? extends E> iterator2) {
        return new IteratorChain<E>(iterator1, iterator2);
    }
    
    public static <E> Iterator<E> chainedIterator(final Iterator<? extends E>... iterators) {
        return new IteratorChain<E>(iterators);
    }
    
    public static <E> Iterator<E> chainedIterator(final Collection<Iterator<? extends E>> iterators) {
        return new IteratorChain<E>(iterators);
    }
    
    public static <E> Iterator<E> collatedIterator(final Comparator<? super E> comparator, final Iterator<? extends E> iterator1, final Iterator<? extends E> iterator2) {
        final Comparator<E> comp = (Comparator<E>)((comparator == null) ? ComparatorUtils.NATURAL_COMPARATOR : comparator);
        return new CollatingIterator<E>(comp, iterator1, iterator2);
    }
    
    public static <E> Iterator<E> collatedIterator(final Comparator<? super E> comparator, final Iterator<? extends E>... iterators) {
        final Comparator<E> comp = (Comparator<E>)((comparator == null) ? ComparatorUtils.NATURAL_COMPARATOR : comparator);
        return new CollatingIterator<E>(comp, iterators);
    }
    
    public static <E> Iterator<E> collatedIterator(final Comparator<? super E> comparator, final Collection<Iterator<? extends E>> iterators) {
        final Comparator<E> comp = (Comparator<E>)((comparator == null) ? ComparatorUtils.NATURAL_COMPARATOR : comparator);
        return new CollatingIterator<E>(comp, iterators);
    }
    
    public static <E> Iterator<E> objectGraphIterator(final E root, final Transformer<? super E, ? extends E> transformer) {
        return new ObjectGraphIterator<E>(root, transformer);
    }
    
    public static <I, O> Iterator<O> transformedIterator(final Iterator<? extends I> iterator, final Transformer<? super I, ? extends O> transform) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (transform == null) {
            throw new NullPointerException("Transformer must not be null");
        }
        return new TransformIterator<Object, O>(iterator, transform);
    }
    
    public static <E> Iterator<E> filteredIterator(final Iterator<? extends E> iterator, final Predicate<? super E> predicate) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new FilterIterator<E>(iterator, predicate);
    }
    
    public static <E> ListIterator<E> filteredListIterator(final ListIterator<? extends E> listIterator, final Predicate<? super E> predicate) {
        if (listIterator == null) {
            throw new NullPointerException("ListIterator must not be null");
        }
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new FilterListIterator<E>(listIterator, predicate);
    }
    
    public static <E> ResettableIterator<E> loopingIterator(final Collection<? extends E> coll) {
        if (coll == null) {
            throw new NullPointerException("Collection must not be null");
        }
        return new LoopingIterator<E>(coll);
    }
    
    public static <E> ResettableListIterator<E> loopingListIterator(final List<E> list) {
        if (list == null) {
            throw new NullPointerException("List must not be null");
        }
        return new LoopingListIterator<E>(list);
    }
    
    public static NodeListIterator nodeListIterator(final NodeList nodeList) {
        if (nodeList == null) {
            throw new NullPointerException("NodeList must not be null");
        }
        return new NodeListIterator(nodeList);
    }
    
    public static NodeListIterator nodeListIterator(final Node node) {
        if (node == null) {
            throw new NullPointerException("Node must not be null");
        }
        return new NodeListIterator(node);
    }
    
    public static <E> Iterator<E> peekingIterator(final Iterator<? extends E> iterator) {
        return (Iterator<E>)PeekingIterator.peekingIterator((Iterator<?>)iterator);
    }
    
    public static <E> Iterator<E> pushbackIterator(final Iterator<? extends E> iterator) {
        return (Iterator<E>)PushbackIterator.pushbackIterator((Iterator<?>)iterator);
    }
    
    public static <E> SkippingIterator<E> skippingIterator(final Iterator<E> iterator, final long offset) {
        return new SkippingIterator<E>(iterator, offset);
    }
    
    public static <E> ZippingIterator<E> zippingIterator(final Iterator<? extends E> a, final Iterator<? extends E> b) {
        return new ZippingIterator<E>(a, b);
    }
    
    public static <E> ZippingIterator<E> zippingIterator(final Iterator<? extends E> a, final Iterator<? extends E> b, final Iterator<? extends E> c) {
        return new ZippingIterator<E>(a, b, c);
    }
    
    public static <E> ZippingIterator<E> zippingIterator(final Iterator<? extends E>... iterators) {
        return new ZippingIterator<E>(iterators);
    }
    
    public static <E> Iterator<E> asIterator(final Enumeration<? extends E> enumeration) {
        if (enumeration == null) {
            throw new NullPointerException("Enumeration must not be null");
        }
        return new EnumerationIterator<E>(enumeration);
    }
    
    public static <E> Iterator<E> asIterator(final Enumeration<? extends E> enumeration, final Collection<? super E> removeCollection) {
        if (enumeration == null) {
            throw new NullPointerException("Enumeration must not be null");
        }
        if (removeCollection == null) {
            throw new NullPointerException("Collection must not be null");
        }
        return new EnumerationIterator<E>(enumeration, removeCollection);
    }
    
    public static <E> Enumeration<E> asEnumeration(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new IteratorEnumeration<E>(iterator);
    }
    
    public static <E> Iterable<E> asIterable(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new IteratorIterable<E>(iterator, false);
    }
    
    public static <E> Iterable<E> asMultipleUseIterable(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new IteratorIterable<E>(iterator, true);
    }
    
    public static <E> ListIterator<E> toListIterator(final Iterator<? extends E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new ListIteratorWrapper<E>(iterator);
    }
    
    public static Object[] toArray(final Iterator<?> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        final List<?> list = toList(iterator, 100);
        return list.toArray();
    }
    
    public static <E> E[] toArray(final Iterator<? extends E> iterator, final Class<E> arrayClass) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (arrayClass == null) {
            throw new NullPointerException("Array class must not be null");
        }
        final List<E> list = toList(iterator, 100);
        final E[] array = (E[])Array.newInstance(arrayClass, list.size());
        return list.toArray(array);
    }
    
    public static <E> List<E> toList(final Iterator<? extends E> iterator) {
        return toList(iterator, 10);
    }
    
    public static <E> List<E> toList(final Iterator<? extends E> iterator, final int estimatedSize) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (estimatedSize < 1) {
            throw new IllegalArgumentException("Estimated size must be greater than 0");
        }
        final List<E> list = new ArrayList<E>(estimatedSize);
        while (iterator.hasNext()) {
            list.add((E)iterator.next());
        }
        return list;
    }
    
    public static Iterator<?> getIterator(final Object obj) {
        if (obj == null) {
            return emptyIterator();
        }
        if (obj instanceof Iterator) {
            return (Iterator)obj;
        }
        if (obj instanceof Iterable) {
            return ((Iterable)obj).iterator();
        }
        if (obj instanceof Object[]) {
            return new ObjectArrayIterator<Object>((Object[])obj);
        }
        if (obj instanceof Enumeration) {
            return new EnumerationIterator<Object>((Enumeration<?>)obj);
        }
        if (obj instanceof Map) {
            return ((Map)obj).values().iterator();
        }
        if (obj instanceof NodeList) {
            return new NodeListIterator((NodeList)obj);
        }
        if (obj instanceof Node) {
            return new NodeListIterator((Node)obj);
        }
        if (obj instanceof Dictionary) {
            return new EnumerationIterator<Object>(((Dictionary)obj).elements());
        }
        if (obj.getClass().isArray()) {
            return new ArrayIterator<Object>(obj);
        }
        try {
            final Method method = obj.getClass().getMethod("iterator", (Class<?>[])null);
            if (Iterator.class.isAssignableFrom(method.getReturnType())) {
                final Iterator<?> it = (Iterator<?>)method.invoke(obj, (Object[])null);
                if (it != null) {
                    return it;
                }
            }
        }
        catch (final RuntimeException e) {}
        catch (final NoSuchMethodException e2) {}
        catch (final IllegalAccessException e3) {}
        catch (final InvocationTargetException ex) {}
        return singletonIterator(obj);
    }
    
    public static <E> void forEach(final Iterator<E> iterator, final Closure<? super E> closure) {
        if (closure == null) {
            throw new NullPointerException("Closure must not be null");
        }
        if (iterator != null) {
            while (iterator.hasNext()) {
                final E element = iterator.next();
                closure.execute(element);
            }
        }
    }
    
    public static <E> E forEachButLast(final Iterator<E> iterator, final Closure<? super E> closure) {
        if (closure == null) {
            throw new NullPointerException("Closure must not be null.");
        }
        if (iterator != null) {
            while (iterator.hasNext()) {
                final E element = iterator.next();
                if (!iterator.hasNext()) {
                    return element;
                }
                closure.execute(element);
            }
        }
        return null;
    }
    
    public static <E> E find(final Iterator<E> iterator, final Predicate<? super E> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        if (iterator != null) {
            while (iterator.hasNext()) {
                final E element = iterator.next();
                if (predicate.evaluate(element)) {
                    return element;
                }
            }
        }
        return null;
    }
    
    public static <E> int indexOf(final Iterator<E> iterator, final Predicate<? super E> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        if (iterator != null) {
            int index = 0;
            while (iterator.hasNext()) {
                final E element = iterator.next();
                if (predicate.evaluate(element)) {
                    return index;
                }
                ++index;
            }
        }
        return -1;
    }
    
    public static <E> boolean matchesAny(final Iterator<E> iterator, final Predicate<? super E> predicate) {
        return indexOf(iterator, predicate) != -1;
    }
    
    public static <E> boolean matchesAll(final Iterator<E> iterator, final Predicate<? super E> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        if (iterator != null) {
            while (iterator.hasNext()) {
                final E element = iterator.next();
                if (!predicate.evaluate(element)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean isEmpty(final Iterator<?> iterator) {
        return iterator == null || !iterator.hasNext();
    }
    
    public static <E> boolean contains(final Iterator<E> iterator, final Object object) {
        return matchesAny(iterator, (Predicate<? super E>)EqualPredicate.equalPredicate(object));
    }
    
    public static <E> E get(final Iterator<E> iterator, final int index) {
        int i = index;
        CollectionUtils.checkIndexBounds(i);
        while (iterator.hasNext()) {
            if (--i == -1) {
                return iterator.next();
            }
            iterator.next();
        }
        throw new IndexOutOfBoundsException("Entry does not exist: " + i);
    }
    
    public static int size(final Iterator<?> iterator) {
        int size = 0;
        if (iterator != null) {
            while (iterator.hasNext()) {
                iterator.next();
                ++size;
            }
        }
        return size;
    }
    
    public static <E> String toString(final Iterator<E> iterator) {
        return toString(iterator, TransformerUtils.stringValueTransformer(), ", ", "[", "]");
    }
    
    public static <E> String toString(final Iterator<E> iterator, final Transformer<? super E, String> transformer) {
        return toString(iterator, transformer, ", ", "[", "]");
    }
    
    public static <E> String toString(final Iterator<E> iterator, final Transformer<? super E, String> transformer, final String delimiter, final String prefix, final String suffix) {
        if (transformer == null) {
            throw new NullPointerException("transformer may not be null");
        }
        if (delimiter == null) {
            throw new NullPointerException("delimiter may not be null");
        }
        if (prefix == null) {
            throw new NullPointerException("prefix may not be null");
        }
        if (suffix == null) {
            throw new NullPointerException("suffix may not be null");
        }
        final StringBuilder stringBuilder = new StringBuilder(prefix);
        if (iterator != null) {
            while (iterator.hasNext()) {
                final E element = iterator.next();
                stringBuilder.append(transformer.transform(element));
                stringBuilder.append(delimiter);
            }
            if (stringBuilder.length() > prefix.length()) {
                stringBuilder.setLength(stringBuilder.length() - delimiter.length());
            }
        }
        stringBuilder.append(suffix);
        return stringBuilder.toString();
    }
    
    static {
        EMPTY_ITERATOR = EmptyIterator.RESETTABLE_INSTANCE;
        EMPTY_LIST_ITERATOR = EmptyListIterator.RESETTABLE_INSTANCE;
        EMPTY_ORDERED_ITERATOR = EmptyOrderedIterator.INSTANCE;
        EMPTY_MAP_ITERATOR = EmptyMapIterator.INSTANCE;
        EMPTY_ORDERED_MAP_ITERATOR = EmptyOrderedMapIterator.INSTANCE;
    }
}
