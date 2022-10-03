package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongPredicate
{
    boolean test(final long p0);
    
    default LongPredicate and(final LongPredicate longPredicate) {
        Objects.requireNonNull(longPredicate);
        return n2 -> this.test(n2) && longPredicate2.test(n2);
    }
    
    default LongPredicate negate() {
        return n2 -> !this.test(n2);
    }
    
    default LongPredicate or(final LongPredicate longPredicate) {
        Objects.requireNonNull(longPredicate);
        return n2 -> this.test(n2) || longPredicate2.test(n2);
    }
}
