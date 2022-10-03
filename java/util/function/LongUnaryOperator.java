package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongUnaryOperator
{
    long applyAsLong(final long p0);
    
    default LongUnaryOperator compose(final LongUnaryOperator longUnaryOperator) {
        Objects.requireNonNull(longUnaryOperator);
        return n2 -> this.applyAsLong(longUnaryOperator2.applyAsLong(n2));
    }
    
    default LongUnaryOperator andThen(final LongUnaryOperator longUnaryOperator) {
        Objects.requireNonNull(longUnaryOperator);
        return n2 -> longUnaryOperator2.applyAsLong(this.applyAsLong(n2));
    }
    
    default LongUnaryOperator identity() {
        return n -> n;
    }
}
