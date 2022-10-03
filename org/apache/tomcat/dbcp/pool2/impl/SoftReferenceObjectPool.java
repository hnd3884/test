package org.apache.tomcat.dbcp.pool2.impl;

import java.util.Iterator;
import org.apache.tomcat.dbcp.pool2.DestroyMode;
import org.apache.tomcat.dbcp.pool2.PoolUtils;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import java.lang.ref.SoftReference;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.lang.ref.ReferenceQueue;
import org.apache.tomcat.dbcp.pool2.PooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.BaseObjectPool;

public class SoftReferenceObjectPool<T> extends BaseObjectPool<T>
{
    private final PooledObjectFactory<T> factory;
    private final ReferenceQueue<T> refQueue;
    private int numActive;
    private long destroyCount;
    private long createCount;
    private final LinkedBlockingDeque<PooledSoftReference<T>> idleReferences;
    private final ArrayList<PooledSoftReference<T>> allReferences;
    
    public SoftReferenceObjectPool(final PooledObjectFactory<T> factory) {
        this.refQueue = new ReferenceQueue<T>();
        this.numActive = 0;
        this.destroyCount = 0L;
        this.createCount = 0L;
        this.idleReferences = new LinkedBlockingDeque<PooledSoftReference<T>>();
        this.allReferences = new ArrayList<PooledSoftReference<T>>();
        this.factory = factory;
    }
    
    @Override
    public synchronized T borrowObject() throws Exception {
        this.assertOpen();
        T obj = null;
        boolean newlyCreated = false;
        PooledSoftReference<T> ref = null;
        while (null == obj) {
            if (this.idleReferences.isEmpty()) {
                if (null == this.factory) {
                    throw new NoSuchElementException();
                }
                newlyCreated = true;
                obj = this.factory.makeObject().getObject();
                ++this.createCount;
                ref = new PooledSoftReference<T>(new SoftReference<T>(obj));
                this.allReferences.add(ref);
            }
            else {
                ref = this.idleReferences.pollFirst();
                obj = ref.getObject();
                ref.getReference().clear();
                ref.setReference(new SoftReference<T>(obj));
            }
            if (null != this.factory && null != obj) {
                try {
                    this.factory.activateObject(ref);
                    if (!this.factory.validateObject(ref)) {
                        throw new Exception("ValidateObject failed");
                    }
                    continue;
                }
                catch (final Throwable t) {
                    PoolUtils.checkRethrow(t);
                    try {
                        this.destroy(ref);
                    }
                    catch (final Throwable t2) {
                        PoolUtils.checkRethrow(t2);
                    }
                    finally {
                        obj = null;
                    }
                    if (newlyCreated) {
                        throw new NoSuchElementException("Could not create a validated object, cause: " + t.getMessage());
                    }
                    continue;
                }
            }
        }
        ++this.numActive;
        ref.allocate();
        return obj;
    }
    
    @Override
    public synchronized void returnObject(final T obj) throws Exception {
        boolean success = !this.isClosed();
        final PooledSoftReference<T> ref = this.findReference(obj);
        if (ref == null) {
            throw new IllegalStateException("Returned object not currently part of this pool");
        }
        if (this.factory != null) {
            if (!this.factory.validateObject(ref)) {
                success = false;
            }
            else {
                try {
                    this.factory.passivateObject(ref);
                }
                catch (final Exception e) {
                    success = false;
                }
            }
        }
        final boolean shouldDestroy = !success;
        --this.numActive;
        if (success) {
            ref.deallocate();
            this.idleReferences.add(ref);
        }
        this.notifyAll();
        if (shouldDestroy && this.factory != null) {
            try {
                this.destroy(ref);
            }
            catch (final Exception ex) {}
        }
    }
    
    @Override
    public synchronized void invalidateObject(final T obj) throws Exception {
        final PooledSoftReference<T> ref = this.findReference(obj);
        if (ref == null) {
            throw new IllegalStateException("Object to invalidate is not currently part of this pool");
        }
        if (this.factory != null) {
            this.destroy(ref);
        }
        --this.numActive;
        this.notifyAll();
    }
    
    @Override
    public void invalidateObject(final T obj, final DestroyMode mode) throws Exception {
        this.invalidateObject(obj);
    }
    
    @Override
    public synchronized void addObject() throws Exception {
        this.assertOpen();
        if (this.factory == null) {
            throw new IllegalStateException("Cannot add objects without a factory.");
        }
        final T obj = this.factory.makeObject().getObject();
        ++this.createCount;
        final PooledSoftReference<T> ref = new PooledSoftReference<T>(new SoftReference<T>(obj, this.refQueue));
        this.allReferences.add(ref);
        boolean success = true;
        if (!this.factory.validateObject(ref)) {
            success = false;
        }
        else {
            this.factory.passivateObject(ref);
        }
        final boolean shouldDestroy = !success;
        if (success) {
            this.idleReferences.add(ref);
            this.notifyAll();
        }
        if (shouldDestroy) {
            try {
                this.destroy(ref);
            }
            catch (final Exception ex) {}
        }
    }
    
    @Override
    public synchronized int getNumIdle() {
        this.pruneClearedReferences();
        return this.idleReferences.size();
    }
    
    @Override
    public synchronized int getNumActive() {
        return this.numActive;
    }
    
    @Override
    public synchronized void clear() {
        if (null != this.factory) {
            for (final PooledSoftReference<T> idleReference : this.idleReferences) {
                try {
                    final PooledSoftReference<T> ref = idleReference;
                    if (null == ref.getObject()) {
                        continue;
                    }
                    this.factory.destroyObject(ref);
                }
                catch (final Exception ex) {}
            }
        }
        this.idleReferences.clear();
        this.pruneClearedReferences();
    }
    
    @Override
    public void close() {
        super.close();
        this.clear();
    }
    
    public synchronized PooledObjectFactory<T> getFactory() {
        return this.factory;
    }
    
    private void pruneClearedReferences() {
        this.removeClearedReferences(this.idleReferences.iterator());
        this.removeClearedReferences(this.allReferences.iterator());
        while (this.refQueue.poll() != null) {}
    }
    
    private PooledSoftReference<T> findReference(final T obj) {
        for (final PooledSoftReference<T> reference : this.allReferences) {
            if (reference.getObject() != null && reference.getObject().equals(obj)) {
                return reference;
            }
        }
        return null;
    }
    
    private void destroy(final PooledSoftReference<T> toDestroy) throws Exception {
        toDestroy.invalidate();
        this.idleReferences.remove(toDestroy);
        this.allReferences.remove(toDestroy);
        try {
            this.factory.destroyObject(toDestroy);
        }
        finally {
            ++this.destroyCount;
            toDestroy.getReference().clear();
        }
    }
    
    private void removeClearedReferences(final Iterator<PooledSoftReference<T>> iterator) {
        while (iterator.hasNext()) {
            final PooledSoftReference<T> ref = iterator.next();
            if (ref.getReference() == null || ref.getReference().isEnqueued()) {
                iterator.remove();
            }
        }
    }
    
    @Override
    protected void toStringAppendFields(final StringBuilder builder) {
        super.toStringAppendFields(builder);
        builder.append(", factory=");
        builder.append(this.factory);
        builder.append(", refQueue=");
        builder.append(this.refQueue);
        builder.append(", numActive=");
        builder.append(this.numActive);
        builder.append(", destroyCount=");
        builder.append(this.destroyCount);
        builder.append(", createCount=");
        builder.append(this.createCount);
        builder.append(", idleReferences=");
        builder.append(this.idleReferences);
        builder.append(", allReferences=");
        builder.append(this.allReferences);
    }
}
