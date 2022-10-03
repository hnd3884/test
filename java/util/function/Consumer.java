package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Consumer<T>
{
    void accept(final T p0);
    
    default Consumer<T> andThen(final Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        return o -> {
            this.accept(o);
            consumer2.accept(o);
        };
    }
}
