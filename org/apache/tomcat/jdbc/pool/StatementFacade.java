package org.apache.tomcat.jdbc.pool;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import org.apache.juli.logging.LogFactory;
import java.lang.reflect.Constructor;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.lang.reflect.Method;
import org.apache.juli.logging.Log;
import org.apache.tomcat.jdbc.pool.interceptor.AbstractCreateStatementInterceptor;

public class StatementFacade extends AbstractCreateStatementInterceptor
{
    private static final Log logger;
    
    protected StatementFacade(final JdbcInterceptor interceptor) {
        this.setUseEquals(interceptor.isUseEquals());
        this.setNext(interceptor);
    }
    
    @Override
    public void closeInvoked() {
    }
    
    @Override
    public Object createStatement(final Object proxy, final Method method, final Object[] args, final Object statement, final long time) {
        try {
            final String name = method.getName();
            Constructor<?> constructor = null;
            String sql = null;
            if (this.compare("createStatement", name)) {
                constructor = this.getConstructor(0, Statement.class);
            }
            else if (this.compare("prepareStatement", name)) {
                constructor = this.getConstructor(1, PreparedStatement.class);
                sql = (String)args[0];
            }
            else {
                if (!this.compare("prepareCall", name)) {
                    return statement;
                }
                constructor = this.getConstructor(2, CallableStatement.class);
                sql = (String)args[0];
            }
            return constructor.newInstance(new StatementProxy(statement, sql));
        }
        catch (final Exception x) {
            StatementFacade.logger.warn((Object)"Unable to create statement proxy.", (Throwable)x);
            return statement;
        }
    }
    
    static {
        logger = LogFactory.getLog((Class)StatementFacade.class);
    }
    
    protected class StatementProxy implements InvocationHandler
    {
        protected boolean closed;
        protected Object delegate;
        protected final String query;
        
        public StatementProxy(final Object parent, final String query) {
            this.closed = false;
            this.delegate = parent;
            this.query = query;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (StatementFacade.this.compare("toString", method)) {
                return this.toString();
            }
            if (StatementFacade.this.compare("equals", method)) {
                return this.equals(Proxy.getInvocationHandler(args[0]));
            }
            if (StatementFacade.this.compare("hashCode", method)) {
                return this.hashCode();
            }
            if (StatementFacade.this.compare("close", method) && this.delegate == null) {
                return null;
            }
            if (StatementFacade.this.compare("isClosed", method) && this.delegate == null) {
                return Boolean.TRUE;
            }
            if (this.delegate == null) {
                throw new SQLException("Statement closed.");
            }
            Object result = null;
            try {
                result = method.invoke(this.delegate, args);
            }
            catch (final Throwable t) {
                if (t instanceof InvocationTargetException && t.getCause() != null) {
                    throw t.getCause();
                }
                throw t;
            }
            if (StatementFacade.this.compare("close", method)) {
                this.delegate = null;
            }
            return result;
        }
        
        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
        
        @Override
        public boolean equals(final Object obj) {
            return this == obj;
        }
        
        @Override
        public String toString() {
            final StringBuffer buf = new StringBuffer(StatementProxy.class.getName());
            buf.append("[Proxy=");
            buf.append(this.hashCode());
            buf.append("; Query=");
            buf.append(this.query);
            buf.append("; Delegate=");
            buf.append(this.delegate);
            buf.append("]");
            return buf.toString();
        }
    }
}
