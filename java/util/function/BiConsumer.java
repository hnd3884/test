package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiConsumer<T, U>
{
    void accept(final T p0, final U p1);
    
    default BiConsumer<T, U> andThen(final BiConsumer<? super T, ? super U> biConsumer) {
        Objects.requireNonNull(biConsumer);
        return (o, o2) -> {
            this.accept(o, o2);
            biConsumer2.accept(o, o2);
        };
    }
}
