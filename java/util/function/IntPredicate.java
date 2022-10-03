package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntPredicate
{
    boolean test(final int p0);
    
    default IntPredicate and(final IntPredicate intPredicate) {
        Objects.requireNonNull(intPredicate);
        return n2 -> this.test(n2) && intPredicate2.test(n2);
    }
    
    default IntPredicate negate() {
        return n2 -> !this.test(n2);
    }
    
    default IntPredicate or(final IntPredicate intPredicate) {
        Objects.requireNonNull(intPredicate);
        return n2 -> this.test(n2) || intPredicate2.test(n2);
    }
}
