package java.util.stream;

import java.util.Arrays;
import java.util.AbstractSet;
import java.util.AbstractMap;
import java.util.DoubleSummaryStatistics;
import java.util.LongSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.function.Predicate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.Objects;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.function.ToIntFunction;
import java.util.Optional;
import java.util.Comparator;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.Map;
import java.util.StringJoiner;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BinaryOperator;
import java.util.Set;

public final class Collectors
{
    static final Set<Collector.Characteristics> CH_CONCURRENT_ID;
    static final Set<Collector.Characteristics> CH_CONCURRENT_NOID;
    static final Set<Collector.Characteristics> CH_ID;
    static final Set<Collector.Characteristics> CH_UNORDERED_ID;
    static final Set<Collector.Characteristics> CH_NOID;
    
    private Collectors() {
    }
    
    private static <T> BinaryOperator<T> throwingMerger() {
        return (o, p1) -> {
            new IllegalStateException(String.format("Duplicate key %s", o));
            throw;
        };
    }
    
    private static <I, R> Function<I, R> castingIdentity() {
        return (Function<I, R>)(o -> o);
    }
    
    public static <T, C extends Collection<T>> Collector<T, ?, C> toCollection(final Supplier<C> supplier) {
        return new CollectorImpl<T, Object, C>((Supplier<?>)supplier, Collection::add, (collection, collection2) -> {
            collection.addAll(collection2);
            return collection;
        }, Collectors.CH_ID);
    }
    
    public static <T> Collector<T, ?, List<T>> toList() {
        return new CollectorImpl<T, Object, List<T>>((Supplier<?>)ArrayList::new, List::add, (list, list2) -> {
            list.addAll(list2);
            return list;
        }, Collectors.CH_ID);
    }
    
    public static <T> Collector<T, ?, Set<T>> toSet() {
        return new CollectorImpl<T, Object, Set<T>>((Supplier<?>)HashSet::new, Set::add, (set, set2) -> {
            set.addAll(set2);
            return set;
        }, Collectors.CH_UNORDERED_ID);
    }
    
    public static Collector<CharSequence, ?, String> joining() {
        return new CollectorImpl<CharSequence, Object, String>((Supplier<?>)StringBuilder::new, StringBuilder::append, (sb, sb2) -> {
            sb.append((CharSequence)sb2);
            return sb;
        }, StringBuilder::toString, Collectors.CH_NOID);
    }
    
    public static Collector<CharSequence, ?, String> joining(final CharSequence charSequence) {
        return joining(charSequence, "", "");
    }
    
    public static Collector<CharSequence, ?, String> joining(final CharSequence charSequence, final CharSequence charSequence2, final CharSequence charSequence3) {
        return new CollectorImpl<CharSequence, Object, String>(() -> new StringJoiner(charSequence4, charSequence5, charSequence6), StringJoiner::add, StringJoiner::merge, StringJoiner::toString, Collectors.CH_NOID);
    }
    
    private static <K, V, M extends Map<K, V>> BinaryOperator<M> mapMerger(final BinaryOperator<V> binaryOperator) {
        return (BinaryOperator<M>)((map2, map4) -> {
            map4.entrySet().iterator();
            final Iterator iterator;
            while (iterator.hasNext()) {
                final Map.Entry entry = iterator.next();
                map2.merge(entry.getKey(), entry.getValue(), binaryOperator2);
            }
            return map2;
        });
    }
    
    public static <T, U, A, R> Collector<T, ?, R> mapping(final Function<? super T, ? extends U> function, final Collector<? super U, A, R> collector2) {
        return new CollectorImpl<T, Object, R>(collector2.supplier(), (o, o2) -> {
            collector2.accumulator();
            biConsumer.accept(o, function2.apply(o2));
        }, collector2.combiner(), collector2.finisher(), collector2.characteristics());
    }
    
