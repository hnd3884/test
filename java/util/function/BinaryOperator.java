package java.util.function;

import java.util.Objects;
import java.util.Comparator;

@FunctionalInterface
public interface BinaryOperator<T> extends BiFunction<T, T, T>
{
    default <T> BinaryOperator<T> minBy(final Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return (BinaryOperator<T>)((o2, o4) -> (comparator2.compare(o2, o4) <= 0) ? o2 : o4);
    }
    
    default <T> BinaryOperator<T> maxBy(final Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return (BinaryOperator<T>)((o2, o4) -> (comparator2.compare(o2, o4) >= 0) ? o2 : o4);
    }
}
