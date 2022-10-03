package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoublePredicate
{
    boolean test(final double p0);
    
    default DoublePredicate and(final DoublePredicate doublePredicate) {
        Objects.requireNonNull(doublePredicate);
        return n2 -> this.test(n2) && doublePredicate2.test(n2);
    }
    
    default DoublePredicate negate() {
        return n2 -> !this.test(n2);
    }
    
    default DoublePredicate or(final DoublePredicate doublePredicate) {
        Objects.requireNonNull(doublePredicate);
        return n2 -> this.test(n2) || doublePredicate2.test(n2);
    }
}
