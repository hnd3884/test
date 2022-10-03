package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongConsumer
{
    void accept(final long p0);
    
    default LongConsumer andThen(final LongConsumer longConsumer) {
        Objects.requireNonNull(longConsumer);
        return n2 -> {
            this.accept(n2);
            longConsumer2.accept(n2);
        };
    }
}
