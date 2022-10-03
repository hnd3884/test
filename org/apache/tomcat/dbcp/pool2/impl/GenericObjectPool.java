package org.apache.tomcat.dbcp.pool2.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Deque;
import org.apache.tomcat.dbcp.pool2.PooledObjectState;
import org.apache.tomcat.dbcp.pool2.PoolUtils;
import org.apache.tomcat.dbcp.pool2.DestroyMode;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import java.util.Map;
import org.apache.tomcat.dbcp.pool2.PooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.UsageTracking;
import org.apache.tomcat.dbcp.pool2.ObjectPool;

public class GenericObjectPool<T> extends BaseGenericObjectPool<T> implements ObjectPool<T>, GenericObjectPoolMXBean, UsageTracking<T>
{
    private volatile String factoryType;
    private volatile int maxIdle;
    private volatile int minIdle;
    private final PooledObjectFactory<T> factory;
    private final Map<IdentityWrapper<T>, PooledObject<T>> allObjects;
    private final AtomicLong createCount;
    private long makeObjectCount;
    private final Object makeObjectCountLock;
    private final LinkedBlockingDeque<PooledObject<T>> idleObjects;
    private static final String ONAME_BASE = "org.apache.tomcat.dbcp.pool2:type=GenericObjectPool,name=";
    private volatile AbandonedConfig abandonedConfig;
    
    public GenericObjectPool(final PooledObjectFactory<T> factory) {
        this(factory, (GenericObjectPoolConfig)new GenericObjectPoolConfig());
    }
    
    public GenericObjectPool(final PooledObjectFactory<T> factory, final GenericObjectPoolConfig<T> config) {
        super(config, "org.apache.tomcat.dbcp.pool2:type=GenericObjectPool,name=", config.getJmxNamePrefix());
        this.factoryType = null;
        this.maxIdle = 8;
        this.minIdle = 0;
        this.allObjects = new ConcurrentHashMap<IdentityWrapper<T>, PooledObject<T>>();
        this.createCount = new AtomicLong(0L);
        this.makeObjectCount = 0L;
        this.makeObjectCountLock = new Object();
        this.abandonedConfig = null;
        if (factory == null) {
            this.jmxUnregister();
            throw new IllegalArgumentException("factory may not be null");
        }
        this.factory = factory;
        this.idleObjects = new LinkedBlockingDeque<PooledObject<T>>(config.getFairness());
        this.setConfig(config);
    }
    
    public GenericObjectPool(final PooledObjectFactory<T> factory, final GenericObjectPoolConfig<T> config, final AbandonedConfig abandonedConfig) {
        this(factory, config);
        this.setAbandonedConfig(abandonedConfig);
    }
    
    @Override
    public int getMaxIdle() {
        return this.maxIdle;
    }
    
    public void setMaxIdle(final int maxIdle) {
        this.maxIdle = maxIdle;
    }
    
    public void setMinIdle(final int minIdle) {
        this.minIdle = minIdle;
    }
    
    @Override
    public int getMinIdle() {
        final int maxIdleSave = this.getMaxIdle();
        if (this.minIdle > maxIdleSave) {
            return maxIdleSave;
        }
        return this.minIdle;
    }
    
    @Override
    public boolean isAbandonedConfig() {
        return this.abandonedConfig != null;
    }
    
    @Override
    public boolean getLogAbandoned() {
        final AbandonedConfig ac = this.abandonedConfig;
        return ac != null && ac.getLogAbandoned();
    }
    
    @Override
    public boolean getRemoveAbandonedOnBorrow() {
        final AbandonedConfig ac = this.abandonedConfig;
        return ac != null && ac.getRemoveAbandonedOnBorrow();
    }
    
    @Override
    public boolean getRemoveAbandonedOnMaintenance() {
        final AbandonedConfig ac = this.abandonedConfig;
        return ac != null && ac.getRemoveAbandonedOnMaintenance();
    }
    
    @Override
    public int getRemoveAbandonedTimeout() {
        final AbandonedConfig ac = this.abandonedConfig;
        return (ac != null) ? ac.getRemoveAbandonedTimeout() : Integer.MAX_VALUE;
    }
    
    public void setConfig(final GenericObjectPoolConfig<T> conf) {
        super.setConfig(conf);
        this.setMaxIdle(conf.getMaxIdle());
        this.setMinIdle(conf.getMinIdle());
        this.setMaxTotal(conf.getMaxTotal());
    }
    