    public static <T, A, R, RR> Collector<T, A, RR> collectingAndThen(final Collector<T, A, R> collector, final Function<R, RR> function) {
        Object o = collector.characteristics();
        if (((Set)o).contains(Collector.Characteristics.IDENTITY_FINISH)) {
            if (((Set)o).size() == 1) {
                o = Collectors.CH_NOID;
            }
            else {
                final EnumSet<Enum> copy = EnumSet.copyOf((Collection<Enum>)o);
                copy.remove(Collector.Characteristics.IDENTITY_FINISH);
                o = Collections.unmodifiableSet((Set<?>)copy);
            }
        }
        return new CollectorImpl<T, A, RR>(collector.supplier(), collector.accumulator(), collector.combiner(), (Function<Object, Object>)collector.finisher().andThen((Function<? super R, ?>)function), (Set<Collector.Characteristics>)o);
    }
    
    public static <T> Collector<T, ?, Long> counting() {
        return reducing(0L, p0 -> 1L, Long::sum);
    }
    
    public static <T> Collector<T, ?, Optional<T>> minBy(final Comparator<? super T> comparator) {
        return reducing(BinaryOperator.minBy(comparator));
    }
    
    public static <T> Collector<T, ?, Optional<T>> maxBy(final Comparator<? super T> comparator) {
        return reducing(BinaryOperator.maxBy(comparator));
    }
    
    public static <T> Collector<T, ?, Integer> summingInt(final ToIntFunction<? super T> toIntFunction) {
        return new CollectorImpl<T, Object, Integer>(() -> new int[1], (array, o) -> {
            final int n;
            array[n] += toIntFunction2.applyAsInt(o);
        }, (array2, array3) -> {
            final int n2;
            array2[n2] += array3[0];
            return array2;
        }, array4 -> array4[0], Collectors.CH_NOID);
    }
    
    public static <T> Collector<T, ?, Long> summingLong(final ToLongFunction<? super T> toLongFunction) {
        return new CollectorImpl<T, Object, Long>(() -> new long[1], (array, o) -> {
            final int n;
            array[n] += toLongFunction2.applyAsLong(o);
        }, (array2, array3) -> {
            final int n2;
            array2[n2] += array3[0];
            return array2;
        }, array4 -> array4[0], Collectors.CH_NOID);
    }
    
    public static <T> Collector<T, ?, Double> summingDouble(final ToDoubleFunction<? super T> toDoubleFunction) {
        return new CollectorImpl<T, Object, Double>(() -> new double[3], (array, o) -> {
            sumWithCompensation(array, toDoubleFunction2.applyAsDouble(o));
            final int n;
            array[n] += toDoubleFunction2.applyAsDouble(o);
        }, (array2, array3) -> {
            sumWithCompensation(array2, array3[0]);
            final int n2;
            array2[n2] += array3[2];
            return sumWithCompensation(array2, array3[1]);
        }, array4 -> computeFinalSum(array4), Collectors.CH_NOID);
    }
    
    static double[] sumWithCompensation(final double[] array, final double n) {
        final double n2 = n - array[1];
        final double n3 = array[0];
        final double n4 = n3 + n2;
        array[1] = n4 - n3 - n2;
        array[0] = n4;
        return array;
    }
    
    static double computeFinalSum(final double[] array) {
        final double n = array[0] + array[1];
        final double n2 = array[array.length - 1];
        if (Double.isNaN(n) && Double.isInfinite(n2)) {
            return n2;
        }
        return n;
    }
    
    public static <T> Collector<T, ?, Double> averagingInt(final ToIntFunction<? super T> toIntFunction) {
        return new CollectorImpl<T, Object, Double>(() -> new long[2], (array, o) -> {
            final int n;
            array[n] += toIntFunction2.applyAsInt(o);
            final int n2;
            ++array[n2];
        }, (array2, array3) -> {
            final int n3;
            array2[n3] += array3[0];
            final int n4;
            array2[n4] += array3[1];
            return array2;
        }, array4 -> (array4[1] == 0L) ? 0.0 : (array4[0] / (double)array4[1]), Collectors.CH_NOID);
    }
    
