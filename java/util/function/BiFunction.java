package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiFunction<T, U, R>
{
    R apply(final T p0, final U p1);
    
    default <V> BiFunction<T, U, V> andThen(final Function<? super R, ? extends V> function) {
        Objects.requireNonNull(function);
        return (o, o2) -> function2.apply(this.apply(o, o2));
    }
}