    public void setAbandonedConfig(final AbandonedConfig abandonedConfig) {
        if (abandonedConfig == null) {
            this.abandonedConfig = null;
        }
        else {
            (this.abandonedConfig = new AbandonedConfig()).setLogAbandoned(abandonedConfig.getLogAbandoned());
            this.abandonedConfig.setLogWriter(abandonedConfig.getLogWriter());
            this.abandonedConfig.setRemoveAbandonedOnBorrow(abandonedConfig.getRemoveAbandonedOnBorrow());
            this.abandonedConfig.setRemoveAbandonedOnMaintenance(abandonedConfig.getRemoveAbandonedOnMaintenance());
            this.abandonedConfig.setRemoveAbandonedTimeout(abandonedConfig.getRemoveAbandonedTimeout());
            this.abandonedConfig.setUseUsageTracking(abandonedConfig.getUseUsageTracking());
            this.abandonedConfig.setRequireFullStackTrace(abandonedConfig.getRequireFullStackTrace());
        }
    }
    
    public PooledObjectFactory<T> getFactory() {
        return this.factory;
    }
    
    @Override
    public T borrowObject() throws Exception {
        return this.borrowObject(this.getMaxWaitMillis());
    }
    
    public T borrowObject(final long borrowMaxWaitMillis) throws Exception {
        this.assertOpen();
        final AbandonedConfig ac = this.abandonedConfig;
        if (ac != null && ac.getRemoveAbandonedOnBorrow() && this.getNumIdle() < 2 && this.getNumActive() > this.getMaxTotal() - 3) {
            this.removeAbandoned(ac);
        }
        PooledObject<T> p = null;
        final boolean blockWhenExhausted = this.getBlockWhenExhausted();
        final long waitTimeMillis = System.currentTimeMillis();
        while (p == null) {
            boolean create = false;
            p = this.idleObjects.pollFirst();
            if (p == null) {
                p = this.create();
                if (p != null) {
                    create = true;
                }
            }
            if (blockWhenExhausted) {
                if (p == null) {
                    if (borrowMaxWaitMillis < 0L) {
                        p = this.idleObjects.takeFirst();
                    }
                    else {
                        p = this.idleObjects.pollFirst(borrowMaxWaitMillis, TimeUnit.MILLISECONDS);
                    }
                }
                if (p == null) {
                    throw new NoSuchElementException("Timeout waiting for idle object");
                }
            }
            else if (p == null) {
                throw new NoSuchElementException("Pool exhausted");
            }
            if (!p.allocate()) {
                p = null;
            }
            if (p != null) {
                try {
                    this.factory.activateObject(p);
                }
                catch (final Exception e) {
                    try {
                        this.destroy(p, DestroyMode.NORMAL);
                    }
                    catch (final Exception ex) {}
                    p = null;
                    if (create) {
                        final NoSuchElementException nsee = new NoSuchElementException("Unable to activate object");
                        nsee.initCause(e);
                        throw nsee;
                    }
                }
                if (p == null || !this.getTestOnBorrow()) {
                    continue;
                }
                boolean validate = false;
                Throwable validationThrowable = null;
                try {
                    validate = this.factory.validateObject(p);
                }
                catch (final Throwable t) {
                    PoolUtils.checkRethrow(t);
                    validationThrowable = t;
                }
                if (validate) {
                    continue;
                }
                try {
                    this.destroy(p, DestroyMode.NORMAL);
                    this.destroyedByBorrowValidationCount.incrementAndGet();
                }
                catch (final Exception ex2) {}
                p = null;
                if (create) {
                    final NoSuchElementException nsee2 = new NoSuchElementException("Unable to validate object");
                    nsee2.initCause(validationThrowable);
                    throw nsee2;
                }
                continue;
            }
        }
        this.updateStatsBorrow(p, System.currentTimeMillis() - waitTimeMillis);
        return p.getObject();
    }
    
