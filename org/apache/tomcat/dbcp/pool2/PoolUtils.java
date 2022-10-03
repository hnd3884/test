package org.apache.tomcat.dbcp.pool2;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Timer;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.TimerTask;

public final class PoolUtils
{
    private static final String MSG_MIN_IDLE = "minIdle must be non-negative.";
    public static final String MSG_NULL_KEY = "key must not be null.";
    private static final String MSG_NULL_KEYED_POOL = "keyedPool must not be null.";
    public static final String MSG_NULL_KEYS = "keys must not be null.";
    private static final String MSG_NULL_POOL = "pool must not be null.";
    
    public static void checkRethrow(final Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError)t;
        }
    }
    
    public static <T> TimerTask checkMinIdle(final ObjectPool<T> pool, final int minIdle, final long period) throws IllegalArgumentException {
        if (pool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (minIdle < 0) {
            throw new IllegalArgumentException("minIdle must be non-negative.");
        }
        final TimerTask task = new ObjectPoolMinIdleTimerTask<Object>(pool, minIdle);
        getMinIdleTimer().schedule(task, 0L, period);
        return task;
    }
    
    public static <K, V> TimerTask checkMinIdle(final KeyedObjectPool<K, V> keyedPool, final K key, final int minIdle, final long period) throws IllegalArgumentException {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        if (key == null) {
            throw new IllegalArgumentException("key must not be null.");
        }
        if (minIdle < 0) {
            throw new IllegalArgumentException("minIdle must be non-negative.");
        }
        final TimerTask task = new KeyedObjectPoolMinIdleTimerTask<Object, Object>(keyedPool, key, minIdle);
        getMinIdleTimer().schedule(task, 0L, period);
        return task;
    }
    
    public static <K, V> Map<K, TimerTask> checkMinIdle(final KeyedObjectPool<K, V> keyedPool, final Collection<K> keys, final int minIdle, final long period) throws IllegalArgumentException {
        if (keys == null) {
            throw new IllegalArgumentException("keys must not be null.");
        }
        final Map<K, TimerTask> tasks = new HashMap<K, TimerTask>(keys.size());
        for (final K key : keys) {
            final TimerTask task = checkMinIdle(keyedPool, key, minIdle, period);
            tasks.put(key, task);
        }
        return tasks;
    }
    
    @Deprecated
    public static <T> void prefill(final ObjectPool<T> pool, final int count) throws Exception, IllegalArgumentException {
        if (pool == null) {
            throw new IllegalArgumentException("pool must not be null.");
        }
        pool.addObjects(count);
    }
    
    @Deprecated
    public static <K, V> void prefill(final KeyedObjectPool<K, V> keyedPool, final K key, final int count) throws Exception, IllegalArgumentException {
        if (keyedPool == null) {
            throw new IllegalArgumentException("keyedPool must not be null.");
        }
        keyedPool.addObjects(key, count);
    }
    
    @Deprecated
    public static <K, V> void prefill(final KeyedObjectPool<K, V> keyedPool, final Collection<K> keys, final int count) throws Exception, IllegalArgumentException {
        if (keys == null) {
            throw new IllegalArgumentException("keys must not be null.");
        }
        keyedPool.addObjects(keys, count);
    }
    
    public static <T> PooledObjectFactory<T> synchronizedPooledFactory(final PooledObjectFactory<T> factory) {
        return new SynchronizedPooledObjectFactory<T>(factory);
    }
    
    public static <K, V> KeyedPooledObjectFactory<K, V> synchronizedKeyedPooledFactory(final KeyedPooledObjectFactory<K, V> keyedFactory) {
        return new SynchronizedKeyedPooledObjectFactory<K, V>(keyedFactory);
    }
    
    private static Timer getMinIdleTimer() {
        return TimerHolder.MIN_IDLE_TIMER;
    }
    
    static class TimerHolder
    {
        static final Timer MIN_IDLE_TIMER;
        
        static {
            MIN_IDLE_TIMER = new Timer(true);
        }
    }
    
    private static final class ObjectPoolMinIdleTimerTask<T> extends TimerTask
    {
        private final int minIdle;
        private final ObjectPool<T> pool;
        
        ObjectPoolMinIdleTimerTask(final ObjectPool<T> pool, final int minIdle) throws IllegalArgumentException {
            if (pool == null) {
                throw new IllegalArgumentException("pool must not be null.");
            }
            this.pool = pool;
            this.minIdle = minIdle;
        }
        
        @Override
        public void run() {
            boolean success = false;
            try {
                if (this.pool.getNumIdle() < this.minIdle) {
                    this.pool.addObject();
                }
                success = true;
            }
            catch (final Exception e) {
                this.cancel();
            }
            finally {
                if (!success) {
                    this.cancel();
                }
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("ObjectPoolMinIdleTimerTask");
            sb.append("{minIdle=").append(this.minIdle);
            sb.append(", pool=").append(this.pool);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static final class KeyedObjectPoolMinIdleTimerTask<K, V> extends TimerTask
    {
        private final int minIdle;
        private final K key;
        private final KeyedObjectPool<K, V> keyedPool;
        
        KeyedObjectPoolMinIdleTimerTask(final KeyedObjectPool<K, V> keyedPool, final K key, final int minIdle) throws IllegalArgumentException {
            if (keyedPool == null) {
                throw new IllegalArgumentException("keyedPool must not be null.");
            }
            this.keyedPool = keyedPool;
            this.key = key;
            this.minIdle = minIdle;
        }
        
        @Override
        public void run() {
            boolean success = false;
            try {
                if (this.keyedPool.getNumIdle(this.key) < this.minIdle) {
                    this.keyedPool.addObject(this.key);
                }
                success = true;
            }
            catch (final Exception e) {
                this.cancel();
            }
            finally {
                if (!success) {
                    this.cancel();
                }
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("KeyedObjectPoolMinIdleTimerTask");
            sb.append("{minIdle=").append(this.minIdle);
            sb.append(", key=").append(this.key);
            sb.append(", keyedPool=").append(this.keyedPool);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static final class SynchronizedPooledObjectFactory<T> implements PooledObjectFactory<T>
    {
        private final ReentrantReadWriteLock.WriteLock writeLock;
        private final PooledObjectFactory<T> factory;
        
        SynchronizedPooledObjectFactory(final PooledObjectFactory<T> factory) throws IllegalArgumentException {
            this.writeLock = new ReentrantReadWriteLock().writeLock();
            if (factory == null) {
                throw new IllegalArgumentException("factory must not be null.");
            }
            this.factory = factory;
        }
        
        @Override
        public PooledObject<T> makeObject() throws Exception {
            this.writeLock.lock();
            try {
                return this.factory.makeObject();
            }
            finally {
                this.writeLock.unlock();
            }
        }
        
        @Override
        public void destroyObject(final PooledObject<T> p) throws Exception {
            this.writeLock.lock();
            try {
                this.factory.destroyObject(p);
            }
            finally {
                this.writeLock.unlock();
            }
        }
        
        @Override
        public void destroyObject(final PooledObject<T> p, final DestroyMode mode) throws Exception {
            this.destroyObject(p);
        }
        
        @Override
        public boolean validateObject(final PooledObject<T> p) {
            this.writeLock.lock();
            try {
                return this.factory.validateObject(p);
            }
            finally {
                this.writeLock.unlock();
            }
        }
        
        @Override
        public void activateObject(final PooledObject<T> p) throws Exception {
            this.writeLock.lock();
            try {
                this.factory.activateObject(p);
            }
            finally {
                this.writeLock.unlock();
            }
        }
        
        @Override
        public void passivateObject(final PooledObject<T> p) throws Exception {
            this.writeLock.lock();
            try {
                this.factory.passivateObject(p);
            }
            finally {
                this.writeLock.unlock();
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("SynchronizedPoolableObjectFactory");
            sb.append("{factory=").append(this.factory);
            sb.append('}');
            return sb.toString();
        }
    }
    
    private static final class SynchronizedKeyedPooledObjectFactory<K, V> implements KeyedPooledObjectFactory<K, V>
    {
        private final ReentrantReadWriteLock.WriteLock writeLock;
        private final KeyedPooledObjectFactory<K, V> keyedFactory;
        
        SynchronizedKeyedPooledObjectFactory(final KeyedPooledObjectFactory<K, V> keyedFactory) throws IllegalArgumentException {
            this.writeLock = new ReentrantReadWriteLock().writeLock();
            if (keyedFactory == null) {
                throw new IllegalArgumentException("keyedFactory must not be null.");
            }
            this.keyedFactory = keyedFactory;
        }
        
        @Override
        public PooledObject<V> makeObject(final K key) throws Exception {
            this.writeLock.lock();
            try {
                return this.keyedFactory.makeObject(key);
            }
            finally {
                this.writeLock.unlock();
            }
        }
        
        @Override
        public void destroyObject(final K key, final PooledObject<V> p) throws Exception {
            this.writeLock.lock();
            try {
                this.keyedFactory.destroyObject(key, p);
            }
            finally {
                this.writeLock.unlock();
            }
        }
        
        @Override
        public void destroyObject(final K key, final PooledObject<V> p, final DestroyMode mode) throws Exception {
            this.destroyObject(key, p);
        }
        
        @Override
        public boolean validateObject(final K key, final PooledObject<V> p) {
            this.writeLock.lock();
            try {
                return this.keyedFactory.validateObject(key, p);
            }
            finally {
                this.writeLock.unlock();
            }
        }
        
        @Override
        public void activateObject(final K key, final PooledObject<V> p) throws Exception {
            this.writeLock.lock();
            try {
                this.keyedFactory.activateObject(key, p);
            }
            finally {
                this.writeLock.unlock();
            }
        }
        
        @Override
        public void passivateObject(final K key, final PooledObject<V> p) throws Exception {
            this.writeLock.lock();
            try {
                this.keyedFactory.passivateObject(key, p);
            }
            finally {
                this.writeLock.unlock();
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("SynchronizedKeyedPoolableObjectFactory");
            sb.append("{keyedFactory=").append(this.keyedFactory);
            sb.append('}');
            return sb.toString();
        }
    }
}
