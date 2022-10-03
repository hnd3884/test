package org.postgresql.osgi;

import java.util.Dictionary;
import org.osgi.service.jdbc.DataSourceFactory;
import org.postgresql.Driver;
import java.util.Hashtable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.BundleActivator;

public class PGBundleActivator implements BundleActivator
{
    private ServiceRegistration<?> registration;
    
    public void start(final BundleContext context) throws Exception {
        final Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put("osgi.jdbc.driver.class", Driver.class.getName());
        properties.put("osgi.jdbc.driver.name", "PostgreSQL JDBC Driver");
        properties.put("osgi.jdbc.driver.version", "42.2.19");
        try {
            this.registration = (ServiceRegistration<?>)context.registerService(DataSourceFactory.class.getName(), (Object)new PGDataSourceFactory(), (Dictionary)properties);
        }
        catch (final NoClassDefFoundError e) {
            final String msg = e.getMessage();
            if (msg == null || !msg.contains("org/osgi/service/jdbc/DataSourceFactory")) {
                throw e;
            }
            if (!Boolean.getBoolean("pgjdbc.osgi.debug")) {
                return;
            }
            new IllegalArgumentException("Unable to load DataSourceFactory. Will ignore DataSourceFactory registration. If you need one, ensure org.osgi.enterprise is on the classpath", e).printStackTrace();
        }
    }
    
    public void stop(final BundleContext context) throws Exception {
        if (this.registration != null) {
            this.registration.unregister();
            this.registration = null;
        }
        if (Driver.isRegistered()) {
            Driver.deregister();
        }
    }
}
