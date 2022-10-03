package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntUnaryOperator
{
    int applyAsInt(final int p0);
    
    default IntUnaryOperator compose(final IntUnaryOperator intUnaryOperator) {
        Objects.requireNonNull(intUnaryOperator);
        return n2 -> this.applyAsInt(intUnaryOperator2.applyAsInt(n2));
    }
    
    default IntUnaryOperator andThen(final IntUnaryOperator intUnaryOperator) {
        Objects.requireNonNull(intUnaryOperator);
        return n2 -> intUnaryOperator2.applyAsInt(this.applyAsInt(n2));
    }
    
    default IntUnaryOperator identity() {
        return n -> n;
    }
}