    public static <T> Collector<T, ?, Double> averagingLong(final ToLongFunction<? super T> toLongFunction) {
        return new CollectorImpl<T, Object, Double>(() -> new long[2], (array, o) -> {
            final int n;
            array[n] += toLongFunction2.applyAsLong(o);
            final int n2;
            ++array[n2];
        }, (array2, array3) -> {
            final int n3;
            array2[n3] += array3[0];
            final int n4;
            array2[n4] += array3[1];
            return array2;
        }, array4 -> (array4[1] == 0L) ? 0.0 : (array4[0] / (double)array4[1]), Collectors.CH_NOID);
    }
    
    public static <T> Collector<T, ?, Double> averagingDouble(final ToDoubleFunction<? super T> toDoubleFunction) {
        return new CollectorImpl<T, Object, Double>(() -> new double[4], (array, o) -> {
            sumWithCompensation(array, toDoubleFunction2.applyAsDouble(o));
            final int n;
            ++array[n];
            final int n2;
            array[n2] += toDoubleFunction2.applyAsDouble(o);
        }, (array2, array3) -> {
            sumWithCompensation(array2, array3[0]);
            sumWithCompensation(array2, array3[1]);
            final int n3;
            array2[n3] += array3[2];
            final int n4;
            array2[n4] += array3[3];
            return array2;
        }, array4 -> (array4[2] == 0.0) ? 0.0 : (computeFinalSum(array4) / array4[2]), Collectors.CH_NOID);
    }
    
    public static <T> Collector<T, ?, T> reducing(final T t, final BinaryOperator<T> binaryOperator) {
        return new CollectorImpl<T, Object, T>((Supplier<?>)boxSupplier(t), (array, o) -> array[0] = binaryOperator2.apply(array[0], o), (array2, array3) -> {
            array2[0] = binaryOperator3.apply(array2[0], array3[0]);
            return array2;
        }, array4 -> array4[0], Collectors.CH_NOID);
    }
    
    private static <T> Supplier<T[]> boxSupplier(final T t) {
        return (Supplier<T[]>)(() -> new Object[] { o });
    }
    
    public static <T> Collector<T, ?, Optional<T>> reducing(final BinaryOperator<T> binaryOperator) {
        class OptionalBox implements Consumer<T>
        {
            T value;
            boolean present;
            
            OptionalBox() {
                this.value = null;
                this.present = false;
            }
            
            @Override
            public void accept(final T value) {
                if (this.present) {
                    this.value = (T)binaryOperator2.apply(this.value, value);
                }
                else {
                    this.value = value;
                    this.present = true;
                }
            }
        }
        return new CollectorImpl<T, Object, Optional<T>>(() -> new OptionalBox(), OptionalBox::accept, (optionalBox, optionalBox2) -> {
            if (optionalBox2.present) {
                optionalBox.accept(optionalBox2.value);
            }
            return optionalBox;
        }, optionalBox3 -> Optional.ofNullable(optionalBox3.value), Collectors.CH_NOID);
    }
    
    public static <T, U> Collector<T, ?, U> reducing(final U u, final Function<? super T, ? extends U> function, final BinaryOperator<U> binaryOperator) {
        return new CollectorImpl<T, Object, U>((Supplier<?>)boxSupplier(u), (array, o) -> array[0] = binaryOperator2.apply(array[0], function2.apply(o)), (array2, array3) -> {
            array2[0] = binaryOperator3.apply(array2[0], array3[0]);
            return array2;
        }, array4 -> array4[0], Collectors.CH_NOID);
    }
    
    public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingBy(final Function<? super T, ? extends K> function) {
        return groupingBy(function, (Collector<? super T, ?, List<T>>)toList());
    }
    
    public static <T, K, A, D> Collector<T, ?, Map<K, D>> groupingBy(final Function<? super T, ? extends K> function, final Collector<? super T, A, D> collector) {
        return (Collector<T, ?, Map<K, D>>)groupingBy((Function<? super T, ?>)function, HashMap::new, collector);
    }
    
