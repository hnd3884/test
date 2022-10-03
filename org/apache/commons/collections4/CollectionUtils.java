package org.apache.commons.collections4;

import org.apache.commons.collections4.collection.TransformedCollection;
import org.apache.commons.collections4.collection.PredicatedCollection;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.collection.SynchronizedCollection;
import java.util.LinkedList;
import org.apache.commons.collections4.iterators.PermutationIterator;
import org.apache.commons.collections4.iterators.CollatingIterator;
import java.util.Comparator;
import org.apache.commons.collections4.collection.UnmodifiableBoundedCollection;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import org.apache.commons.collections4.bag.HashBag;
import java.util.ArrayList;
import org.apache.commons.collections4.functors.TruePredicate;
import java.util.Iterator;
import java.util.Collection;

public class CollectionUtils
{
    public static final Collection EMPTY_COLLECTION;
    
    private CollectionUtils() {
    }
    
    public static <T> Collection<T> emptyCollection() {
        return CollectionUtils.EMPTY_COLLECTION;
    }
    
    public static <T> Collection<T> emptyIfNull(final Collection<T> collection) {
        return (collection == null) ? CollectionUtils.EMPTY_COLLECTION : collection;
    }
    
    public static <O> Collection<O> union(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        final SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<O>(a, b);
        for (final O obj : helper) {
            helper.setCardinality(obj, helper.max(obj));
        }
        return helper.list();
    }
    
    public static <O> Collection<O> intersection(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        final SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<O>(a, b);
        for (final O obj : helper) {
            helper.setCardinality(obj, helper.min(obj));
        }
        return helper.list();
    }
    
    public static <O> Collection<O> disjunction(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        final SetOperationCardinalityHelper<O> helper = new SetOperationCardinalityHelper<O>(a, b);
        for (final O obj : helper) {
            helper.setCardinality(obj, helper.max(obj) - helper.min(obj));
        }
        return helper.list();
    }
    
    public static <O> Collection<O> subtract(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        final Predicate<O> p = TruePredicate.truePredicate();
        return subtract(a, b, p);
    }
    
    public static <O> Collection<O> subtract(final Iterable<? extends O> a, final Iterable<? extends O> b, final Predicate<O> p) {
        final ArrayList<O> list = new ArrayList<O>();
        final HashBag<O> bag = new HashBag<O>();
        for (final O element : b) {
            if (p.evaluate(element)) {
                bag.add(element);
            }
        }
        for (final O element : a) {
            if (!bag.remove(element, 1)) {
                list.add(element);
            }
        }
        return list;
    }
    
    public static boolean containsAll(final Collection<?> coll1, final Collection<?> coll2) {
        if (coll2.isEmpty()) {
            return true;
        }
        final Iterator<?> it = coll1.iterator();
        final Set<Object> elementsAlreadySeen = new HashSet<Object>();
        for (final Object nextElement : coll2) {
            if (elementsAlreadySeen.contains(nextElement)) {
                continue;
            }
            boolean foundCurrentElement = false;
            while (it.hasNext()) {
                final Object p = it.next();
                elementsAlreadySeen.add(p);
                Label_0119: {
                    if (nextElement == null) {
                        if (p == null) {
                            break Label_0119;
                        }
                        continue;
                    }
                    else {
                        if (nextElement.equals(p)) {
                            break Label_0119;
                        }
                        continue;
                    }
                    continue;
                }
                foundCurrentElement = true;
                break;
            }
            if (foundCurrentElement) {
                continue;
            }
            return false;
        }
        return true;
    }
    
