package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiPredicate<T, U>
{
    boolean test(final T p0, final U p1);
    
    default BiPredicate<T, U> and(final BiPredicate<? super T, ? super U> biPredicate) {
        Objects.requireNonNull(biPredicate);
        return (o, o2) -> this.test(o, o2) && biPredicate2.test(o, o2);
    }
    
    default BiPredicate<T, U> negate() {
        return (o, o2) -> !this.test(o, o2);
    }
    
    default BiPredicate<T, U> or(final BiPredicate<? super T, ? super U> biPredicate) {
        Objects.requireNonNull(biPredicate);
        return (o, o2) -> this.test(o, o2) || biPredicate2.test(o, o2);
    }
}
