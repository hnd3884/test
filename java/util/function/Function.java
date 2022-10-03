package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Function<T, R>
{
    R apply(final T p0);
    
    default <V> Function<V, R> compose(final Function<? super V, ? extends T> function) {
        Objects.requireNonNull(function);
        return (Function<V, R>)(o -> this.apply(function2.apply(o)));
    }
    
    default <V> Function<T, V> andThen(final Function<? super R, ? extends V> function) {
        Objects.requireNonNull(function);
        return o -> function2.apply(this.apply(o));
    }
    
    default <T> Function<T, T> identity() {
        return (Function<T, T>)(o -> o);
    }
}
