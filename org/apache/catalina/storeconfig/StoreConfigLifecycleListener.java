package org.apache.catalina.storeconfig;

import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.ManagedBean;
import javax.management.DynamicMBean;
import org.apache.catalina.Server;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.mbeans.MBeanUtils;
import javax.management.ObjectName;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.LifecycleListener;

public class StoreConfigLifecycleListener implements LifecycleListener
{
    private static Log log;
    private static StringManager sm;
    protected final Registry registry;
    IStoreConfig storeConfig;
    private String storeConfigClass;
    private String storeRegistry;
    private ObjectName oname;
    
    public StoreConfigLifecycleListener() {
        this.registry = MBeanUtils.createRegistry();
        this.storeConfigClass = "org.apache.catalina.storeconfig.StoreConfig";
        this.storeRegistry = null;
        this.oname = null;
    }
    
    public void lifecycleEvent(final LifecycleEvent event) {
        if ("after_start".equals(event.getType())) {
            if (event.getSource() instanceof Server) {
                this.createMBean((Server)event.getSource());
            }
            else {
                StoreConfigLifecycleListener.log.warn((Object)StoreConfigLifecycleListener.sm.getString("storeConfigListener.notServer"));
            }
        }
        else if ("after_stop".equals(event.getType()) && this.oname != null) {
            this.registry.unregisterComponent(this.oname);
            this.oname = null;
        }
    }
    
    protected void createMBean(final Server server) {
        final StoreLoader loader = new StoreLoader();
        try {
            final Class<?> clazz = Class.forName(this.getStoreConfigClass(), true, this.getClass().getClassLoader());
            this.storeConfig = (IStoreConfig)clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            if (null == this.getStoreRegistry()) {
                loader.load();
            }
            else {
                loader.load(this.getStoreRegistry());
            }
            this.storeConfig.setRegistry(loader.getRegistry());
            this.storeConfig.setServer(server);
        }
        catch (final Exception e) {
            StoreConfigLifecycleListener.log.error((Object)"createMBean load", (Throwable)e);
            return;
        }
        try {
            this.oname = new ObjectName("Catalina:type=StoreConfig");
            this.registry.registerComponent((Object)this.storeConfig, this.oname, "StoreConfig");
        }
        catch (final Exception ex) {
            StoreConfigLifecycleListener.log.error((Object)"createMBean register MBean", (Throwable)ex);
        }
    }
    
    protected DynamicMBean getManagedBean(final Object object) throws Exception {
        final ManagedBean managedBean = this.registry.findManagedBean("StoreConfig");
        return managedBean.createMBean(object);
    }
    
    public IStoreConfig getStoreConfig() {
        return this.storeConfig;
    }
    
    public void setStoreConfig(final IStoreConfig storeConfig) {
        this.storeConfig = storeConfig;
    }
    
    public String getStoreConfigClass() {
        return this.storeConfigClass;
    }
    
    public void setStoreConfigClass(final String storeConfigClass) {
        this.storeConfigClass = storeConfigClass;
    }
    
    public String getStoreRegistry() {
        return this.storeRegistry;
    }
    
    public void setStoreRegistry(final String storeRegistry) {
        this.storeRegistry = storeRegistry;
    }
    
    static {
        StoreConfigLifecycleListener.log = LogFactory.getLog((Class)StoreConfigLifecycleListener.class);
        StoreConfigLifecycleListener.sm = StringManager.getManager((Class)StoreConfigLifecycleListener.class);
    }
}
