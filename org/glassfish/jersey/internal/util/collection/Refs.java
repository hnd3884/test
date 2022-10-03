package org.glassfish.jersey.internal.util.collection;

public final class Refs
{
    private Refs() {
    }
    
    public static <T> Ref<T> of(final T value) {
        return new DefaultRefImpl<T>(value);
    }
    
    public static <T> Ref<T> emptyRef() {
        return new DefaultRefImpl<T>();
    }
    
    public static <T> Ref<T> threadSafe() {
        return new ThreadSafeRefImpl<T>();
    }
    
    public static <T> Ref<T> threadSafe(final T value) {
        return new ThreadSafeRefImpl<T>(value);
    }
    
    public static <T> Ref<T> immutableRef(final T value) {
        return new ImmutableRefImpl<T>(value);
    }
    
    private static final class ImmutableRefImpl<T> implements Ref<T>
    {
        private final T reference;
        
        ImmutableRefImpl(final T value) {
            this.reference = value;
        }
        
        @Override
        public T get() {
            return this.reference;
        }
        
        @Override
        public void set(final T value) throws IllegalStateException {
            throw new IllegalStateException("This implementation of Ref interface is immutable.");
        }
        
        @Override
        public String toString() {
            return "ImmutableRefImpl{reference=" + this.reference + '}';
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Ref)) {
                return false;
            }
            final Object otherRef = ((Ref)obj).get();
            return this.reference == otherRef || (this.reference != null && this.reference.equals(otherRef));
        }
        
        @Override
        public int hashCode() {
            int hash = 5;
            hash = 47 * hash + ((this.reference != null) ? this.reference.hashCode() : 0);
            return hash;
        }
    }
    
    private static final class DefaultRefImpl<T> implements Ref<T>
    {
        private T reference;
        
        DefaultRefImpl() {
            this.reference = null;
        }
        
        DefaultRefImpl(final T value) {
            this.reference = value;
        }
        
        @Override
        public T get() {
            return this.reference;
        }
        
        @Override
        public void set(final T value) throws IllegalStateException {
            this.reference = value;
        }
        
        @Override
        public String toString() {
            return "DefaultRefImpl{reference=" + this.reference + '}';
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Ref)) {
                return false;
            }
            final Object otherRef = ((Ref)obj).get();
            final T ref = this.reference;
            return ref == otherRef || (ref != null && ref.equals(otherRef));
        }
        
        @Override
        public int hashCode() {
            int hash = 5;
            hash = 47 * hash + ((this.reference != null) ? this.reference.hashCode() : 0);
            return hash;
        }
    }
    
    private static final class ThreadSafeRefImpl<T> implements Ref<T>
    {
        private volatile T reference;
        
        ThreadSafeRefImpl() {
            this.reference = null;
        }
        
        ThreadSafeRefImpl(final T value) {
            this.reference = value;
        }
        
        @Override
        public T get() {
            return this.reference;
        }
        
        @Override
        public void set(final T value) throws IllegalStateException {
            this.reference = value;
        }
        
        @Override
        public String toString() {
            return "ThreadSafeRefImpl{reference=" + this.reference + '}';
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Ref)) {
                return false;
            }
            final Object otherRef = ((Ref)obj).get();
            final T localRef = this.reference;
            return localRef == otherRef || (localRef != null && localRef.equals(otherRef));
        }
        
        @Override
        public int hashCode() {
            final T localRef = this.reference;
            int hash = 5;
            hash = 47 * hash + ((localRef != null) ? localRef.hashCode() : 0);
            return hash;
        }
    }
}
