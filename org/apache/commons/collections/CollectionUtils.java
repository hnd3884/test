package org.apache.commons.collections;

import org.apache.commons.collections.collection.TransformedCollection;
import org.apache.commons.collections.collection.TypedCollection;
import org.apache.commons.collections.collection.PredicatedCollection;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import org.apache.commons.collections.collection.SynchronizedCollection;
import org.apache.commons.collections.collection.UnmodifiableBoundedCollection;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collection;

public class CollectionUtils
{
    private static Integer INTEGER_ONE;
    public static final Collection EMPTY_COLLECTION;
    
    public static Collection union(final Collection a, final Collection b) {
        final ArrayList list = new ArrayList();
        final Map mapa = getCardinalityMap(a);
        final Map mapb = getCardinalityMap(b);
        final Set elts = new HashSet(a);
        elts.addAll(b);
        final Iterator it = elts.iterator();
        while (it.hasNext()) {
            final Object obj = it.next();
            for (int i = 0, m = Math.max(getFreq(obj, mapa), getFreq(obj, mapb)); i < m; ++i) {
                list.add(obj);
            }
        }
        return list;
    }
    
    public static Collection intersection(final Collection a, final Collection b) {
        final ArrayList list = new ArrayList();
        final Map mapa = getCardinalityMap(a);
        final Map mapb = getCardinalityMap(b);
        final Set elts = new HashSet(a);
        elts.addAll(b);
        final Iterator it = elts.iterator();
        while (it.hasNext()) {
            final Object obj = it.next();
            for (int i = 0, m = Math.min(getFreq(obj, mapa), getFreq(obj, mapb)); i < m; ++i) {
                list.add(obj);
            }
        }
        return list;
    }
    
    public static Collection disjunction(final Collection a, final Collection b) {
        final ArrayList list = new ArrayList();
        final Map mapa = getCardinalityMap(a);
        final Map mapb = getCardinalityMap(b);
        final Set elts = new HashSet(a);
        elts.addAll(b);
        final Iterator it = elts.iterator();
        while (it.hasNext()) {
            final Object obj = it.next();
            for (int i = 0, m = Math.max(getFreq(obj, mapa), getFreq(obj, mapb)) - Math.min(getFreq(obj, mapa), getFreq(obj, mapb)); i < m; ++i) {
                list.add(obj);
            }
        }
        return list;
    }
    
    public static Collection subtract(final Collection a, final Collection b) {
        final ArrayList list = new ArrayList(a);
        final Iterator it = b.iterator();
        while (it.hasNext()) {
            list.remove(it.next());
        }
        return list;
    }
    
