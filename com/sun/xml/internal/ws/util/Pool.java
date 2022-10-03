package com.sun.xml.internal.ws.util;

import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.ref.WeakReference;

public abstract class Pool<T>
{
    private volatile WeakReference<ConcurrentLinkedQueue<T>> queue;
    
    public final T take() {
        final T t = this.getQueue().poll();
        if (t == null) {
            return this.create();
        }
        return t;
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
    
    public final void recycle(final T t) {
        this.getQueue().offer(t);
    }
    
    protected abstract T create();
    
    public static final class Marshaller extends Pool<javax.xml.bind.Marshaller>
    {
        private final JAXBContext context;
        
        public Marshaller(final JAXBContext context) {
            this.context = context;
        }
        
        @Override
        protected javax.xml.bind.Marshaller create() {
            try {
                return this.context.createMarshaller();
            }
            catch (final JAXBException e) {
                throw new AssertionError((Object)e);
            }
        }
    }
    
    public static final class Unmarshaller extends Pool<javax.xml.bind.Unmarshaller>
    {
        private final JAXBContext context;
        
        public Unmarshaller(final JAXBContext context) {
            this.context = context;
        }
        
        @Override
        protected javax.xml.bind.Unmarshaller create() {
            try {
                return this.context.createUnmarshaller();
            }
            catch (final JAXBException e) {
                throw new AssertionError((Object)e);
            }
        }
    }
    
    public static final class TubePool extends Pool<Tube>
    {
        private final Tube master;
        
        public TubePool(final Tube master) {
            this.recycle(this.master = master);
        }
        
        @Override
        protected Tube create() {
            return TubeCloner.clone(this.master);
        }
        
        @Deprecated
        public final Tube takeMaster() {
            return this.master;
        }
    }
}
