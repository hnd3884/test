package java.util;

import java.lang.invoke.SerializedLambda;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.function.ToIntFunction;
import java.util.function.Function;
import java.io.Serializable;

@FunctionalInterface
public interface Comparator<T>
{
    int compare(final T p0, final T p1);
    
    boolean equals(final Object p0);
    
    default Comparator<T> reversed() {
        return Collections.reverseOrder(this);
    }
    
    default Comparator<T> thenComparing(final Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return (o, o2) -> {
            this.compare(o, o2);
            final int n;
            return (n != 0) ? n : comparator2.compare(o, o2);
        };
    }
    
    default <U> Comparator<T> thenComparing(final Function<? super T, ? extends U> function, final Comparator<? super U> comparator) {
        return this.thenComparing(comparing((Function<? super T, ?>)function, (Comparator<? super Object>)comparator));
    }
    
    default <U extends Comparable<? super U>> Comparator<T> thenComparing(final Function<? super T, ? extends U> function) {
        return this.thenComparing(comparing((Function<? super T, ? extends Comparable>)function));
    }
    
    default Comparator<T> thenComparingInt(final ToIntFunction<? super T> toIntFunction) {
        return this.thenComparing(comparingInt(toIntFunction));
    }
    
    default Comparator<T> thenComparingLong(final ToLongFunction<? super T> toLongFunction) {
        return this.thenComparing(comparingLong(toLongFunction));
    }
    
    default Comparator<T> thenComparingDouble(final ToDoubleFunction<? super T> toDoubleFunction) {
        return this.thenComparing(comparingDouble(toDoubleFunction));
    }
    
    default <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
        return Collections.reverseOrder();
    }
    
    default <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
        return (Comparator<T>)Comparators.NaturalOrderComparator.INSTANCE;
    }
    
    default <T> Comparator<T> nullsFirst(final Comparator<? super T> comparator) {
        return new Comparators.NullComparator<T>(true, comparator);
    }
    
    default <T> Comparator<T> nullsLast(final Comparator<? super T> comparator) {
        return new Comparators.NullComparator<T>(false, comparator);
    }
    
    default <T, U> Comparator<T> comparing(final Function<? super T, ? extends U> function, final Comparator<? super U> comparator) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(comparator);
        return (o, o2) -> comparator2.compare(function2.apply(o), function2.apply(o2));
    }
    
    default <T, U extends Comparable<? super U>> Comparator<T> comparing(final Function<? super T, ? extends U> function) {
        Objects.requireNonNull(function);
        return (o, o2) -> function2.apply(o).compareTo(function2.apply(o2));
    }
    
    default <T> Comparator<T> comparingInt(final ToIntFunction<? super T> toIntFunction) {
        Objects.requireNonNull(toIntFunction);
        return (o, o2) -> Integer.compare(toIntFunction2.applyAsInt(o), toIntFunction2.applyAsInt(o2));
    }
    
    default <T> Comparator<T> comparingLong(final ToLongFunction<? super T> toLongFunction) {
        Objects.requireNonNull(toLongFunction);
        return (o, o2) -> Long.compare(toLongFunction2.applyAsLong(o), toLongFunction2.applyAsLong(o2));
    }
    
    default <T> Comparator<T> comparingDouble(final ToDoubleFunction<? super T> toDoubleFunction) {
        Objects.requireNonNull(toDoubleFunction);
        return (o, o2) -> Double.compare(toDoubleFunction2.applyAsDouble(o), toDoubleFunction2.applyAsDouble(o2));
    }
}
