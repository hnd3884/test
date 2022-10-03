package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoubleUnaryOperator
{
    double applyAsDouble(final double p0);
    
    default DoubleUnaryOperator compose(final DoubleUnaryOperator doubleUnaryOperator) {
        Objects.requireNonNull(doubleUnaryOperator);
        return n2 -> this.applyAsDouble(doubleUnaryOperator2.applyAsDouble(n2));
    }
    
    default DoubleUnaryOperator andThen(final DoubleUnaryOperator doubleUnaryOperator) {
        Objects.requireNonNull(doubleUnaryOperator);
        return n2 -> doubleUnaryOperator2.applyAsDouble(this.applyAsDouble(n2));
    }
    
    default DoubleUnaryOperator identity() {
        return n -> n;
    }
}