    @Override
    public void returnObject(final T obj) {
        final PooledObject<T> p = this.allObjects.get(new IdentityWrapper(obj));
        if (p == null) {
            if (!this.isAbandonedConfig()) {
                throw new IllegalStateException("Returned object not currently part of this pool");
            }
        }
        else {
            this.markReturningState(p);
            final long activeTime = p.getActiveTimeMillis();
            if (this.getTestOnReturn() && !this.factory.validateObject(p)) {
                try {
                    this.destroy(p, DestroyMode.NORMAL);
                }
                catch (final Exception e) {
                    this.swallowException(e);
                }
                try {
                    this.ensureIdle(1, false);
                }
                catch (final Exception e) {
                    this.swallowException(e);
                }
                this.updateStatsReturn(activeTime);
                return;
            }
            try {
                this.factory.passivateObject(p);
            }
            catch (final Exception e2) {
                this.swallowException(e2);
                try {
                    this.destroy(p, DestroyMode.NORMAL);
                }
                catch (final Exception e3) {
                    this.swallowException(e3);
                }
                try {
                    this.ensureIdle(1, false);
                }
                catch (final Exception e3) {
                    this.swallowException(e3);
                }
                this.updateStatsReturn(activeTime);
                return;
            }
            if (!p.deallocate()) {
                throw new IllegalStateException("Object has already been returned to this pool or is invalid");
            }
            final int maxIdleSave = this.getMaxIdle();
            Label_0306: {
                if (!this.isClosed()) {
                    if (maxIdleSave <= -1 || maxIdleSave > this.idleObjects.size()) {
                        if (this.getLifo()) {
                            this.idleObjects.addFirst(p);
                        }
                        else {
                            this.idleObjects.addLast(p);
                        }
                        if (this.isClosed()) {
                            this.clear();
                        }
                        break Label_0306;
                    }
                }
                try {
                    this.destroy(p, DestroyMode.NORMAL);
                }
                catch (final Exception e3) {
                    this.swallowException(e3);
                }
                try {
                    this.ensureIdle(1, false);
                }
                catch (final Exception e3) {
                    this.swallowException(e3);
                }
            }
            this.updateStatsReturn(activeTime);
        }
    }
    
    @Override
    public void invalidateObject(final T obj) throws Exception {
        this.invalidateObject(obj, DestroyMode.NORMAL);
    }
    
    @Override
    public void invalidateObject(final T obj, final DestroyMode mode) throws Exception {
        final PooledObject<T> p = this.allObjects.get(new IdentityWrapper(obj));
        if (p != null) {
            synchronized (p) {
                if (p.getState() != PooledObjectState.INVALID) {
                    this.destroy(p, mode);
                }
            }
            this.ensureIdle(1, false);
            return;
        }
        if (this.isAbandonedConfig()) {
            return;
        }
        throw new IllegalStateException("Invalidated object not currently part of this pool");
    }
    
    @Override
    public void clear() {
        for (PooledObject<T> p = this.idleObjects.poll(); p != null; p = this.idleObjects.poll()) {
            try {
                this.destroy(p, DestroyMode.NORMAL);
            }
            catch (final Exception e) {
                this.swallowException(e);
            }
        }
    }
    
    @Override
    public int getNumActive() {
        return this.allObjects.size() - this.idleObjects.size();
    }
    
    @Override
    public int getNumIdle() {
        return this.idleObjects.size();
    }
    
    @Override
    public void close() {
        if (this.isClosed()) {
            return;
        }
        synchronized (this.closeLock) {
            if (this.isClosed()) {
                return;
            }
            this.stopEvictor();
            this.closed = true;
            this.clear();
            this.jmxUnregister();
            this.idleObjects.interuptTakeWaiters();
        }
    }
    
