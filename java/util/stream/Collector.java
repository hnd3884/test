package java.util.stream;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.BinaryOperator;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface Collector<T, A, R>
{
    Supplier<A> supplier();
    
    BiConsumer<A, T> accumulator();
    
    BinaryOperator<A> combiner();
    
    Function<A, R> finisher();
    
    Set<Characteristics> characteristics();
    
    default <T, R> Collector<T, R, R> of(final Supplier<R> supplier, final BiConsumer<R, T> biConsumer, final BinaryOperator<R> binaryOperator, final Characteristics... array) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(biConsumer);
        Objects.requireNonNull(binaryOperator);
        Objects.requireNonNull(array);
        return new Collectors.CollectorImpl<T, R, R>(supplier, biConsumer, binaryOperator, (array.length == 0) ? Collectors.CH_ID : Collections.unmodifiableSet((Set<? extends Characteristics>)EnumSet.of(Characteristics.IDENTITY_FINISH, array)));
    }
    
    default <T, A, R> Collector<T, A, R> of(final Supplier<A> supplier, final BiConsumer<A, T> biConsumer, final BinaryOperator<A> binaryOperator, final Function<A, R> function, final Characteristics... array) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(biConsumer);
        Objects.requireNonNull(binaryOperator);
        Objects.requireNonNull(function);
        Objects.requireNonNull(array);
        Object o = Collectors.CH_NOID;
        if (array.length > 0) {
            final EnumSet<Characteristics> none = EnumSet.noneOf(Characteristics.class);
            Collections.addAll(none, array);
            o = Collections.unmodifiableSet((Set<?>)none);
        }
        return new Collectors.CollectorImpl<T, A, R>((Supplier<Object>)supplier, (BiConsumer<Object, Object>)biConsumer, (BinaryOperator<Object>)binaryOperator, (Function<Object, Object>)function, (Set<Characteristics>)o);
    }
    
    public enum Characteristics
    {
        CONCURRENT, 
        UNORDERED, 
        IDENTITY_FINISH;
    }
}
