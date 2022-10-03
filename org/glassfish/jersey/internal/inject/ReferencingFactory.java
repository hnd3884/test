package org.glassfish.jersey.internal.inject;

import org.glassfish.jersey.internal.util.collection.Refs;
import org.glassfish.jersey.internal.util.collection.Ref;
import javax.inject.Provider;
import java.util.function.Supplier;

public abstract class ReferencingFactory<T> implements Supplier<T>
{
    private final Provider<Ref<T>> referenceFactory;
    
    public ReferencingFactory(final Provider<Ref<T>> referenceFactory) {
        this.referenceFactory = referenceFactory;
    }
    
    @Override
    public T get() {
        return (T)((Ref)this.referenceFactory.get()).get();
    }
    
    public static <T> Supplier<Ref<T>> referenceFactory() {
        return new EmptyReferenceFactory<T>();
    }
    
    public static <T> Supplier<Ref<T>> referenceFactory(final T initialValue) {
        if (initialValue == null) {
            return new EmptyReferenceFactory<T>();
        }
        return new InitializedReferenceFactory<T>(initialValue);
    }
    
    private static class EmptyReferenceFactory<T> implements Supplier<Ref<T>>
    {
        @Override
        public Ref<T> get() {
            return Refs.emptyRef();
        }
    }
    
    private static class InitializedReferenceFactory<T> implements Supplier<Ref<T>>
    {
        private final T initialValue;
        
        public InitializedReferenceFactory(final T initialValue) {
            this.initialValue = initialValue;
        }
        
        @Override
        public Ref<T> get() {
            return Refs.of(this.initialValue);
        }
    }
}
