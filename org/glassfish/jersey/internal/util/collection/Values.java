package org.glassfish.jersey.internal.util.collection;

public final class Values
{
    private static final LazyValue EMPTY;
    private static final LazyUnsafeValue EMPTY_UNSAFE;
    
    private Values() {
    }
    
    public static <T> Value<T> empty() {
        return Values.EMPTY;
    }
    
    public static <T, E extends Throwable> UnsafeValue<T, E> emptyUnsafe() {
        return Values.EMPTY_UNSAFE;
    }
    
    public static <T> Value<T> of(final T value) {
        return (value == null) ? empty() : new InstanceValue<T>(value);
    }
    
    public static <T, E extends Throwable> UnsafeValue<T, E> unsafe(final T value) {
        return (value == null) ? emptyUnsafe() : new InstanceUnsafeValue<T, E>(value);
    }
    
    public static <T, E extends Throwable> UnsafeValue<T, E> throwing(final E throwable) {
        if (throwable == null) {
            throw new NullPointerException("Supplied throwable ");
        }
        return new ExceptionValue<T, E>(throwable);
    }
    
    public static <T> LazyValue<T> lazy(final Value<T> delegate) {
        return (delegate == null) ? Values.EMPTY : new LazyValueImpl<T>(delegate);
    }
    
    public static <T> Value<T> eager(final Value<T> delegate) {
        return (delegate == null) ? empty() : new EagerValue<T>((Value)delegate);
    }
    
    public static <T, E extends Throwable> LazyUnsafeValue<T, E> lazy(final UnsafeValue<T, E> delegate) {
        return (delegate == null) ? Values.EMPTY_UNSAFE : new LazyUnsafeValueImpl<T, E>(delegate);
    }
    
    static {
        EMPTY = new LazyValue() {
            @Override
            public Object get() {
                return null;
            }
            
            @Override
            public boolean isInitialized() {
                return true;
            }
        };
        EMPTY_UNSAFE = new LazyUnsafeValue() {
            @Override
            public Object get() {
                return null;
            }
            
            @Override
            public boolean isInitialized() {
                return true;
            }
        };
    }
    
    private static class InstanceValue<T> implements Value<T>
    {
        private final T value;
        
        public InstanceValue(final T value) {
            this.value = value;
        }
        
        @Override
        public T get() {
            return this.value;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o != null && this.getClass() == o.getClass() && this.value.equals(((InstanceValue)o).value));
        }
        
        @Override
        public int hashCode() {
            return (this.value != null) ? this.value.hashCode() : 0;
        }
        
        @Override
        public String toString() {
            return "InstanceValue{value=" + this.value + '}';
        }
    }
    
    private static class InstanceUnsafeValue<T, E extends Throwable> implements UnsafeValue<T, E>
    {
        private final T value;
        
        public InstanceUnsafeValue(final T value) {
            this.value = value;
        }
        
        @Override
        public T get() {
            return this.value;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o != null && this.getClass() == o.getClass() && this.value.equals(((InstanceUnsafeValue)o).value));
        }
        
        @Override
        public int hashCode() {
            return (this.value != null) ? this.value.hashCode() : 0;
        }
        
        @Override
        public String toString() {
            return "InstanceUnsafeValue{value=" + this.value + '}';
        }
    }
    
    private static class ExceptionValue<T, E extends Throwable> implements UnsafeValue<T, E>
    {
        private final E throwable;
        
        public ExceptionValue(final E throwable) {
            this.throwable = throwable;
        }
        
        @Override
        public T get() throws E, Throwable {
            throw this.throwable;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o != null && this.getClass() == o.getClass() && this.throwable.equals(((ExceptionValue)o).throwable));
        }
        
        @Override
        public int hashCode() {
            return (this.throwable != null) ? this.throwable.hashCode() : 0;
        }
        
        @Override
        public String toString() {
            return "ExceptionValue{throwable=" + this.throwable + '}';
        }
    }
    
    private static class EagerValue<T> implements Value<T>
    {
        private final T result;
        
        private EagerValue(final Value<T> value) {
            this.result = value.get();
        }
        
        @Override
        public T get() {
            return this.result;
        }
    }
    
    private static class LazyValueImpl<T> implements LazyValue<T>
    {
        private final Object lock;
        private final Value<T> delegate;
        private volatile Value<T> value;
        
        public LazyValueImpl(final Value<T> delegate) {
            this.delegate = delegate;
            this.lock = new Object();
        }
        
        @Override
        public T get() {
            Value<T> result = this.value;
            if (result == null) {
                synchronized (this.lock) {
                    result = this.value;
                    if (result == null) {
                        result = (this.value = Values.of(this.delegate.get()));
                    }
                }
            }
            return result.get();
        }
        
        @Override
        public boolean isInitialized() {
            return this.value != null;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o != null && this.getClass() == o.getClass() && this.delegate.equals(((LazyValueImpl)o).delegate));
        }
        
        @Override
        public int hashCode() {
            return (this.delegate != null) ? this.delegate.hashCode() : 0;
        }
        
        @Override
        public String toString() {
            return "LazyValue{delegate=" + this.delegate.toString() + '}';
        }
    }
    
    private static class LazyUnsafeValueImpl<T, E extends Throwable> implements LazyUnsafeValue<T, E>
    {
        private final Object lock;
        private final UnsafeValue<T, E> delegate;
        private volatile UnsafeValue<T, E> value;
        
        public LazyUnsafeValueImpl(final UnsafeValue<T, E> delegate) {
            this.delegate = delegate;
            this.lock = new Object();
        }
        
        @Override
        public T get() throws E, Throwable {
            UnsafeValue<T, E> result = this.value;
            if (result == null) {
                synchronized (this.lock) {
                    result = this.value;
                    if (result == null) {
                        try {
                            result = Values.unsafe(this.delegate.get());
                        }
                        catch (final Throwable e) {
                            result = Values.throwing(e);
                        }
                        this.value = result;
                    }
                }
            }
            return result.get();
        }
        
        @Override
        public boolean isInitialized() {
            return this.value != null;
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o != null && this.getClass() == o.getClass() && this.delegate.equals(((LazyUnsafeValueImpl)o).delegate));
        }
        
        @Override
        public int hashCode() {
            return (this.delegate != null) ? this.delegate.hashCode() : 0;
        }
        
        @Override
        public String toString() {
            return "LazyValue{delegate=" + this.delegate.toString() + '}';
        }
    }
}