    public static <T, K, D, A, M extends Map<K, D>> Collector<T, ?, M> groupingBy(final Function<? super T, ? extends K> function, final Supplier<M> supplier, final Collector<? super T, A, D> collector3) {
        final BiConsumer biConsumer = (map, o) -> {
            collector3.supplier();
            collector3.accumulator();
            biConsumer2.accept(map.computeIfAbsent(Objects.requireNonNull(function2.apply(o), "element cannot be mapped to a null key"), p1 -> supplier2.get()), o);
            return;
        };
        final BinaryOperator<Map> mapMerger = (BinaryOperator<Map>)mapMerger(collector3.combiner());
        if (collector3.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl<T, Object, M>((Supplier<Object>)supplier, biConsumer, (BinaryOperator<Object>)mapMerger, Collectors.CH_ID);
        }
        return new CollectorImpl<T, Object, M>((Supplier<Object>)supplier, (BiConsumer<Object, Object>)biConsumer, (BinaryOperator<Object>)mapMerger, map2 -> {
            collector3.finisher();
            map2.replaceAll((p1, o4) -> function3.apply(o4));
            return map2;
        }, Collectors.CH_NOID);
    }
    
    public static <T, K> Collector<T, ?, ConcurrentMap<K, List<T>>> groupingByConcurrent(final Function<? super T, ? extends K> function) {
        return (Collector<T, ?, ConcurrentMap<K, List<T>>>)groupingByConcurrent((Function<? super T, ?>)function, ConcurrentHashMap::new, (Collector<? super T, ?, List<? super T>>)toList());
    }
    
    public static <T, K, A, D> Collector<T, ?, ConcurrentMap<K, D>> groupingByConcurrent(final Function<? super T, ? extends K> function, final Collector<? super T, A, D> collector) {
        return (Collector<T, ?, ConcurrentMap<K, D>>)groupingByConcurrent((Function<? super T, ?>)function, ConcurrentHashMap::new, collector);
    }
    
    public static <T, K, A, D, M extends ConcurrentMap<K, D>> Collector<T, ?, M> groupingByConcurrent(final Function<? super T, ? extends K> function, final Supplier<M> supplier, final Collector<? super T, A, D> collector2) {
        collector2.supplier();
        collector2.accumulator();
        final BinaryOperator<Map> mapMerger = (BinaryOperator<Map>)mapMerger(collector2.combiner());
        BiConsumer biConsumer;
        if (collector2.characteristics().contains(Collector.Characteristics.CONCURRENT)) {
            biConsumer = ((concurrentMap, o) -> biConsumer2.accept(concurrentMap.computeIfAbsent(Objects.requireNonNull(function2.apply(o), "element cannot be mapped to a null key"), p1 -> supplier2.get()), o));
        }
        else {
            biConsumer = ((concurrentMap2, o2) -> {
                concurrentMap2.computeIfAbsent(Objects.requireNonNull(function3.apply(o2), "element cannot be mapped to a null key"), p1 -> supplier3.get());
                final Object o3;
                synchronized (o3) {
                    biConsumer3.accept(o3, o2);
                }
                return;
            });
        }
        if (collector2.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl<T, Object, M>((Supplier<Object>)supplier, biConsumer, (BinaryOperator<Object>)mapMerger, Collectors.CH_CONCURRENT_ID);
        }
        return new CollectorImpl<T, Object, M>((Supplier<Object>)supplier, (BiConsumer<Object, Object>)biConsumer, (BinaryOperator<Object>)mapMerger, concurrentMap3 -> {
            collector2.finisher();
            concurrentMap3.replaceAll((p1, o6) -> function4.apply(o6));
            return concurrentMap3;
        }, Collectors.CH_CONCURRENT_NOID);
    }
    
    public static <T> Collector<T, ?, Map<Boolean, List<T>>> partitioningBy(final Predicate<? super T> predicate) {
        return partitioningBy(predicate, (Collector<? super T, ?, List<T>>)toList());
    }
    