    @Override
    public void evict() throws Exception {
        this.assertOpen();
        if (!this.idleObjects.isEmpty()) {
            PooledObject<T> underTest = null;
            final EvictionPolicy<T> evictionPolicy = this.getEvictionPolicy();
            synchronized (this.evictionLock) {
                final EvictionConfig evictionConfig = new EvictionConfig(this.getMinEvictableIdleTimeMillis(), this.getSoftMinEvictableIdleTimeMillis(), this.getMinIdle());
                final boolean testWhileIdle = this.getTestWhileIdle();
                for (int i = 0, m = this.getNumTests(); i < m; ++i) {
                    if (this.evictionIterator == null || !this.evictionIterator.hasNext()) {
                        this.evictionIterator = new EvictionIterator((Deque<PooledObject<T>>)this.idleObjects);
                    }
                    if (!this.evictionIterator.hasNext()) {
                        return;
                    }
                    try {
                        underTest = this.evictionIterator.next();
                    }
                    catch (final NoSuchElementException nsee) {
                        --i;
                        this.evictionIterator = null;
                        continue;
                    }
                    if (!underTest.startEvictionTest()) {
                        --i;
                    }
                    else {
                        boolean evict;
                        try {
                            evict = evictionPolicy.evict(evictionConfig, underTest, this.idleObjects.size());
                        }
                        catch (final Throwable t) {
                            PoolUtils.checkRethrow(t);
                            this.swallowException(new Exception(t));
                            evict = false;
                        }
                        if (evict) {
                            this.destroy(underTest, DestroyMode.NORMAL);
                            this.destroyedByEvictorCount.incrementAndGet();
                        }
                        else {
                            if (testWhileIdle) {
                                boolean active = false;
                                try {
                                    this.factory.activateObject(underTest);
                                    active = true;
                                }
                                catch (final Exception e) {
                                    this.destroy(underTest, DestroyMode.NORMAL);
                                    this.destroyedByEvictorCount.incrementAndGet();
                                }
                                if (active) {
                                    if (!this.factory.validateObject(underTest)) {
                                        this.destroy(underTest, DestroyMode.NORMAL);
                                        this.destroyedByEvictorCount.incrementAndGet();
                                    }
                                    else {
                                        try {
                                            this.factory.passivateObject(underTest);
                                        }
                                        catch (final Exception e) {
                                            this.destroy(underTest, DestroyMode.NORMAL);
                                            this.destroyedByEvictorCount.incrementAndGet();
                                        }
                                    }
                                }
                            }
                            if (!underTest.endEvictionTest(this.idleObjects)) {}
                        }
                    }
                }
            }
        }
        final AbandonedConfig ac = this.abandonedConfig;
        if (ac != null && ac.getRemoveAbandonedOnMaintenance()) {
            this.removeAbandoned(ac);
        }
    }
    
    public void preparePool() throws Exception {
        if (this.getMinIdle() < 1) {
            return;
        }
        this.ensureMinIdle();
    }
    
    private PooledObject<T> create() throws Exception {
        int localMaxTotal = this.getMaxTotal();
        if (localMaxTotal < 0) {
            localMaxTotal = Integer.MAX_VALUE;
        }
        final long localStartTimeMillis = System.currentTimeMillis();
        final long localMaxWaitTimeMillis = Math.max(this.getMaxWaitMillis(), 0L);
        Boolean create;
        for (create = null; create == null; create = Boolean.FALSE) {
            synchronized (this.makeObjectCountLock) {
                final long newCreateCount = this.createCount.incrementAndGet();
                if (newCreateCount > localMaxTotal) {
                    this.createCount.decrementAndGet();
                    if (this.makeObjectCount == 0L) {
                        create = Boolean.FALSE;
                    }
                    else {
                        this.makeObjectCountLock.wait(localMaxWaitTimeMillis);
                    }
                }
                else {
                    ++this.makeObjectCount;
                    create = Boolean.TRUE;
                }
            }
            if (create == null && localMaxWaitTimeMillis > 0L && System.currentTimeMillis() - localStartTimeMillis >= localMaxWaitTimeMillis) {}
        }
        if (!create) {
            return null;
        }
        PooledObject<T> p;
        try {
            p = this.factory.makeObject();
            if (this.getTestOnCreate() && !this.factory.validateObject(p)) {
                this.createCount.decrementAndGet();
                return null;
            }
        }
        catch (final Throwable e) {
            this.createCount.decrementAndGet();
            throw e;
        }
        finally {
            synchronized (this.makeObjectCountLock) {
                --this.makeObjectCount;
                this.makeObjectCountLock.notifyAll();
            }
        }
        final AbandonedConfig ac = this.abandonedConfig;
        if (ac != null && ac.getLogAbandoned()) {
            p.setLogAbandoned(true);
            p.setRequireFullStackTrace(ac.getRequireFullStackTrace());
        }
        this.createdCount.incrementAndGet();
        this.allObjects.put(new IdentityWrapper<T>(p.getObject()), p);
        return p;
    }
    
    private void destroy(final PooledObject<T> toDestroy, final DestroyMode mode) throws Exception {
        toDestroy.invalidate();
        this.idleObjects.remove(toDestroy);
        this.allObjects.remove(new IdentityWrapper(toDestroy.getObject()));
        try {
            this.factory.destroyObject(toDestroy, mode);
        }
        finally {
            this.destroyedCount.incrementAndGet();
            this.createCount.decrementAndGet();
        }
    }
    
    @Override
    void ensureMinIdle() throws Exception {
        this.ensureIdle(this.getMinIdle(), true);
    }
    
