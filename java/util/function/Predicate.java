package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Predicate<T>
{
    boolean test(final T p0);
    
    default Predicate<T> and(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return o -> this.test(o) && predicate2.test(o);
    }
    
    default Predicate<T> negate() {
        return o -> !this.test(o);
    }
    
    default Predicate<T> or(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return o -> this.test(o) || predicate2.test(o);
    }
    
    default <T> Predicate<T> isEqual(final Object o) {
        return (null == o) ? Objects::isNull : (o3 -> o2.equals(o3));
    }
}
