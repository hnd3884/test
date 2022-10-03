package org.glassfish.jersey.inject.hk2;

import org.glassfish.jersey.internal.util.collection.Refs;
import org.glassfish.jersey.internal.util.collection.Ref;
import javax.inject.Provider;
import org.glassfish.hk2.api.Factory;

public abstract class Hk2ReferencingFactory<T> implements Factory<T>
{
    private final Provider<Ref<T>> referenceFactory;
    
    public Hk2ReferencingFactory(final Provider<Ref<T>> referenceFactory) {
        this.referenceFactory = referenceFactory;
    }
    
    public T provide() {
        return (T)((Ref)this.referenceFactory.get()).get();
    }
    
    public void dispose(final T instance) {
    }
    
    public static <T> Factory<Ref<T>> referenceFactory() {
        return (Factory<Ref<T>>)new EmptyReferenceFactory();
    }
    
    public static <T> Factory<Ref<T>> referenceFactory(final T initialValue) {
        if (initialValue == null) {
            return (Factory<Ref<T>>)new EmptyReferenceFactory();
        }
        return (Factory<Ref<T>>)new InitializedReferenceFactory(initialValue);
    }
    
    private static class EmptyReferenceFactory<T> implements Factory<Ref<T>>
    {
        public Ref<T> provide() {
            return (Ref<T>)Refs.emptyRef();
        }
        
        public void dispose(final Ref<T> instance) {
        }
    }
    
    private static class InitializedReferenceFactory<T> implements Factory<Ref<T>>
    {
        private final T initialValue;
        
        public InitializedReferenceFactory(final T initialValue) {
            this.initialValue = initialValue;
        }
        
        public Ref<T> provide() {
            return (Ref<T>)Refs.of((Object)this.initialValue);
        }
        
        public void dispose(final Ref<T> instance) {
        }
    }
}
