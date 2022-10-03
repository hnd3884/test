package org.apache.tomcat.jdbc.pool.interceptor;

import java.lang.reflect.Method;
import org.apache.tomcat.jdbc.pool.jmx.JmxUtil;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import javax.management.ObjectName;
import org.apache.tomcat.jdbc.pool.PooledConnection;

public class ResetAbandonedTimer extends AbstractQueryReport implements ResetAbandonedTimerMBean
{
    private PooledConnection pcon;
    private ObjectName oname;
    
    public ResetAbandonedTimer() {
        this.oname = null;
    }
    
    @Override
    public void reset(final ConnectionPool parent, final PooledConnection con) {
        super.reset(parent, con);
        if (con == null) {
            this.pcon = null;
            if (this.oname != null) {
                JmxUtil.unregisterJmx(this.oname);
                this.oname = null;
            }
        }
        else {
            this.pcon = con;
            if (this.oname == null) {
                final String keyprop = ",JdbcInterceptor=" + this.getClass().getSimpleName();
                this.oname = JmxUtil.registerJmx(this.pcon.getObjectName(), keyprop, this);
            }
        }
    }
    
    @Override
    public boolean resetTimer() {
        boolean result = false;
        if (this.pcon != null) {
            this.pcon.setTimestamp(System.currentTimeMillis());
            result = true;
        }
        return result;
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final Object result = super.invoke(proxy, method, args);
        this.resetTimer();
        return result;
    }
    
    @Override
    protected void prepareCall(final String query, final long time) {
        this.resetTimer();
    }
    
    @Override
    protected void prepareStatement(final String sql, final long time) {
        this.resetTimer();
    }
    
    @Override
    public void closeInvoked() {
        this.resetTimer();
    }
    
    @Override
    protected String reportQuery(final String query, final Object[] args, final String name, final long start, final long delta) {
        this.resetTimer();
        return super.reportQuery(query, args, name, start, delta);
    }
    
    @Override
    protected String reportSlowQuery(final String query, final Object[] args, final String name, final long start, final long delta) {
        this.resetTimer();
        return super.reportSlowQuery(query, args, name, start, delta);
    }
}
