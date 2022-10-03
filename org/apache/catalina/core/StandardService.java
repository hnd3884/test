package org.apache.catalina.core;

import org.apache.juli.logging.LogFactory;
import org.apache.catalina.Container;
import org.apache.catalina.JmxEnabled;
import org.apache.catalina.LifecycleState;
import java.util.Iterator;
import java.beans.PropertyChangeListener;
import javax.management.ObjectName;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.mapper.MapperListener;
import org.apache.catalina.mapper.Mapper;
import org.apache.catalina.Engine;
import org.apache.catalina.Executor;
import java.util.ArrayList;
import org.apache.catalina.connector.Connector;
import java.beans.PropertyChangeSupport;
import org.apache.catalina.Server;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.Service;
import org.apache.catalina.util.LifecycleMBeanBase;

public class StandardService extends LifecycleMBeanBase implements Service
{
    private static final Log log;
    private static final StringManager sm;
    private String name;
    private Server server;
    protected final PropertyChangeSupport support;
    protected Connector[] connectors;
    private final Object connectorsLock;
    protected final ArrayList<Executor> executors;
    private Engine engine;
    private ClassLoader parentClassLoader;
    protected final Mapper mapper;
    protected final MapperListener mapperListener;
    
    public StandardService() {
        this.name = null;
        this.server = null;
        this.support = new PropertyChangeSupport(this);
        this.connectors = new Connector[0];
        this.connectorsLock = new Object();
        this.executors = new ArrayList<Executor>();
        this.engine = null;
        this.parentClassLoader = null;
        this.mapper = new Mapper();
        this.mapperListener = new MapperListener(this);
    }
    
    @Override
    public Mapper getMapper() {
        return this.mapper;
    }
    
    @Override
    public Engine getContainer() {
        return this.engine;
    }
    
