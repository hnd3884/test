package org.apache.tomcat.jdbc.pool.interceptor;

import org.apache.tomcat.jdbc.pool.PooledConnection;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;

public abstract class AbstractCreateStatementInterceptor extends JdbcInterceptor
{
    protected static final String CREATE_STATEMENT = "createStatement";
    protected static final int CREATE_STATEMENT_IDX = 0;
    protected static final String PREPARE_STATEMENT = "prepareStatement";
    protected static final int PREPARE_STATEMENT_IDX = 1;
    protected static final String PREPARE_CALL = "prepareCall";
    protected static final int PREPARE_CALL_IDX = 2;
    protected static final String[] STATEMENT_TYPES;
    protected static final int STATEMENT_TYPE_COUNT;
    protected static final String EXECUTE = "execute";
    protected static final String EXECUTE_QUERY = "executeQuery";
    protected static final String EXECUTE_UPDATE = "executeUpdate";
    protected static final String EXECUTE_BATCH = "executeBatch";
    protected static final String[] EXECUTE_TYPES;
    protected static final Constructor<?>[] constructors;
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (this.compare("close", method)) {
            this.closeInvoked();
            return super.invoke(proxy, method, args);
        }
        boolean process = false;
        process = this.isStatement(method, process);
        if (process) {
            final long start = System.currentTimeMillis();
            final Object statement = super.invoke(proxy, method, args);
            final long delta = System.currentTimeMillis() - start;
            return this.createStatement(proxy, method, args, statement, delta);
        }
        return super.invoke(proxy, method, args);
    }
    
    protected Constructor<?> getConstructor(final int idx, final Class<?> clazz) throws NoSuchMethodException {
        if (AbstractCreateStatementInterceptor.constructors[idx] == null) {
            final Class<?> proxyClass = Proxy.getProxyClass(AbstractCreateStatementInterceptor.class.getClassLoader(), clazz);
            AbstractCreateStatementInterceptor.constructors[idx] = proxyClass.getConstructor(InvocationHandler.class);
        }
        return AbstractCreateStatementInterceptor.constructors[idx];
    }
    
    public abstract Object createStatement(final Object p0, final Method p1, final Object[] p2, final Object p3, final long p4);
    
    public abstract void closeInvoked();
    
    protected boolean isStatement(final Method method, final boolean process) {
        return this.process(AbstractCreateStatementInterceptor.STATEMENT_TYPES, method, process);
    }
    
    protected boolean isExecute(final Method method, final boolean process) {
        return this.process(AbstractCreateStatementInterceptor.EXECUTE_TYPES, method, process);
    }
    
    protected boolean process(final String[] names, final Method method, boolean process) {
        final String name = method.getName();
        for (int i = 0; !process && i < names.length; process = this.compare(names[i], name), ++i) {}
        return process;
    }
    
    @Override
    public void reset(final ConnectionPool parent, final PooledConnection con) {
    }
    
    static {
        STATEMENT_TYPES = new String[] { "createStatement", "prepareStatement", "prepareCall" };
        STATEMENT_TYPE_COUNT = AbstractCreateStatementInterceptor.STATEMENT_TYPES.length;
        EXECUTE_TYPES = new String[] { "execute", "executeQuery", "executeUpdate", "executeBatch" };
        constructors = new Constructor[AbstractCreateStatementInterceptor.STATEMENT_TYPE_COUNT];
    }
}
