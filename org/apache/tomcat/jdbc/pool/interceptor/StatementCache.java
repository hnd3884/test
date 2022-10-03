package org.apache.tomcat.jdbc.pool.interceptor;

import java.util.Arrays;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.juli.logging.LogFactory;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import org.apache.tomcat.jdbc.pool.jmx.JmxUtil;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.ObjectName;
import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.apache.juli.logging.Log;

public class StatementCache extends StatementDecoratorInterceptor implements StatementCacheMBean
{
    private static final Log log;
    protected static final String[] ALL_TYPES;
    protected static final String[] CALLABLE_TYPE;
    protected static final String[] PREPARED_TYPE;
    protected static final String[] NO_TYPE;
    protected static final String STATEMENT_CACHE_ATTR;
    private boolean cachePrepared;
    private boolean cacheCallable;
    private int maxCacheSize;
    private PooledConnection pcon;
    private String[] types;
    private ObjectName oname;
    private static ConcurrentHashMap<ConnectionPool, AtomicInteger> cacheSizeMap;
    private AtomicInteger cacheSize;
    
    public StatementCache() {
        this.cachePrepared = true;
        this.cacheCallable = false;
        this.maxCacheSize = 50;
        this.oname = null;
    }
    
    @Override
    public boolean isCachePrepared() {
        return this.cachePrepared;
    }
    
    @Override
    public boolean isCacheCallable() {
        return this.cacheCallable;
    }
    
    @Override
    public int getMaxCacheSize() {
        return this.maxCacheSize;
    }
    
    public String[] getTypes() {
        return this.types;
    }
    
    @Override
    public AtomicInteger getCacheSize() {
        return this.cacheSize;
    }
    
    @Override
    public void setProperties(final Map<String, PoolProperties.InterceptorProperty> properties) {
        super.setProperties(properties);
        PoolProperties.InterceptorProperty p = properties.get("prepared");
        if (p != null) {
            this.cachePrepared = p.getValueAsBoolean(this.cachePrepared);
        }
        p = properties.get("callable");
        if (p != null) {
            this.cacheCallable = p.getValueAsBoolean(this.cacheCallable);
        }
        p = properties.get("max");
        if (p != null) {
            this.maxCacheSize = p.getValueAsInt(this.maxCacheSize);
        }
        if (this.cachePrepared && this.cacheCallable) {
            this.types = StatementCache.ALL_TYPES;
        }
        else if (this.cachePrepared) {
            this.types = StatementCache.PREPARED_TYPE;
        }
        else if (this.cacheCallable) {
            this.types = StatementCache.CALLABLE_TYPE;
        }
        else {
            this.types = StatementCache.NO_TYPE;
        }
    }
    
    @Override
    public void poolStarted(final ConnectionPool pool) {
        StatementCache.cacheSizeMap.putIfAbsent(pool, new AtomicInteger(0));
        super.poolStarted(pool);
    }
    
    @Override
    public void poolClosed(final ConnectionPool pool) {
        StatementCache.cacheSizeMap.remove(pool);
        super.poolClosed(pool);
    }
    
    @Override
    public void reset(final ConnectionPool parent, final PooledConnection con) {
        super.reset(parent, con);
        if (parent == null) {
            this.cacheSize = null;
            this.pcon = null;
            if (this.oname != null) {
                JmxUtil.unregisterJmx(this.oname);
                this.oname = null;
            }
        }
        else {
            this.cacheSize = StatementCache.cacheSizeMap.get(parent);
            this.pcon = con;
            if (!this.pcon.getAttributes().containsKey(StatementCache.STATEMENT_CACHE_ATTR)) {
                final ConcurrentHashMap<CacheKey, CachedStatement> cache = new ConcurrentHashMap<CacheKey, CachedStatement>();
                this.pcon.getAttributes().put(StatementCache.STATEMENT_CACHE_ATTR, cache);
            }
            if (this.oname == null) {
                final String keyprop = ",JdbcInterceptor=" + this.getClass().getSimpleName();
                this.oname = JmxUtil.registerJmx(this.pcon.getObjectName(), keyprop, this);
            }
        }
    }
    
    @Override
    public void disconnected(final ConnectionPool parent, final PooledConnection con, final boolean finalizing) {
        final ConcurrentHashMap<CacheKey, CachedStatement> statements = con.getAttributes().get(StatementCache.STATEMENT_CACHE_ATTR);
        if (statements != null) {
            for (final Map.Entry<CacheKey, CachedStatement> p : statements.entrySet()) {
                this.closeStatement(p.getValue());
            }
            statements.clear();
        }
        super.disconnected(parent, con, finalizing);
    }
    
    public void closeStatement(final CachedStatement st) {
        if (st == null) {
            return;
        }
        st.forceClose();
    }
    
