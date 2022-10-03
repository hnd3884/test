package org.apache.catalina.ha.session;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Cluster;
import org.apache.catalina.Manager;
import org.apache.catalina.Valve;
import org.apache.catalina.SessionIdGenerator;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.catalina.tribes.io.ReplicationStream;
import org.apache.catalina.Loader;
import org.apache.catalina.Context;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.catalina.ha.tcp.ReplicationValve;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.juli.logging.Log;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.session.ManagerBase;

public abstract class ClusterManagerBase extends ManagerBase implements ClusterManager
{
    private final Log log;
    protected CatalinaCluster cluster;
    private boolean notifyListenersOnReplication;
    private volatile ReplicationValve replicationValve;
    private boolean recordAllActions;
    private SynchronizedStack<DeltaRequest> deltaRequestPool;
    
    public ClusterManagerBase() {
        this.log = LogFactory.getLog((Class)ClusterManagerBase.class);
        this.cluster = null;
        this.notifyListenersOnReplication = true;
        this.replicationValve = null;
        this.recordAllActions = false;
        this.deltaRequestPool = (SynchronizedStack<DeltaRequest>)new SynchronizedStack();
    }
    
    protected SynchronizedStack<DeltaRequest> getDeltaRequestPool() {
        return this.deltaRequestPool;
    }
    
    public CatalinaCluster getCluster() {
        return this.cluster;
    }
    
    public void setCluster(final CatalinaCluster cluster) {
        this.cluster = cluster;
    }
    
    public boolean isNotifyListenersOnReplication() {
        return this.notifyListenersOnReplication;
    }
    
    public void setNotifyListenersOnReplication(final boolean notifyListenersOnReplication) {
        this.notifyListenersOnReplication = notifyListenersOnReplication;
    }
    
    public boolean isRecordAllActions() {
        return this.recordAllActions;
    }
    
    public void setRecordAllActions(final boolean recordAllActions) {
        this.recordAllActions = recordAllActions;
    }
    
    public static ClassLoader[] getClassLoaders(final Context context) {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final Loader loader = context.getLoader();
        ClassLoader classLoader = null;
        if (loader != null) {
            classLoader = loader.getClassLoader();
        }
        if (classLoader == null) {
            classLoader = tccl;
        }
        if (classLoader == tccl) {
            return new ClassLoader[] { classLoader };
        }
        return new ClassLoader[] { classLoader, tccl };
    }
    
    public ClassLoader[] getClassLoaders() {
        return getClassLoaders(this.getContext());
    }
    
    public ReplicationStream getReplicationStream(final byte[] data) throws IOException {
        return this.getReplicationStream(data, 0, data.length);
    }
    
    public ReplicationStream getReplicationStream(final byte[] data, final int offset, final int length) throws IOException {
        final ByteArrayInputStream fis = new ByteArrayInputStream(data, offset, length);
        return new ReplicationStream((InputStream)fis, this.getClassLoaders());
    }
    
    public void load() {
    }
    
    public void unload() {
    }
    
    protected void clone(final ClusterManagerBase copy) {
        copy.setName("Clone-from-" + this.getName());
        copy.setMaxActiveSessions(this.getMaxActiveSessions());
        copy.setProcessExpiresFrequency(this.getProcessExpiresFrequency());
        copy.setNotifyListenersOnReplication(this.isNotifyListenersOnReplication());
        copy.setSessionAttributeNameFilter(this.getSessionAttributeNameFilter());
        copy.setSessionAttributeValueClassNameFilter(this.getSessionAttributeValueClassNameFilter());
        copy.setWarnOnSessionAttributeFilterFailure(this.getWarnOnSessionAttributeFilterFailure());
        copy.setSecureRandomClass(this.getSecureRandomClass());
        copy.setSecureRandomProvider(this.getSecureRandomProvider());
        copy.setSecureRandomAlgorithm(this.getSecureRandomAlgorithm());
        if (this.getSessionIdGenerator() != null) {
            try {
                final SessionIdGenerator copyIdGenerator = this.sessionIdGeneratorClass.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                copyIdGenerator.setSessionIdLength(this.getSessionIdGenerator().getSessionIdLength());
                copyIdGenerator.setJvmRoute(this.getSessionIdGenerator().getJvmRoute());
                copy.setSessionIdGenerator(copyIdGenerator);
            }
            catch (final ReflectiveOperationException ex) {}
        }
        copy.setRecordAllActions(this.isRecordAllActions());
    }
    
    protected void registerSessionAtReplicationValve(final DeltaSession session) {
        if (this.replicationValve == null) {
            final CatalinaCluster cluster = this.getCluster();
            if (cluster != null) {
                final Valve[] valves = cluster.getValves();
                if (valves != null && valves.length > 0) {
                    for (int i = 0; this.replicationValve == null && i < valves.length; ++i) {
                        if (valves[i] instanceof ReplicationValve) {
                            this.replicationValve = (ReplicationValve)valves[i];
                        }
                    }
                    if (this.replicationValve == null && this.log.isDebugEnabled()) {
                        this.log.debug((Object)"no ReplicationValve found for CrossContext Support");
                    }
                }
            }
        }
        if (this.replicationValve != null) {
            this.replicationValve.registerReplicationSession(session);
        }
    }
    
    protected void startInternal() throws LifecycleException {
        super.startInternal();
        if (this.getCluster() == null) {
            final Cluster cluster = this.getContext().getCluster();
            if (cluster instanceof CatalinaCluster) {
                this.setCluster((CatalinaCluster)cluster);
            }
        }
        if (this.cluster != null) {
            this.cluster.registerManager((Manager)this);
        }
    }
    
    protected void stopInternal() throws LifecycleException {
        if (this.cluster != null) {
            this.cluster.removeManager((Manager)this);
        }
        this.replicationValve = null;
        super.stopInternal();
    }
}