    public static <T, D, A> Collector<T, ?, Map<Boolean, D>> partitioningBy(final Predicate<? super T> predicate, final Collector<? super T, A, D> collector3) {
        final BiConsumer biConsumer = (partition, o) -> {
            collector3.accumulator();
            biConsumer2.accept(predicate2.test(o) ? partition.forTrue : partition.forFalse, o);
            return;
        };
        final BiFunction<Object, Object, Partition> biFunction = (partition2, partition3) -> {
            collector3.combiner();
            return new Partition(binaryOperator.apply(partition2.forTrue, partition3.forTrue), binaryOperator.apply(partition2.forFalse, partition3.forFalse));
        };
        final Supplier<Partition> supplier = () -> new Partition(collector4.supplier().get(), collector4.supplier().get());
        if (collector3.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return new CollectorImpl<T, Object, Map<Boolean, D>>((Supplier<Object>)supplier, biConsumer, (BinaryOperator<Object>)biFunction, Collectors.CH_ID);
        }
        return new CollectorImpl<T, Object, Map<Boolean, D>>((Supplier<Object>)supplier, (BiConsumer<Object, Object>)biConsumer, (BinaryOperator<Object>)biFunction, partition4 -> new Partition(collector5.finisher().apply(partition4.forTrue), collector5.finisher().apply(partition4.forFalse)), Collectors.CH_NOID);
    }
    
    public static <T, K, U> Collector<T, ?, Map<K, U>> toMap(final Function<? super T, ? extends K> function, final Function<? super T, ? extends U> function2) {
        return (Collector<T, ?, Map<K, U>>)toMap((Function<? super T, ?>)function, (Function<? super T, ?>)function2, throwingMerger(), HashMap::new);
    }
    
    public static <T, K, U> Collector<T, ?, Map<K, U>> toMap(final Function<? super T, ? extends K> function, final Function<? super T, ? extends U> function2, final BinaryOperator<U> binaryOperator) {
        return (Collector<T, ?, Map<K, U>>)toMap((Function<? super T, ?>)function, function2, binaryOperator, HashMap::new);
    }
    
    public static <T, K, U, M extends Map<K, U>> Collector<T, ?, M> toMap(final Function<? super T, ? extends K> function, final Function<? super T, ? extends U> function2, final BinaryOperator<U> binaryOperator, final Supplier<M> supplier) {
        return new CollectorImpl<T, Object, M>(supplier, (map, o) -> map.merge(function3.apply(o), function4.apply(o), binaryOperator2), mapMerger(binaryOperator), Collectors.CH_ID);
    }
    
    public static <T, K, U> Collector<T, ?, ConcurrentMap<K, U>> toConcurrentMap(final Function<? super T, ? extends K> function, final Function<? super T, ? extends U> function2) {
        return (Collector<T, ?, ConcurrentMap<K, U>>)toConcurrentMap((Function<? super T, ?>)function, (Function<? super T, ?>)function2, throwingMerger(), ConcurrentHashMap::new);
    }
    
    public static <T, K, U> Collector<T, ?, ConcurrentMap<K, U>> toConcurrentMap(final Function<? super T, ? extends K> function, final Function<? super T, ? extends U> function2, final BinaryOperator<U> binaryOperator) {
        return (Collector<T, ?, ConcurrentMap<K, U>>)toConcurrentMap((Function<? super T, ?>)function, function2, binaryOperator, ConcurrentHashMap::new);
    }
    
    public static <T, K, U, M extends ConcurrentMap<K, U>> Collector<T, ?, M> toConcurrentMap(final Function<? super T, ? extends K> function, final Function<? super T, ? extends U> function2, final BinaryOperator<U> binaryOperator, final Supplier<M> supplier) {
        return new CollectorImpl<T, Object, M>(supplier, (concurrentMap, o) -> concurrentMap.merge(function3.apply(o), function4.apply(o), binaryOperator2), mapMerger(binaryOperator), Collectors.CH_CONCURRENT_ID);
    }
    
    public static <T> Collector<T, ?, IntSummaryStatistics> summarizingInt(final ToIntFunction<? super T> toIntFunction) {
        return new CollectorImpl<T, Object, IntSummaryStatistics>((Supplier<?>)IntSummaryStatistics::new, (intSummaryStatistics, o) -> intSummaryStatistics.accept(toIntFunction2.applyAsInt(o)), (intSummaryStatistics2, intSummaryStatistics3) -> {
            intSummaryStatistics2.combine(intSummaryStatistics3);
            return intSummaryStatistics2;
        }, Collectors.CH_ID);
    }
    