    @Override
    protected Object createDecorator(final Object proxy, final Method method, final Object[] args, final Object statement, final Constructor<?> constructor, final String sql) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        final boolean process = this.process(this.types, method, false);
        if (process) {
            Object result = null;
            final CachedStatement statementProxy = new CachedStatement((PreparedStatement)statement, sql);
            result = constructor.newInstance(statementProxy);
            statementProxy.setActualProxy(result);
            statementProxy.setConnection(proxy);
            statementProxy.setConstructor(constructor);
            statementProxy.setCacheKey(this.createCacheKey(method, args));
            return result;
        }
        return super.createDecorator(proxy, method, args, statement, constructor, sql);
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final boolean process = this.process(this.types, method, false);
        if (!process || args.length <= 0 || !(args[0] instanceof String)) {
            return super.invoke(proxy, method, args);
        }
        final CachedStatement statement = this.isCached(method, args);
        if (statement != null) {
            this.removeStatement(statement);
            return statement.getActualProxy();
        }
        return super.invoke(proxy, method, args);
    }
    
    @Deprecated
    public CachedStatement isCached(final String sql) {
        return null;
    }
    
    public CachedStatement isCached(final Method method, final Object[] args) {
        final ConcurrentHashMap<CacheKey, CachedStatement> cache = this.getCache();
        if (cache == null) {
            return null;
        }
        return cache.get(this.createCacheKey(method, args));
    }
    
    public boolean cacheStatement(final CachedStatement proxy) {
        final ConcurrentHashMap<CacheKey, CachedStatement> cache = this.getCache();
        if (cache == null) {
            return false;
        }
        if (proxy.getCacheKey() == null) {
            return false;
        }
        if (cache.containsKey(proxy.getCacheKey())) {
            return false;
        }
        if (this.cacheSize.get() >= this.maxCacheSize) {
            return false;
        }
        if (this.cacheSize.incrementAndGet() > this.maxCacheSize) {
            this.cacheSize.decrementAndGet();
            return false;
        }
        cache.put(proxy.getCacheKey(), proxy);
        return true;
    }
    
    public boolean removeStatement(final CachedStatement proxy) {
        final ConcurrentHashMap<CacheKey, CachedStatement> cache = this.getCache();
        if (cache == null) {
            return false;
        }
        if (cache.remove(proxy.getCacheKey()) != null) {
            this.cacheSize.decrementAndGet();
            return true;
        }
        return false;
    }
    
    protected ConcurrentHashMap<CacheKey, CachedStatement> getCache() {
        final PooledConnection pCon = this.pcon;
        if (pCon == null) {
            if (StatementCache.log.isWarnEnabled()) {
                StatementCache.log.warn((Object)"Connection has already been closed or abandoned");
            }
            return null;
        }
        final ConcurrentHashMap<CacheKey, CachedStatement> cache = pCon.getAttributes().get(StatementCache.STATEMENT_CACHE_ATTR);
        return cache;
    }
    
    @Override
    public int getCacheSizePerConnection() {
        final ConcurrentHashMap<CacheKey, CachedStatement> cache = this.getCache();
        if (cache == null) {
            return 0;
        }
        return cache.size();
    }
    
    protected CacheKey createCacheKey(final Method method, final Object[] args) {
        return this.createCacheKey(method.getName(), args);
    }
    
    protected CacheKey createCacheKey(final String methodName, final Object[] args) {
        CacheKey key = null;
        if (this.compare("prepareStatement", methodName)) {
            key = new CacheKey("prepareStatement", args);
        }
        else if (this.compare("prepareCall", methodName)) {
            key = new CacheKey("prepareCall", args);
        }
        return key;
    }
    
    static {
        log = LogFactory.getLog((Class)StatementCache.class);
        ALL_TYPES = new String[] { "prepareStatement", "prepareCall" };
        CALLABLE_TYPE = new String[] { "prepareCall" };
        PREPARED_TYPE = new String[] { "prepareStatement" };
        NO_TYPE = new String[0];
        STATEMENT_CACHE_ATTR = StatementCache.class.getName() + ".cache";
        StatementCache.cacheSizeMap = new ConcurrentHashMap<ConnectionPool, AtomicInteger>();
    }
    
    protected class CachedStatement extends StatementProxy<PreparedStatement>
    {
        boolean cached;
        CacheKey key;
        
        public CachedStatement(final PreparedStatement parent, final String sql) {
            super(parent, sql);
            this.cached = false;
        }
        
        @Override
        public void closeInvoked() {
            boolean shouldClose = true;
            if (StatementCache.this.cacheSize.get() < StatementCache.this.maxCacheSize) {
                final CachedStatement proxy = new CachedStatement(this.getDelegate(), this.getSql());
                proxy.setCacheKey(this.getCacheKey());
                try {
                    final ResultSet result = this.getDelegate().getResultSet();
                    if (result != null && !result.isClosed()) {
                        result.close();
                    }
                    this.getDelegate().clearParameters();
                    final Object actualProxy = this.getConstructor().newInstance(proxy);
                    proxy.setActualProxy(actualProxy);
                    proxy.setConnection(this.getConnection());
                    proxy.setConstructor(this.getConstructor());
                    if (StatementCache.this.cacheStatement(proxy)) {
                        proxy.cached = true;
                        shouldClose = false;
                    }
                }
                catch (final RuntimeException | ReflectiveOperationException | SQLException x) {
                    StatementCache.this.removeStatement(proxy);
                }
            }
            if (shouldClose) {
                super.closeInvoked();
            }
            this.closed = true;
            this.delegate = null;
        }
        
        public void forceClose() {
            StatementCache.this.removeStatement(this);
            super.closeInvoked();
        }
        
        public CacheKey getCacheKey() {
            return this.key;
        }
        
        public void setCacheKey(final CacheKey cacheKey) {
            this.key = cacheKey;
        }
    }
    
    private static final class CacheKey
    {
        private final String stmtType;
        private final Object[] args;
        
        private CacheKey(final String type, final Object[] methodArgs) {
            this.stmtType = type;
            this.args = methodArgs;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + Arrays.deepHashCode(this.args);
            result = 31 * result + ((this.stmtType == null) ? 0 : this.stmtType.hashCode());
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final CacheKey other = (CacheKey)obj;
            if (!Arrays.deepEquals(this.args, other.args)) {
                return false;
            }
            if (this.stmtType == null) {
                if (other.stmtType != null) {
                    return false;
                }
            }
            else if (!this.stmtType.equals(other.stmtType)) {
                return false;
            }
            return true;
        }
    }
}