    @Override
    public void setContainer(final Engine engine) {
        final Engine oldEngine = this.engine;
        if (oldEngine != null) {
            oldEngine.setService(null);
        }
        this.engine = engine;
        if (this.engine != null) {
            this.engine.setService(this);
        }
        if (this.getState().isAvailable()) {
            if (this.engine != null) {
                try {
                    this.engine.start();
                }
                catch (final LifecycleException e) {
                    StandardService.log.error((Object)StandardService.sm.getString("standardService.engine.startFailed"), (Throwable)e);
                }
            }
            try {
                this.mapperListener.stop();
            }
            catch (final LifecycleException e) {
                StandardService.log.error((Object)StandardService.sm.getString("standardService.mapperListener.stopFailed"), (Throwable)e);
            }
            try {
                this.mapperListener.start();
            }
            catch (final LifecycleException e) {
                StandardService.log.error((Object)StandardService.sm.getString("standardService.mapperListener.startFailed"), (Throwable)e);
            }
            if (oldEngine != null) {
                try {
                    oldEngine.stop();
                }
                catch (final LifecycleException e) {
                    StandardService.log.error((Object)StandardService.sm.getString("standardService.engine.stopFailed"), (Throwable)e);
                }
            }
        }
        this.support.firePropertyChange("container", oldEngine, this.engine);
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public Server getServer() {
        return this.server;
    }
    
    @Override
    public void setServer(final Server server) {
        this.server = server;
    }
    
    @Override
    public void addConnector(final Connector connector) {
        synchronized (this.connectorsLock) {
            connector.setService(this);
            final Connector[] results = new Connector[this.connectors.length + 1];
            System.arraycopy(this.connectors, 0, results, 0, this.connectors.length);
            results[this.connectors.length] = connector;
            this.connectors = results;
            if (this.getState().isAvailable()) {
                try {
                    connector.start();
                }
                catch (final LifecycleException e) {
                    StandardService.log.error((Object)StandardService.sm.getString("standardService.connector.startFailed", new Object[] { connector }), (Throwable)e);
                }
            }
            this.support.firePropertyChange("connector", null, connector);
        }
    }
    
    public ObjectName[] getConnectorNames() {
        final ObjectName[] results = new ObjectName[this.connectors.length];
        for (int i = 0; i < results.length; ++i) {
            results[i] = this.connectors[i].getObjectName();
        }
        return results;
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }
    
    @Override
    public Connector[] findConnectors() {
        return this.connectors;
    }
    
    @Override
    public void removeConnector(final Connector connector) {
        synchronized (this.connectorsLock) {
            int j = -1;
            for (int i = 0; i < this.connectors.length; ++i) {
                if (connector == this.connectors[i]) {
                    j = i;
                    break;
                }
            }
            if (j < 0) {
                return;
            }
            if (this.connectors[j].getState().isAvailable()) {
                try {
                    this.connectors[j].stop();
                }
                catch (final LifecycleException e) {
                    StandardService.log.error((Object)StandardService.sm.getString("standardService.connector.stopFailed", new Object[] { this.connectors[j] }), (Throwable)e);
                }
            }
            connector.setService(null);
            int k = 0;
            final Connector[] results = new Connector[this.connectors.length - 1];
            for (int l = 0; l < this.connectors.length; ++l) {
                if (l != j) {
                    results[k++] = this.connectors[l];
                }
            }
            this.connectors = results;
            this.support.firePropertyChange("connector", connector, null);
        }
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StandardService[");
        sb.append(this.getName());
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public void addExecutor(final Executor ex) {
        synchronized (this.executors) {
            if (!this.executors.contains(ex)) {
                this.executors.add(ex);
                if (this.getState().isAvailable()) {
                    try {
                        ex.start();
                    }
                    catch (final LifecycleException x) {
                        StandardService.log.error((Object)StandardService.sm.getString("standardService.executor.start"), (Throwable)x);
                    }
                }
            }
        }
    }
    
    @Override
    public Executor[] findExecutors() {
        synchronized (this.executors) {
            final Executor[] arr = new Executor[this.executors.size()];
            this.executors.toArray(arr);
            return arr;
        }
    }
    
    @Override
    public Executor getExecutor(final String executorName) {
        synchronized (this.executors) {
            for (final Executor executor : this.executors) {
                if (executorName.equals(executor.getName())) {
                    return executor;
                }
            }
        }
        return null;
    }
    
    @Override
    public void removeExecutor(final Executor ex) {
        synchronized (this.executors) {
            if (this.executors.remove(ex) && this.getState().isAvailable()) {
                try {
                    ex.stop();
                }
                catch (final LifecycleException e) {
                    StandardService.log.error((Object)StandardService.sm.getString("standardService.executor.stop"), (Throwable)e);
                }
            }
        }
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        if (StandardService.log.isInfoEnabled()) {
            StandardService.log.info((Object)StandardService.sm.getString("standardService.start.name", new Object[] { this.name }));
        }
        this.setState(LifecycleState.STARTING);
        if (this.engine != null) {
            synchronized (this.engine) {
                this.engine.start();
            }
        }
        synchronized (this.executors) {
            for (final Executor executor : this.executors) {
                executor.start();
            }
        }
        this.mapperListener.start();
        synchronized (this.connectorsLock) {
            for (final Connector connector : this.connectors) {
                try {
                    if (connector.getState() != LifecycleState.FAILED) {
                        connector.start();
                    }
                }
                catch (final Exception e) {
                    StandardService.log.error((Object)StandardService.sm.getString("standardService.connector.startFailed", new Object[] { connector }), (Throwable)e);
                }
            }
        }
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        synchronized (this.connectorsLock) {
            for (final Connector connector : this.connectors) {
                try {
                    connector.pause();
                }
                catch (final Exception e) {
                    StandardService.log.error((Object)StandardService.sm.getString("standardService.connector.pauseFailed", new Object[] { connector }), (Throwable)e);
                }
                connector.getProtocolHandler().closeServerSocketGraceful();
            }
        }
        if (StandardService.log.isInfoEnabled()) {
            StandardService.log.info((Object)StandardService.sm.getString("standardService.stop.name", new Object[] { this.name }));
        }
        this.setState(LifecycleState.STOPPING);
        if (this.engine != null) {
            synchronized (this.engine) {
                this.engine.stop();
            }
        }
        synchronized (this.connectorsLock) {
            for (final Connector connector : this.connectors) {
                if (LifecycleState.STARTED.equals(connector.getState())) {
                    try {
                        connector.stop();
                    }
                    catch (final Exception e) {
                        StandardService.log.error((Object)StandardService.sm.getString("standardService.connector.stopFailed", new Object[] { connector }), (Throwable)e);
                    }
                }
            }
        }
        if (this.mapperListener.getState() != LifecycleState.INITIALIZED) {
            this.mapperListener.stop();
        }
        synchronized (this.executors) {
            for (final Executor executor : this.executors) {
                executor.stop();
            }
        }
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        if (this.engine != null) {
            this.engine.init();
        }
        for (final Executor executor : this.findExecutors()) {
            if (executor instanceof JmxEnabled) {
                ((JmxEnabled)executor).setDomain(this.getDomain());
            }
            executor.init();
        }
        this.mapperListener.init();
        synchronized (this.connectorsLock) {
            for (final Connector connector : this.connectors) {
                try {
                    connector.init();
                }
                catch (final Exception e) {
                    final String message = StandardService.sm.getString("standardService.connector.initFailed", new Object[] { connector });
                    StandardService.log.error((Object)message, (Throwable)e);
                    if (Boolean.getBoolean("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE")) {
                        throw new LifecycleException(message);
                    }
                }
            }
        }
    }
    
    @Override
    protected void destroyInternal() throws LifecycleException {
        this.mapperListener.destroy();
        synchronized (this.connectorsLock) {
            for (final Connector connector : this.connectors) {
                try {
                    connector.destroy();
                }
                catch (final Exception e) {
                    StandardService.log.error((Object)StandardService.sm.getString("standardService.connector.destroyFailed", new Object[] { connector }), (Throwable)e);
                }
            }
        }
        for (final Executor executor : this.findExecutors()) {
            executor.destroy();
        }
        if (this.engine != null) {
            this.engine.destroy();
        }
        super.destroyInternal();
    }
    
    @Override
    public ClassLoader getParentClassLoader() {
        if (this.parentClassLoader != null) {
            return this.parentClassLoader;
        }
        if (this.server != null) {
            return this.server.getParentClassLoader();
        }
        return ClassLoader.getSystemClassLoader();
    }
    
    @Override
    public void setParentClassLoader(final ClassLoader parent) {
        final ClassLoader oldParentClassLoader = this.parentClassLoader;
        this.parentClassLoader = parent;
        this.support.firePropertyChange("parentClassLoader", oldParentClassLoader, this.parentClassLoader);
    }
    
    @Override
    protected String getDomainInternal() {
        String domain = null;
        final Container engine = this.getContainer();
        if (engine != null) {
            domain = engine.getName();
        }
        if (domain == null) {
            domain = this.getName();
        }
        return domain;
    }
    
    public final String getObjectNameKeyProperties() {
        return "type=Service";
    }
    
    static {
        log = LogFactory.getLog((Class)StandardService.class);
        sm = StringManager.getManager((Class)StandardService.class);
    }
}