    public static <T> Collector<T, ?, LongSummaryStatistics> summarizingLong(final ToLongFunction<? super T> toLongFunction) {
        return new CollectorImpl<T, Object, LongSummaryStatistics>((Supplier<?>)LongSummaryStatistics::new, (longSummaryStatistics, o) -> longSummaryStatistics.accept(toLongFunction2.applyAsLong(o)), (longSummaryStatistics2, longSummaryStatistics3) -> {
            longSummaryStatistics2.combine(longSummaryStatistics3);
            return longSummaryStatistics2;
        }, Collectors.CH_ID);
    }
    
    public static <T> Collector<T, ?, DoubleSummaryStatistics> summarizingDouble(final ToDoubleFunction<? super T> toDoubleFunction) {
        return new CollectorImpl<T, Object, DoubleSummaryStatistics>((Supplier<?>)DoubleSummaryStatistics::new, (doubleSummaryStatistics, o) -> doubleSummaryStatistics.accept(toDoubleFunction2.applyAsDouble(o)), (doubleSummaryStatistics2, doubleSummaryStatistics3) -> {
            doubleSummaryStatistics2.combine(doubleSummaryStatistics3);
            return doubleSummaryStatistics2;
        }, Collectors.CH_ID);
    }
    
    static {
        CH_CONCURRENT_ID = Collections.unmodifiableSet((Set<? extends Collector.Characteristics>)EnumSet.of(Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH));
        CH_CONCURRENT_NOID = Collections.unmodifiableSet((Set<? extends Collector.Characteristics>)EnumSet.of(Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED));
        CH_ID = Collections.unmodifiableSet((Set<? extends Collector.Characteristics>)EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
        CH_UNORDERED_ID = Collections.unmodifiableSet((Set<? extends Collector.Characteristics>)EnumSet.of(Collector.Characteristics.UNORDERED, Collector.Characteristics.IDENTITY_FINISH));
        CH_NOID = Collections.emptySet();
    }
    
    static class CollectorImpl<T, A, R> implements Collector<T, A, R>
    {
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;
        
        CollectorImpl(final Supplier<A> supplier, final BiConsumer<A, T> accumulator, final BinaryOperator<A> combiner, final Function<A, R> finisher, final Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }
        
        CollectorImpl(final Supplier<A> supplier, final BiConsumer<A, T> biConsumer, final BinaryOperator<A> binaryOperator, final Set<Characteristics> set) {
            this(supplier, biConsumer, binaryOperator, castingIdentity(), set);
        }
        
        @Override
        public BiConsumer<A, T> accumulator() {
            return this.accumulator;
        }
        
        @Override
        public Supplier<A> supplier() {
            return this.supplier;
        }
        
        @Override
        public BinaryOperator<A> combiner() {
            return this.combiner;
        }
        
        @Override
        public Function<A, R> finisher() {
            return this.finisher;
        }
        
        @Override
        public Set<Characteristics> characteristics() {
            return this.characteristics;
        }
    }
    
    private static final class Partition<T> extends AbstractMap<Boolean, T> implements Map<Boolean, T>
    {
        final T forTrue;
        final T forFalse;
        
        Partition(final T forTrue, final T forFalse) {
            this.forTrue = forTrue;
            this.forFalse = forFalse;
        }
        
        @Override
        public Set<Entry<Boolean, T>> entrySet() {
            return new AbstractSet<Entry<Boolean, T>>() {
                @Override
                public Iterator<Entry<Boolean, T>> iterator() {
                    return Arrays.asList(new SimpleImmutableEntry(false, Partition.this.forFalse), new SimpleImmutableEntry(true, Partition.this.forTrue)).iterator();
                }
                
                @Override
                public int size() {
                    return 2;
                }
            };
        }
    }
}