    private void ensureIdle(final int idleCount, final boolean always) throws Exception {
        if (idleCount < 1 || this.isClosed() || (!always && !this.idleObjects.hasTakeWaiters())) {
            return;
        }
        while (this.idleObjects.size() < idleCount) {
            final PooledObject<T> p = this.create();
            if (p == null) {
                break;
            }
            if (this.getLifo()) {
                this.idleObjects.addFirst(p);
            }
            else {
                this.idleObjects.addLast(p);
            }
        }
        if (this.isClosed()) {
            this.clear();
        }
    }
    
    @Override
    public void addObject() throws Exception {
        this.assertOpen();
        if (this.factory == null) {
            throw new IllegalStateException("Cannot add objects without a factory.");
        }
        final PooledObject<T> p = this.create();
        this.addIdleObject(p);
    }
    
    @Override
    public void addObjects(final int count) throws Exception {
        for (int i = 0; i < count; ++i) {
            this.addObject();
        }
    }
    
    private void addIdleObject(final PooledObject<T> p) throws Exception {
        if (p != null) {
            this.factory.passivateObject(p);
            if (this.getLifo()) {
                this.idleObjects.addFirst(p);
            }
            else {
                this.idleObjects.addLast(p);
            }
        }
    }
    
    private int getNumTests() {
        final int numTestsPerEvictionRun = this.getNumTestsPerEvictionRun();
        if (numTestsPerEvictionRun >= 0) {
            return Math.min(numTestsPerEvictionRun, this.idleObjects.size());
        }
        return (int)Math.ceil(this.idleObjects.size() / Math.abs((double)numTestsPerEvictionRun));
    }
    
    private void removeAbandoned(final AbandonedConfig abandonedConfig) {
        final long nowMillis = System.currentTimeMillis();
        final long timeoutMillis = nowMillis - abandonedConfig.getRemoveAbandonedTimeout() * 1000L;
        final ArrayList<PooledObject<T>> remove = new ArrayList<PooledObject<T>>();
        for (final PooledObject<T> pooledObject : this.allObjects.values()) {
            synchronized (pooledObject) {
                if (pooledObject.getState() != PooledObjectState.ALLOCATED || pooledObject.getLastUsedTime() > timeoutMillis) {
                    continue;
                }
                pooledObject.markAbandoned();
                remove.add(pooledObject);
            }
        }
        for (final PooledObject<T> pooledObject : remove) {
            if (abandonedConfig.getLogAbandoned()) {
                pooledObject.printStackTrace(abandonedConfig.getLogWriter());
            }
            try {
                this.invalidateObject(pooledObject.getObject(), DestroyMode.ABANDONED);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void use(final T pooledObject) {
        final AbandonedConfig abandonedCfg = this.abandonedConfig;
        if (abandonedCfg != null && abandonedCfg.getUseUsageTracking()) {
            final PooledObject<T> wrapper = this.allObjects.get(new IdentityWrapper(pooledObject));
            wrapper.use();
        }
    }
    
    @Override
    public int getNumWaiters() {
        if (this.getBlockWhenExhausted()) {
            return this.idleObjects.getTakeQueueLength();
        }
        return 0;
    }
    
    @Override
    public String getFactoryType() {
        if (this.factoryType == null) {
            final StringBuilder result = new StringBuilder();
            result.append(this.factory.getClass().getName());
            result.append('<');
            final Class<?> pooledObjectType = PoolImplUtils.getFactoryType(this.factory.getClass());
            result.append(pooledObjectType.getName());
            result.append('>');
            this.factoryType = result.toString();
        }
        return this.factoryType;
    }
    
    @Override
    public Set<DefaultPooledObjectInfo> listAllObjects() {
        final Set<DefaultPooledObjectInfo> result = new HashSet<DefaultPooledObjectInfo>(this.allObjects.size());
        for (final PooledObject<T> p : this.allObjects.values()) {
            result.add(new DefaultPooledObjectInfo(p));
        }
        return result;
    }
    
    @Override
    protected void toStringAppendFields(final StringBuilder builder) {
        super.toStringAppendFields(builder);
        builder.append(", factoryType=");
        builder.append(this.factoryType);
        builder.append(", maxIdle=");
        builder.append(this.maxIdle);
        builder.append(", minIdle=");
        builder.append(this.minIdle);
        builder.append(", factory=");
        builder.append(this.factory);
        builder.append(", allObjects=");
        builder.append(this.allObjects);
        builder.append(", createCount=");
        builder.append(this.createCount);
        builder.append(", idleObjects=");
        builder.append(this.idleObjects);
        builder.append(", abandonedConfig=");
        builder.append(this.abandonedConfig);
    }
}