    public static boolean containsAny(final Collection coll1, final Collection coll2) {
        if (coll1.size() < coll2.size()) {
            final Iterator it = coll1.iterator();
            while (it.hasNext()) {
                if (coll2.contains(it.next())) {
                    return true;
                }
            }
        }
        else {
            final Iterator it = coll2.iterator();
            while (it.hasNext()) {
                if (coll1.contains(it.next())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static Map getCardinalityMap(final Collection coll) {
        final Map count = new HashMap();
        final Iterator it = coll.iterator();
        while (it.hasNext()) {
            final Object obj = it.next();
            final Integer c = count.get(obj);
            if (c == null) {
                count.put(obj, CollectionUtils.INTEGER_ONE);
            }
            else {
                count.put(obj, new Integer(c + 1));
            }
        }
        return count;
    }
    
    public static boolean isSubCollection(final Collection a, final Collection b) {
        final Map mapa = getCardinalityMap(a);
        final Map mapb = getCardinalityMap(b);
        final Iterator it = a.iterator();
        while (it.hasNext()) {
            final Object obj = it.next();
            if (getFreq(obj, mapa) > getFreq(obj, mapb)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isProperSubCollection(final Collection a, final Collection b) {
        return a.size() < b.size() && isSubCollection(a, b);
    }
    
    public static boolean isEqualCollection(final Collection a, final Collection b) {
        if (a.size() != b.size()) {
            return false;
        }
        final Map mapa = getCardinalityMap(a);
        final Map mapb = getCardinalityMap(b);
        if (mapa.size() != mapb.size()) {
            return false;
        }
        final Iterator it = mapa.keySet().iterator();
        while (it.hasNext()) {
            final Object obj = it.next();
            if (getFreq(obj, mapa) != getFreq(obj, mapb)) {
                return false;
            }
        }
        return true;
    }
    
    public static int cardinality(final Object obj, final Collection coll) {
        if (coll instanceof Set) {
            return coll.contains(obj) ? 1 : 0;
        }
        if (coll instanceof Bag) {
            return ((Bag)coll).getCount(obj);
        }
        int count = 0;
        if (obj == null) {
            final Iterator it = coll.iterator();
            while (it.hasNext()) {
                if (it.next() == null) {
                    ++count;
                }
            }
        }
        else {
            final Iterator it = coll.iterator();
            while (it.hasNext()) {
                if (obj.equals(it.next())) {
                    ++count;
                }
            }
        }
        return count;
    }
    
    public static Object find(final Collection collection, final Predicate predicate) {
        if (collection != null && predicate != null) {
            final Iterator iter = collection.iterator();
            while (iter.hasNext()) {
                final Object item = iter.next();
                if (predicate.evaluate(item)) {
                    return item;
                }
            }
        }
        return null;
    }
    
    public static void forAllDo(final Collection collection, final Closure closure) {
        if (collection != null && closure != null) {
            final Iterator it = collection.iterator();
            while (it.hasNext()) {
                closure.execute(it.next());
            }
        }
    }
    
    public static void filter(final Collection collection, final Predicate predicate) {
        if (collection != null && predicate != null) {
            final Iterator it = collection.iterator();
            while (it.hasNext()) {
                if (!predicate.evaluate(it.next())) {
                    it.remove();
                }
            }
        }
    }
    
    public static void transform(final Collection collection, final Transformer transformer) {
        if (collection != null && transformer != null) {
            if (collection instanceof List) {
                final List list = (List)collection;
                final ListIterator it = list.listIterator();
                while (it.hasNext()) {
                    it.set(transformer.transform(it.next()));
                }
            }
            else {
                final Collection resultCollection = collect(collection, transformer);
                collection.clear();
                collection.addAll(resultCollection);
            }
        }
    }
    
    public static int countMatches(final Collection inputCollection, final Predicate predicate) {
        int count = 0;
        if (inputCollection != null && predicate != null) {
            final Iterator it = inputCollection.iterator();
            while (it.hasNext()) {
                if (predicate.evaluate(it.next())) {
                    ++count;
                }
            }
        }
        return count;
    }
    
    public static boolean exists(final Collection collection, final Predicate predicate) {
        if (collection != null && predicate != null) {
            final Iterator it = collection.iterator();
            while (it.hasNext()) {
                if (predicate.evaluate(it.next())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static Collection select(final Collection inputCollection, final Predicate predicate) {
        final ArrayList answer = new ArrayList(inputCollection.size());
        select(inputCollection, predicate, answer);
        return answer;
    }
    
    public static void select(final Collection inputCollection, final Predicate predicate, final Collection outputCollection) {
        if (inputCollection != null && predicate != null) {
            final Iterator iter = inputCollection.iterator();
            while (iter.hasNext()) {
                final Object item = iter.next();
                if (predicate.evaluate(item)) {
                    outputCollection.add(item);
                }
            }
        }
    }
    
    public static Collection selectRejected(final Collection inputCollection, final Predicate predicate) {
        final ArrayList answer = new ArrayList(inputCollection.size());
        selectRejected(inputCollection, predicate, answer);
        return answer;
    }
    
    public static void selectRejected(final Collection inputCollection, final Predicate predicate, final Collection outputCollection) {
        if (inputCollection != null && predicate != null) {
            final Iterator iter = inputCollection.iterator();
            while (iter.hasNext()) {
                final Object item = iter.next();
                if (!predicate.evaluate(item)) {
                    outputCollection.add(item);
                }
            }
        }
    }
    
    public static Collection collect(final Collection inputCollection, final Transformer transformer) {
        final ArrayList answer = new ArrayList(inputCollection.size());
        collect(inputCollection, transformer, answer);
        return answer;
    }
    
    public static Collection collect(final Iterator inputIterator, final Transformer transformer) {
        final ArrayList answer = new ArrayList();
        collect(inputIterator, transformer, answer);
        return answer;
    }
    
    public static Collection collect(final Collection inputCollection, final Transformer transformer, final Collection outputCollection) {
        if (inputCollection != null) {
            return collect(inputCollection.iterator(), transformer, outputCollection);
        }
        return outputCollection;
    }
    
    public static Collection collect(final Iterator inputIterator, final Transformer transformer, final Collection outputCollection) {
        if (inputIterator != null && transformer != null) {
            while (inputIterator.hasNext()) {
                final Object item = inputIterator.next();
                final Object value = transformer.transform(item);
                outputCollection.add(value);
            }
        }
        return outputCollection;
    }
    
    public static boolean addIgnoreNull(final Collection collection, final Object object) {
        return object != null && collection.add(object);
    }
    
    public static void addAll(final Collection collection, final Iterator iterator) {
        while (iterator.hasNext()) {
            collection.add(iterator.next());
        }
    }
    
    public static void addAll(final Collection collection, final Enumeration enumeration) {
        while (enumeration.hasMoreElements()) {
            collection.add(enumeration.nextElement());
        }
    }
    
    public static void addAll(final Collection collection, final Object[] elements) {
        for (int i = 0, size = elements.length; i < size; ++i) {
            collection.add(elements[i]);
        }
    }
    
    public static Object index(final Object obj, final int idx) {
        return index(obj, new Integer(idx));
    }
    
    public static Object index(final Object obj, final Object index) {
        if (obj instanceof Map) {
            final Map map = (Map)obj;
            if (map.containsKey(index)) {
                return map.get(index);
            }
        }
        int idx = -1;
        if (index instanceof Integer) {
            idx = (int)index;
        }
        if (idx < 0) {
            return obj;
        }
        if (obj instanceof Map) {
            final Map map2 = (Map)obj;
            final Iterator iterator = map2.keySet().iterator();
            return index(iterator, idx);
        }
        if (obj instanceof List) {
            return ((List)obj).get(idx);
        }
        if (obj instanceof Object[]) {
            return ((Object[])obj)[idx];
        }
        if (obj instanceof Enumeration) {
            final Enumeration it = (Enumeration)obj;
            while (it.hasMoreElements()) {
                if (--idx == -1) {
                    return it.nextElement();
                }
                it.nextElement();
            }
        }
        else {
            if (obj instanceof Iterator) {
                return index((Iterator)obj, idx);
            }
            if (obj instanceof Collection) {
                final Iterator iterator2 = ((Collection)obj).iterator();
                return index(iterator2, idx);
            }
        }
        return obj;
    }
    
    private static Object index(final Iterator iterator, int idx) {
        while (iterator.hasNext()) {
            if (--idx == -1) {
                return iterator.next();
            }
            iterator.next();
        }
        return iterator;
    }
    
    public static Object get(final Object object, int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
        }
        if (object instanceof Map) {
            final Map map = (Map)object;
            final Iterator iterator = map.entrySet().iterator();
            return get(iterator, index);
        }
        if (object instanceof List) {
            return ((List)object).get(index);
        }
        if (object instanceof Object[]) {
            return ((Object[])object)[index];
        }
        if (object instanceof Iterator) {
            final Iterator it = (Iterator)object;
            while (it.hasNext()) {
                if (--index == -1) {
                    return it.next();
                }
                it.next();
            }
            throw new IndexOutOfBoundsException("Entry does not exist: " + index);
        }
        if (object instanceof Collection) {
            final Iterator iterator2 = ((Collection)object).iterator();
            return get(iterator2, index);
        }
        if (object instanceof Enumeration) {
            final Enumeration it2 = (Enumeration)object;
            while (it2.hasMoreElements()) {
                if (--index == -1) {
                    return it2.nextElement();
                }
                it2.nextElement();
            }
            throw new IndexOutOfBoundsException("Entry does not exist: " + index);
        }
        if (object == null) {
            throw new IllegalArgumentException("Unsupported object type: null");
        }
        try {
            return Array.get(object, index);
        }
        catch (final IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }
    
    public static int size(final Object object) {
        int total = 0;
        if (object instanceof Map) {
            total = ((Map)object).size();
        }
        else if (object instanceof Collection) {
            total = ((Collection)object).size();
        }
        else if (object instanceof Object[]) {
            total = ((Object[])object).length;
        }
        else if (object instanceof Iterator) {
            final Iterator it = (Iterator)object;
            while (it.hasNext()) {
                ++total;
                it.next();
            }
        }
        else if (object instanceof Enumeration) {
            final Enumeration it2 = (Enumeration)object;
            while (it2.hasMoreElements()) {
                ++total;
                it2.nextElement();
            }
        }
        else {
            if (object == null) {
                throw new IllegalArgumentException("Unsupported object type: null");
            }
            try {
                total = Array.getLength(object);
            }
            catch (final IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
            }
        }
        return total;
    }
    
    public static boolean sizeIsEmpty(final Object object) {
        if (object instanceof Collection) {
            return ((Collection)object).isEmpty();
        }
        if (object instanceof Map) {
            return ((Map)object).isEmpty();
        }
        if (object instanceof Object[]) {
            return ((Object[])object).length == 0;
        }
        if (object instanceof Iterator) {
            return !((Iterator)object).hasNext();
        }
        if (object instanceof Enumeration) {
            return !((Enumeration)object).hasMoreElements();
        }
        if (object == null) {
            throw new IllegalArgumentException("Unsupported object type: null");
        }
        try {
            return Array.getLength(object) == 0;
        }
        catch (final IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }
    
    public static boolean isEmpty(final Collection coll) {
        return coll == null || coll.isEmpty();
    }
    
    public static boolean isNotEmpty(final Collection coll) {
        return !isEmpty(coll);
    }
    
    public static void reverseArray(final Object[] array) {
        for (int i = 0, j = array.length - 1; j > i; --j, ++i) {
            final Object tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }
    
    private static final int getFreq(final Object obj, final Map freqMap) {
        final Integer count = freqMap.get(obj);
        if (count != null) {
            return count;
        }
        return 0;
    }
    
    public static boolean isFull(final Collection coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        if (coll instanceof BoundedCollection) {
            return ((BoundedCollection)coll).isFull();
        }
        try {
            final BoundedCollection bcoll = UnmodifiableBoundedCollection.decorateUsing(coll);
            return bcoll.isFull();
        }
        catch (final IllegalArgumentException ex) {
            return false;
        }
    }
    
    public static int maxSize(final Collection coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        if (coll instanceof BoundedCollection) {
            return ((BoundedCollection)coll).maxSize();
        }
        try {
            final BoundedCollection bcoll = UnmodifiableBoundedCollection.decorateUsing(coll);
            return bcoll.maxSize();
        }
        catch (final IllegalArgumentException ex) {
            return -1;
        }
    }
    
    public static Collection retainAll(final Collection collection, final Collection retain) {
        return ListUtils.retainAll(collection, retain);
    }
    
    public static Collection removeAll(final Collection collection, final Collection remove) {
        return ListUtils.removeAll(collection, remove);
    }
    
    public static Collection synchronizedCollection(final Collection collection) {
        return SynchronizedCollection.decorate(collection);
    }
    
    public static Collection unmodifiableCollection(final Collection collection) {
        return UnmodifiableCollection.decorate(collection);
    }
    
    public static Collection predicatedCollection(final Collection collection, final Predicate predicate) {
        return PredicatedCollection.decorate(collection, predicate);
    }
    
    public static Collection typedCollection(final Collection collection, final Class type) {
        return TypedCollection.decorate(collection, type);
    }
    
    public static Collection transformedCollection(final Collection collection, final Transformer transformer) {
        return TransformedCollection.decorate(collection, transformer);
    }
    
    static {
        CollectionUtils.INTEGER_ONE = new Integer(1);
        EMPTY_COLLECTION = UnmodifiableCollection.decorate(new ArrayList());
    }
}
