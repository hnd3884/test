package io.netty.util.internal;

import io.netty.util.Recycler;

public abstract class ObjectPool<T>
{
    ObjectPool() {
    }
    
    public abstract T get();
    
    public static <T> ObjectPool<T> newPool(final ObjectCreator<T> creator) {
        return new RecyclerObjectPool<T>(ObjectUtil.checkNotNull(creator, "creator"));
    }
    
    private static final class RecyclerObjectPool<T> extends ObjectPool<T>
    {
        private final Recycler<T> recycler;
        
        RecyclerObjectPool(final ObjectCreator<T> creator) {
            this.recycler = new Recycler<T>() {
                @Override
                protected T newObject(final Recycler.Handle<T> handle) {
                    return creator.newObject(handle);
                }
            };
        }
        
        @Override
        public T get() {
            return this.recycler.get();
        }
    }
    
    public interface Handle<T>
    {
        void recycle(final T p0);
    }
    
    public interface ObjectCreator<T>
    {
        T newObject(final Handle<T> p0);
    }
}
