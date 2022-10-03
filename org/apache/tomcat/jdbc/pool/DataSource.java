package org.apache.tomcat.jdbc.pool;

import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.jmx.JmxUtil;
import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import javax.sql.ConnectionPoolDataSource;
import org.apache.tomcat.jdbc.pool.jmx.ConnectionPoolMBean;
import javax.management.MBeanRegistration;

public class DataSource extends DataSourceProxy implements javax.sql.DataSource, MBeanRegistration, ConnectionPoolMBean, ConnectionPoolDataSource
{
    private static final Log log;
    protected volatile ObjectName oname;
    
    public DataSource() {
        this.oname = null;
    }
    
    public DataSource(final PoolConfiguration poolProperties) {
        super(poolProperties);
        this.oname = null;
    }
    
    @Override
    public void postDeregister() {
        if (this.oname != null) {
            this.unregisterJmx();
        }
    }
    
    @Override
    public void postRegister(final Boolean registrationDone) {
    }
    
    @Override
    public void preDeregister() throws Exception {
    }
    
    @Override
    public ObjectName preRegister(final MBeanServer server, final ObjectName name) throws Exception {
        try {
            if (this.isJmxEnabled()) {
                this.oname = this.createObjectName(name);
                if (this.oname != null) {
                    this.registerJmx();
                }
            }
        }
        catch (final MalformedObjectNameException x) {
            DataSource.log.error((Object)"Unable to create object name for JDBC pool.", (Throwable)x);
        }
        return name;
    }
    
    public ObjectName createObjectName(final ObjectName original) throws MalformedObjectNameException {
        final String domain = "tomcat.jdbc";
        final Hashtable<String, String> properties = original.getKeyPropertyList();
        final String origDomain = original.getDomain();
        properties.put("type", "ConnectionPool");
        properties.put("class", this.getClass().getName());
        if (original.getKeyProperty("path") != null || properties.get("context") != null) {
            properties.put("engine", origDomain);
        }
        final ObjectName name = new ObjectName(domain, properties);
        return name;
    }
    
    protected void registerJmx() {
        if (this.pool.getJmxPool() != null) {
            JmxUtil.registerJmx(this.oname, null, this.pool.getJmxPool());
        }
    }
    
    protected void unregisterJmx() {
        JmxUtil.unregisterJmx(this.oname);
    }
    
    static {
        log = LogFactory.getLog((Class)DataSource.class);
    }
}
