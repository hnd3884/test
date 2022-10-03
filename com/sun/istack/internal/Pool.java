package com.sun.istack.internal;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.ref.WeakReference;

public interface Pool<T>
{
    @NotNull
    T take();
    
    void recycle(@NotNull final T p0);
    
    public abstract static class Impl<T> implements Pool<T>
    {
        private volatile WeakReference<ConcurrentLinkedQueue<T>> queue;
        
        @NotNull
        @Override
        public final T take() {
            final T t = this.getQueue().poll();
            if (t == null) {
                return this.create();
            }
            return t;
        }
        
        @Override
        public final void recycle(final T t) {
            this.getQueue().offer(t);
        }
        
        private ConcurrentLinkedQueue<T> getQueue() {
            final WeakReference<ConcurrentLinkedQueue<T>> q = this.queue;
            if (q != null) {
                final ConcurrentLinkedQueue<T> d = q.get();
                if (d != null) {
                    return d;
                }
            }
            final ConcurrentLinkedQueue<T> d = new ConcurrentLinkedQueue<T>();
            this.queue = new WeakReference<ConcurrentLinkedQueue<T>>(d);
            return d;
        }
        
        @NotNull
        protected abstract T create();
    }
}