    public static boolean containsAny(final Collection<?> coll1, final Collection<?> coll2) {
        if (coll1.size() < coll2.size()) {
            for (final Object aColl1 : coll1) {
                if (coll2.contains(aColl1)) {
                    return true;
                }
            }
        }
        else {
            for (final Object aColl2 : coll2) {
                if (coll1.contains(aColl2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static <O> Map<O, Integer> getCardinalityMap(final Iterable<? extends O> coll) {
        final Map<O, Integer> count = new HashMap<O, Integer>();
        for (final O obj : coll) {
            final Integer c = count.get(obj);
            if (c == null) {
                count.put(obj, 1);
            }
            else {
                count.put(obj, c + 1);
            }
        }
        return count;
    }
    
    public static boolean isSubCollection(final Collection<?> a, final Collection<?> b) {
        final CardinalityHelper<Object> helper = new CardinalityHelper<Object>(a, b);
        for (final Object obj : a) {
            if (helper.freqA(obj) > helper.freqB(obj)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isProperSubCollection(final Collection<?> a, final Collection<?> b) {
        return a.size() < b.size() && isSubCollection(a, b);
    }
    
    public static boolean isEqualCollection(final Collection<?> a, final Collection<?> b) {
        if (a.size() != b.size()) {
            return false;
        }
        final CardinalityHelper<Object> helper = new CardinalityHelper<Object>(a, b);
        if (helper.cardinalityA.size() != helper.cardinalityB.size()) {
            return false;
        }
        for (final Object obj : helper.cardinalityA.keySet()) {
            if (helper.freqA(obj) != helper.freqB(obj)) {
                return false;
            }
        }
        return true;
    }
    
    public static <E> boolean isEqualCollection(final Collection<? extends E> a, final Collection<? extends E> b, final Equator<? super E> equator) {
        if (equator == null) {
            throw new NullPointerException("Equator must not be null.");
        }
        if (a.size() != b.size()) {
            return false;
        }
        final Transformer<E, ?> transformer = new Transformer() {
            @Override
            public EquatorWrapper<?> transform(final Object input) {
                return new EquatorWrapper<Object>(equator, input);
            }
        };
        return isEqualCollection(collect((Iterable<? extends E>)a, (Transformer<? super E, ?>)transformer), collect((Iterable<? extends E>)b, (Transformer<? super E, ?>)transformer));
    }
    
    @Deprecated
    public static <O> int cardinality(final O obj, final Iterable<? super O> coll) {
        if (coll == null) {
            throw new NullPointerException("coll must not be null.");
        }
        return IterableUtils.frequency(coll, obj);
    }
    
    @Deprecated
    public static <T> T find(final Iterable<T> collection, final Predicate<? super T> predicate) {
        return (predicate != null) ? IterableUtils.find(collection, predicate) : null;
    }
    
    @Deprecated
    public static <T, C extends Closure<? super T>> C forAllDo(final Iterable<T> collection, final C closure) {
        if (closure != null) {
            IterableUtils.forEach(collection, closure);
        }
        return closure;
    }
    
    @Deprecated
    public static <T, C extends Closure<? super T>> C forAllDo(final Iterator<T> iterator, final C closure) {
        if (closure != null) {
            IteratorUtils.forEach(iterator, closure);
        }
        return closure;
    }
    
    @Deprecated
    public static <T, C extends Closure<? super T>> T forAllButLastDo(final Iterable<T> collection, final C closure) {
        return (closure != null) ? IterableUtils.forEachButLast(collection, closure) : null;
    }
    
    @Deprecated
    public static <T, C extends Closure<? super T>> T forAllButLastDo(final Iterator<T> iterator, final C closure) {
        return (closure != null) ? IteratorUtils.forEachButLast(iterator, closure) : null;
    }
    
    public static <T> boolean filter(final Iterable<T> collection, final Predicate<? super T> predicate) {
        boolean result = false;
        if (collection != null && predicate != null) {
            final Iterator<T> it = collection.iterator();
            while (it.hasNext()) {
                if (!predicate.evaluate(it.next())) {
                    it.remove();
                    result = true;
                }
            }
        }
        return result;
    }
    
    public static <T> boolean filterInverse(final Iterable<T> collection, final Predicate<? super T> predicate) {
        return filter((Iterable<Object>)collection, (predicate == null) ? null : PredicateUtils.notPredicate((Predicate<? super Object>)predicate));
    }
    
    public static <C> void transform(final Collection<C> collection, final Transformer<? super C, ? extends C> transformer) {
        if (collection != null && transformer != null) {
            if (collection instanceof List) {
                final List<C> list = (List)collection;
                final ListIterator<C> it = list.listIterator();
                while (it.hasNext()) {
                    it.set((C)transformer.transform(it.next()));
                }
            }
            else {
                final Collection<C> resultCollection = collect((Iterable<C>)collection, transformer);
                collection.clear();
                collection.addAll((Collection<? extends C>)resultCollection);
            }
        }
    }
    
    @Deprecated
    public static <C> int countMatches(final Iterable<C> input, final Predicate<? super C> predicate) {
        return (predicate == null) ? 0 : ((int)IterableUtils.countMatches(input, predicate));
    }
    
    @Deprecated
    public static <C> boolean exists(final Iterable<C> input, final Predicate<? super C> predicate) {
        return predicate != null && IterableUtils.matchesAny(input, predicate);
    }
    
    @Deprecated
    public static <C> boolean matchesAll(final Iterable<C> input, final Predicate<? super C> predicate) {
        return predicate != null && IterableUtils.matchesAll(input, predicate);
    }
    
    public static <O> Collection<O> select(final Iterable<? extends O> inputCollection, final Predicate<? super O> predicate) {
        final Collection<O> answer = (inputCollection instanceof Collection) ? new ArrayList<O>(((Collection)inputCollection).size()) : new ArrayList<O>();
        return select((Iterable<?>)inputCollection, (Predicate<? super Object>)predicate, answer);
    }
    
    public static <O, R extends Collection<? super O>> R select(final Iterable<? extends O> inputCollection, final Predicate<? super O> predicate, final R outputCollection) {
        if (inputCollection != null && predicate != null) {
            for (final O item : inputCollection) {
                if (predicate.evaluate(item)) {
                    outputCollection.add(item);
                }
            }
        }
        return outputCollection;
    }
    
    public static <O, R extends Collection<? super O>> R select(final Iterable<? extends O> inputCollection, final Predicate<? super O> predicate, final R outputCollection, final R rejectedCollection) {
        if (inputCollection != null && predicate != null) {
            for (final O element : inputCollection) {
                if (predicate.evaluate(element)) {
                    outputCollection.add(element);
                }
                else {
                    rejectedCollection.add(element);
                }
            }
        }
        return outputCollection;
    }
    
    public static <O> Collection<O> selectRejected(final Iterable<? extends O> inputCollection, final Predicate<? super O> predicate) {
        final Collection<O> answer = (inputCollection instanceof Collection) ? new ArrayList<O>(((Collection)inputCollection).size()) : new ArrayList<O>();
        return selectRejected((Iterable<?>)inputCollection, (Predicate<? super Object>)predicate, answer);
    }
    
    public static <O, R extends Collection<? super O>> R selectRejected(final Iterable<? extends O> inputCollection, final Predicate<? super O> predicate, final R outputCollection) {
        if (inputCollection != null && predicate != null) {
            for (final O item : inputCollection) {
                if (!predicate.evaluate(item)) {
                    outputCollection.add(item);
                }
            }
        }
        return outputCollection;
    }
    
    public static <I, O> Collection<O> collect(final Iterable<I> inputCollection, final Transformer<? super I, ? extends O> transformer) {
        final Collection<O> answer = (inputCollection instanceof Collection) ? new ArrayList<O>(((Collection)inputCollection).size()) : new ArrayList<O>();
        return collect((Iterable<?>)inputCollection, (Transformer<? super Object, ?>)transformer, answer);
    }
    
    public static <I, O> Collection<O> collect(final Iterator<I> inputIterator, final Transformer<? super I, ? extends O> transformer) {
        return collect((Iterator<?>)inputIterator, (Transformer<? super Object, ?>)transformer, (ArrayList)new ArrayList());
    }
    
    public static <I, O, R extends Collection<? super O>> R collect(final Iterable<? extends I> inputCollection, final Transformer<? super I, ? extends O> transformer, final R outputCollection) {
        if (inputCollection != null) {
            return collect((Iterator<?>)inputCollection.iterator(), (Transformer<? super Object, ?>)transformer, outputCollection);
        }
        return outputCollection;
    }
    
    public static <I, O, R extends Collection<? super O>> R collect(final Iterator<? extends I> inputIterator, final Transformer<? super I, ? extends O> transformer, final R outputCollection) {
        if (inputIterator != null && transformer != null) {
            while (inputIterator.hasNext()) {
                final I item = (I)inputIterator.next();
                final O value = (O)transformer.transform(item);
                outputCollection.add(value);
            }
        }
        return outputCollection;
    }
    
    public static <T> boolean addIgnoreNull(final Collection<T> collection, final T object) {
        if (collection == null) {
            throw new NullPointerException("The collection must not be null");
        }
        return object != null && collection.add(object);
    }
    
    public static <C> boolean addAll(final Collection<C> collection, final Iterable<? extends C> iterable) {
        if (iterable instanceof Collection) {
            return collection.addAll((Collection)iterable);
        }
        return addAll(collection, iterable.iterator());
    }
    
    public static <C> boolean addAll(final Collection<C> collection, final Iterator<? extends C> iterator) {
        boolean changed = false;
        while (iterator.hasNext()) {
            changed |= collection.add((C)iterator.next());
        }
        return changed;
    }
    
    public static <C> boolean addAll(final Collection<C> collection, final Enumeration<? extends C> enumeration) {
        boolean changed = false;
        while (enumeration.hasMoreElements()) {
            changed |= collection.add((C)enumeration.nextElement());
        }
        return changed;
    }
    
    public static <C> boolean addAll(final Collection<C> collection, final C[] elements) {
        boolean changed = false;
        for (final C element : elements) {
            changed |= collection.add(element);
        }
        return changed;
    }
    
    @Deprecated
    public static <T> T get(final Iterator<T> iterator, final int index) {
        return IteratorUtils.get(iterator, index);
    }
    
    static void checkIndexBounds(final int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
        }
    }
    
    @Deprecated
    public static <T> T get(final Iterable<T> iterable, final int index) {
        return IterableUtils.get(iterable, index);
    }
    
    public static Object get(final Object object, final int index) {
        final int i = index;
        if (i < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + i);
        }
        if (object instanceof Map) {
            final Map<?, ?> map = (Map<?, ?>)object;
            final Iterator<?> iterator = map.entrySet().iterator();
            return IteratorUtils.get(iterator, i);
        }
        if (object instanceof Object[]) {
            return ((Object[])object)[i];
        }
        if (object instanceof Iterator) {
            final Iterator<?> it = (Iterator<?>)object;
            return IteratorUtils.get(it, i);
        }
        if (object instanceof Iterable) {
            final Iterable<?> iterable = (Iterable<?>)object;
            return IterableUtils.get(iterable, i);
        }
        if (object instanceof Collection) {
            final Iterator<?> iterator2 = ((Collection)object).iterator();
            return IteratorUtils.get(iterator2, i);
        }
        if (object instanceof Enumeration) {
            final Enumeration<?> it2 = (Enumeration<?>)object;
            return EnumerationUtils.get(it2, i);
        }
        if (object == null) {
            throw new IllegalArgumentException("Unsupported object type: null");
        }
        try {
            return Array.get(object, i);
        }
        catch (final IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }
    
    public static <K, V> Map.Entry<K, V> get(final Map<K, V> map, final int index) {
        checkIndexBounds(index);
        return get(map.entrySet(), index);
    }
    
    public static int size(final Object object) {
        if (object == null) {
            return 0;
        }
        int total = 0;
        if (object instanceof Map) {
            total = ((Map)object).size();
        }
        else if (object instanceof Collection) {
            total = ((Collection)object).size();
        }
        else if (object instanceof Iterable) {
            total = IterableUtils.size((Iterable<?>)object);
        }
        else if (object instanceof Object[]) {
            total = ((Object[])object).length;
        }
        else if (object instanceof Iterator) {
            total = IteratorUtils.size((Iterator<?>)object);
        }
        else if (object instanceof Enumeration) {
            final Enumeration<?> it = (Enumeration<?>)object;
            while (it.hasMoreElements()) {
                ++total;
                it.nextElement();
            }
        }
        else {
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
        if (object == null) {
            return true;
        }
        if (object instanceof Collection) {
            return ((Collection)object).isEmpty();
        }
        if (object instanceof Iterable) {
            return IterableUtils.isEmpty((Iterable<?>)object);
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
        try {
            return Array.getLength(object) == 0;
        }
        catch (final IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }
    
    public static boolean isEmpty(final Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }
    
    public static boolean isNotEmpty(final Collection<?> coll) {
        return !isEmpty(coll);
    }
    
    public static void reverseArray(final Object[] array) {
        for (int i = 0, j = array.length - 1; j > i; --j, ++i) {
            final Object tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }
    
    public static boolean isFull(final Collection<?> coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        if (coll instanceof BoundedCollection) {
            return ((BoundedCollection)coll).isFull();
        }
        try {
            final BoundedCollection<?> bcoll = UnmodifiableBoundedCollection.unmodifiableBoundedCollection(coll);
            return bcoll.isFull();
        }
        catch (final IllegalArgumentException ex) {
            return false;
        }
    }
    
    public static int maxSize(final Collection<?> coll) {
        if (coll == null) {
            throw new NullPointerException("The collection must not be null");
        }
        if (coll instanceof BoundedCollection) {
            return ((BoundedCollection)coll).maxSize();
        }
        try {
            final BoundedCollection<?> bcoll = UnmodifiableBoundedCollection.unmodifiableBoundedCollection(coll);
            return bcoll.maxSize();
        }
        catch (final IllegalArgumentException ex) {
            return -1;
        }
    }
    
    public static <O extends Comparable<? super O>> List<O> collate(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        return collate(a, b, ComparatorUtils.naturalComparator(), true);
    }
    
    public static <O extends Comparable<? super O>> List<O> collate(final Iterable<? extends O> a, final Iterable<? extends O> b, final boolean includeDuplicates) {
        return collate(a, b, ComparatorUtils.naturalComparator(), includeDuplicates);
    }
    
    public static <O> List<O> collate(final Iterable<? extends O> a, final Iterable<? extends O> b, final Comparator<? super O> c) {
        return collate(a, b, c, true);
    }
    
    public static <O> List<O> collate(final Iterable<? extends O> a, final Iterable<? extends O> b, final Comparator<? super O> c, final boolean includeDuplicates) {
        if (a == null || b == null) {
            throw new NullPointerException("The collections must not be null");
        }
        if (c == null) {
            throw new NullPointerException("The comparator must not be null");
        }
        final int totalSize = (a instanceof Collection && b instanceof Collection) ? Math.max(1, ((Collection)a).size() + ((Collection)b).size()) : 10;
        final Iterator<O> iterator = new CollatingIterator<O>(c, a.iterator(), b.iterator());
        if (includeDuplicates) {
            return IteratorUtils.toList((Iterator<? extends O>)iterator, totalSize);
        }
        final ArrayList<O> mergedList = new ArrayList<O>(totalSize);
        O lastItem = null;
        while (iterator.hasNext()) {
            final O item = iterator.next();
            if (lastItem == null || !lastItem.equals(item)) {
                mergedList.add(item);
            }
            lastItem = item;
        }
        mergedList.trimToSize();
        return mergedList;
    }
    
    public static <E> Collection<List<E>> permutations(final Collection<E> collection) {
        final PermutationIterator<E> it = new PermutationIterator<E>((Collection<? extends E>)collection);
        final Collection<List<E>> result = new LinkedList<List<E>>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }
    
    public static <C> Collection<C> retainAll(final Collection<C> collection, final Collection<?> retain) {
        return ListUtils.retainAll(collection, retain);
    }
    
    public static <E> Collection<E> retainAll(final Iterable<E> collection, final Iterable<? extends E> retain, final Equator<? super E> equator) {
        final Transformer<E, EquatorWrapper<E>> transformer = new Transformer<E, EquatorWrapper<E>>() {
            @Override
            public EquatorWrapper<E> transform(final E input) {
                return new EquatorWrapper<E>(equator, input);
            }
        };
        final Set<EquatorWrapper<E>> retainSet = collect((Iterable<?>)retain, (Transformer<? super Object, ?>)transformer, (HashSet)new HashSet());
        final List<E> list = new ArrayList<E>();
        for (final E element : collection) {
            if (retainSet.contains(new EquatorWrapper((Equator<? super Object>)equator, element))) {
                list.add(element);
            }
        }
        return list;
    }
    
    public static <E> Collection<E> removeAll(final Collection<E> collection, final Collection<?> remove) {
        return ListUtils.removeAll(collection, remove);
    }
    
    public static <E> Collection<E> removeAll(final Iterable<E> collection, final Iterable<? extends E> remove, final Equator<? super E> equator) {
        final Transformer<E, EquatorWrapper<E>> transformer = new Transformer<E, EquatorWrapper<E>>() {
            @Override
            public EquatorWrapper<E> transform(final E input) {
                return new EquatorWrapper<E>(equator, input);
            }
        };
        final Set<EquatorWrapper<E>> removeSet = collect((Iterable<?>)remove, (Transformer<? super Object, ?>)transformer, (HashSet)new HashSet());
        final List<E> list = new ArrayList<E>();
        for (final E element : collection) {
            if (!removeSet.contains(new EquatorWrapper((Equator<? super Object>)equator, element))) {
                list.add(element);
            }
        }
        return list;
    }
    
    @Deprecated
    public static <C> Collection<C> synchronizedCollection(final Collection<C> collection) {
        return SynchronizedCollection.synchronizedCollection(collection);
    }
    
    @Deprecated
    public static <C> Collection<C> unmodifiableCollection(final Collection<? extends C> collection) {
        return UnmodifiableCollection.unmodifiableCollection(collection);
    }
    
    public static <C> Collection<C> predicatedCollection(final Collection<C> collection, final Predicate<? super C> predicate) {
        return PredicatedCollection.predicatedCollection(collection, predicate);
    }
    
    public static <E> Collection<E> transformingCollection(final Collection<E> collection, final Transformer<? super E, ? extends E> transformer) {
        return TransformedCollection.transformingCollection(collection, transformer);
    }
    
    public static <E> E extractSingleton(final Collection<E> collection) {
        if (collection == null) {
            throw new NullPointerException("Collection must not be null.");
        }
        if (collection.size() != 1) {
            throw new IllegalArgumentException("Can extract singleton only when collection size == 1");
        }
        return collection.iterator().next();
    }
    
    static {
        EMPTY_COLLECTION = UnmodifiableCollection.unmodifiableCollection((Collection<?>)new ArrayList<Object>());
    }
    
    private static class CardinalityHelper<O>
    {
        final Map<O, Integer> cardinalityA;
        final Map<O, Integer> cardinalityB;
        
        public CardinalityHelper(final Iterable<? extends O> a, final Iterable<? extends O> b) {
            this.cardinalityA = CollectionUtils.getCardinalityMap(a);
            this.cardinalityB = CollectionUtils.getCardinalityMap(b);
        }
        
        public final int max(final Object obj) {
            return Math.max(this.freqA(obj), this.freqB(obj));
        }
        
        public final int min(final Object obj) {
            return Math.min(this.freqA(obj), this.freqB(obj));
        }
        
        public int freqA(final Object obj) {
            return this.getFreq(obj, this.cardinalityA);
        }
        
        public int freqB(final Object obj) {
            return this.getFreq(obj, this.cardinalityB);
        }
        
        private final int getFreq(final Object obj, final Map<?, Integer> freqMap) {
            final Integer count = freqMap.get(obj);
            if (count != null) {
                return count;
            }
            return 0;
        }
    }
    
    private static class SetOperationCardinalityHelper<O> extends CardinalityHelper<O> implements Iterable<O>
    {
        private final Set<O> elements;
        private final List<O> newList;
        
        public SetOperationCardinalityHelper(final Iterable<? extends O> a, final Iterable<? extends O> b) {
            super(a, b);
            CollectionUtils.addAll(this.elements = new HashSet<O>(), a);
            CollectionUtils.addAll(this.elements, b);
            this.newList = new ArrayList<O>(this.elements.size());
        }
        
        @Override
        public Iterator<O> iterator() {
            return this.elements.iterator();
        }
        
        public void setCardinality(final O obj, final int count) {
            for (int i = 0; i < count; ++i) {
                this.newList.add(obj);
            }
        }
        
        public Collection<O> list() {
            return this.newList;
        }
    }
    
    private static class EquatorWrapper<O>
    {
        private final Equator<? super O> equator;
        private final O object;
        
        public EquatorWrapper(final Equator<? super O> equator, final O object) {
            this.equator = equator;
            this.object = object;
        }
        
        public O getObject() {
            return this.object;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof EquatorWrapper)) {
                return false;
            }
            final EquatorWrapper<O> otherObj = (EquatorWrapper<O>)obj;
            return this.equator.equate((Object)this.object, (Object)otherObj.getObject());
        }
        
        @Override
        public int hashCode() {
            return this.equator.hash(this.object);
        }
    }
}
