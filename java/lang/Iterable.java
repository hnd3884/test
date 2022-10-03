package java.lang;

import java.util.Spliterators;
import java.util.Spliterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.Iterator;

public interface Iterable<T>
{
    Iterator<T> iterator();
    
    default void forEach(final Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        final Iterator<T> iterator = this.iterator();
        while (iterator.hasNext()) {
            consumer.accept(iterator.next());
        }
    }
    
    default Spliterator<T> spliterator() {
        return Spliterators.spliteratorUnknownSize(this.iterator(), 0);
    }
}
