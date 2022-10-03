package org.apache.commons.collections4.functors;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.Closure;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.collections4.Predicate;

class FunctorUtils
{
    private FunctorUtils() {
    }
    
    static <T> Predicate<T>[] copy(final Predicate<? super T>... predicates) {
        if (predicates == null) {
            return null;
        }
        return predicates.clone();
    }
    
    static <T> Predicate<T> coerce(final Predicate<? super T> predicate) {
        return (Predicate<T>)predicate;
    }
    
    static void validate(final Predicate<?>... predicates) {
        if (predicates == null) {
            throw new NullPointerException("The predicate array must not be null");
        }
        for (int i = 0; i < predicates.length; ++i) {
            if (predicates[i] == null) {
                throw new NullPointerException("The predicate array must not contain a null predicate, index " + i + " was null");
            }
        }
    }
    
    static <T> Predicate<? super T>[] validate(final Collection<? extends Predicate<? super T>> predicates) {
        if (predicates == null) {
            throw new NullPointerException("The predicate collection must not be null");
        }
        final Predicate<? super T>[] preds = new Predicate[predicates.size()];
        int i = 0;
        for (final Predicate<? super T> predicate : predicates) {
            preds[i] = predicate;
            if (preds[i] == null) {
                throw new NullPointerException("The predicate collection must not contain a null predicate, index " + i + " was null");
            }
            ++i;
        }
        return preds;
    }
    
    static <E> Closure<E>[] copy(final Closure<? super E>... closures) {
        if (closures == null) {
            return null;
        }
        return closures.clone();
    }
    
    static void validate(final Closure<?>... closures) {
        if (closures == null) {
            throw new NullPointerException("The closure array must not be null");
        }
        for (int i = 0; i < closures.length; ++i) {
            if (closures[i] == null) {
                throw new NullPointerException("The closure array must not contain a null closure, index " + i + " was null");
            }
        }
    }
    
    static <T> Closure<T> coerce(final Closure<? super T> closure) {
        return (Closure<T>)closure;
    }
    
    static <I, O> Transformer<I, O>[] copy(final Transformer<? super I, ? extends O>... transformers) {
        if (transformers == null) {
            return null;
        }
        return transformers.clone();
    }
    
    static void validate(final Transformer<?, ?>... transformers) {
        if (transformers == null) {
            throw new NullPointerException("The transformer array must not be null");
        }
        for (int i = 0; i < transformers.length; ++i) {
            if (transformers[i] == null) {
                throw new NullPointerException("The transformer array must not contain a null transformer, index " + i + " was null");
            }
        }
    }
    
    static <I, O> Transformer<I, O> coerce(final Transformer<? super I, ? extends O> transformer) {
        return (Transformer<I, O>)transformer;
    }
}
