package com.zoho.clustering.agent.remotemonitor;

import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import com.zoho.clustering.util.ClassUtil;
import java.util.HashMap;
import java.util.logging.Level;
import com.zoho.clustering.util.MyProperties;
import java.util.Map;
import java.util.logging.Logger;

public class MonitorPool
{
    private static Logger logger;
    private static MonitorPool inst;
    private Config config;
    private Map<String, Monitor> monitors;
    
    public static MonitorPool getInst() {
        if (MonitorPool.inst == null) {
            throw new IllegalStateException("RemoteMonitorModule is not initialized");
        }
        return MonitorPool.inst;
    }
    
    public static boolean isEnabled() {
        return MonitorPool.inst != null;
    }
    
    public static void initialize(final MyProperties props) {
        initialize("clustering.remotemonitor", props);
    }
    
    public static void initialize(final String prefix, final MyProperties props) {
        if (MonitorPool.inst != null) {
            throw new IllegalStateException("RemoteMonitorModule is already initialized");
        }
        final int keepAliveInMins = props.intValue(prefix + ".keepAliveInMins");
        final String handlerClassName = props.value(prefix + ".handlerClass");
        MonitorPool.inst = new MonitorPool(new Config(keepAliveInMins, handlerClassName));
        MonitorPool.logger.log(Level.INFO, "MonitorPool: initialized");
    }
    
    public MonitorPool(final Config config) {
        this.monitors = new HashMap<String, Monitor>();
        this.config = config;
    }
    
    public Config config() {
        return this.config;
    }
    
    public Monitor getOrCreate(final String slaveId) {
        Monitor monitor = this.monitors.get(slaveId);
        if (monitor == null) {
            monitor = new Monitor(slaveId, this.config.keepAliveInMins, (Monitor.Handler)ClassUtil.New(this.config.handlerClassName));
            this.monitors.put(slaveId, monitor);
            MonitorPool.logger.log(Level.INFO, "MonitorPool: Monitor [{0}] added", slaveId);
            monitor.start();
        }
        return monitor;
    }
    
    public Monitor getMonitor(final String slaveId) {
        final Monitor monitor = this.monitors.get(slaveId);
        if (monitor == null) {
            throw new IllegalArgumentException("No monitor registered with id [" + slaveId + "]");
        }
        return monitor;
    }
    
    public void removeMonitor(final String slaveId) {
        final Monitor monitor = this.monitors.remove(slaveId);
        if (monitor == null) {
            throw new IllegalArgumentException("No monitor registered with id [" + slaveId + "]");
        }
        monitor.stop();
        MonitorPool.logger.log(Level.INFO, "MonitorPool: Monitor [{0}] removed", slaveId);
    }
    
    public Collection<Monitor> getAll() {
        return Collections.unmodifiableCollection((Collection<? extends Monitor>)this.monitors.values());
    }
    
    public void removeAll() {
        for (final Monitor monitor : this.monitors.values()) {
            monitor.stop();
        }
        this.monitors.clear();
        MonitorPool.logger.log(Level.INFO, "MonitorPool: removed all monitors");
    }
    
    static {
        MonitorPool.logger = Logger.getLogger(Monitor.class.getName());
        MonitorPool.inst = null;
    }
    
    public static class Config
    {
        public final int keepAliveInMins;
        public final String handlerClassName;
        
        public Config(final int keepAliveInMins, final String handlerClassName) {
            this.keepAliveInMins = keepAliveInMins;
            this.handlerClassName = handlerClassName;
        }
    }
}
