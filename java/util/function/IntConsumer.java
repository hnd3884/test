package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntConsumer
{
    void accept(final int p0);
    
    default IntConsumer andThen(final IntConsumer intConsumer) {
        Objects.requireNonNull(intConsumer);
        return n2 -> {
            this.accept(n2);
            intConsumer2.accept(n2);
        };
    }
}
