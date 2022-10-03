package com.microsoft.sqlserver.jdbc.osgi;

import java.util.Dictionary;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import java.util.Hashtable;
import org.osgi.framework.BundleContext;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.BundleActivator;

public class Activator implements BundleActivator
{
    private ServiceRegistration<DataSourceFactory> service;
    
    public void start(final BundleContext context) throws Exception {
        final Dictionary<String, Object> properties = new Hashtable<String, Object>();
        final SQLServerDriver driver = new SQLServerDriver();
        properties.put("osgi.jdbc.driver.class", driver.getClass().getName());
        properties.put("osgi.jdbc.driver.name", "Microsoft JDBC Driver for SQL Server");
        properties.put("osgi.jdbc.driver.version", driver.getMajorVersion() + "." + driver.getMinorVersion());
        this.service = (ServiceRegistration<DataSourceFactory>)context.registerService((Class)DataSourceFactory.class, (Object)new SQLServerDataSourceFactory(), (Dictionary)properties);
        SQLServerDriver.register();
    }
    
    public void stop(final BundleContext context) throws Exception {
        if (this.service != null) {
            this.service.unregister();
        }
        SQLServerDriver.deregister();
    }
}
