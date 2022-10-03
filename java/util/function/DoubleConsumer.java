package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoubleConsumer
{
    void accept(final double p0);
    
    default DoubleConsumer andThen(final DoubleConsumer doubleConsumer) {
        Objects.requireNonNull(doubleConsumer);
        return n2 -> {
            this.accept(n2);
            doubleConsumer2.accept(n2);
        };
    }
}
