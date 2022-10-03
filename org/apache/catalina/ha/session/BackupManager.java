package org.apache.catalina.ha.session;

import java.util.Map;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.catalina.ha.ClusterManager;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.tribes.tipis.LazyReplicatedMap;
import org.apache.catalina.ha.ClusterMessage;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.DistributedManager;
import org.apache.catalina.tribes.tipis.AbstractReplicatedMap;

public class BackupManager extends ClusterManagerBase implements AbstractReplicatedMap.MapOwner, DistributedManager
{
    private final Log log;
    protected static final StringManager sm;
    protected static final long DEFAULT_REPL_TIMEOUT = 15000L;
    protected String name;
    private int mapSendOptions;
    private long rpcTimeout;
    private boolean terminateOnStartFailure;
    private long accessTimeout;
    
    public BackupManager() {
        this.log = LogFactory.getLog((Class)BackupManager.class);
        this.mapSendOptions = 6;
        this.rpcTimeout = 15000L;
        this.terminateOnStartFailure = false;
        this.accessTimeout = 5000L;
    }
    
    public void messageDataReceived(final ClusterMessage msg) {
    }
    
    public ClusterMessage requestCompleted(final String sessionId) {
        if (!this.getState().isAvailable()) {
            return null;
        }
        final LazyReplicatedMap<String, Session> map = (LazyReplicatedMap<String, Session>)this.sessions;
        map.replicate((Object)sessionId, false);
        return null;
    }
    
    public void objectMadePrimary(final Object key, final Object value) {
        if (value instanceof DeltaSession) {
            final DeltaSession session = (DeltaSession)value;
            synchronized (session) {
                session.access();
                session.setPrimarySession(true);
                session.endAccess();
            }
        }
    }
    
    public Session createEmptySession() {
        return (Session)new DeltaSession((Manager)this);
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    protected synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        try {
            if (this.cluster == null) {
                throw new LifecycleException(BackupManager.sm.getString("backupManager.noCluster", new Object[] { this.getName() }));
            }
            final LazyReplicatedMap<String, Session> map = (LazyReplicatedMap<String, Session>)new LazyReplicatedMap((AbstractReplicatedMap.MapOwner)this, this.cluster.getChannel(), this.rpcTimeout, this.getMapName(), this.getClassLoaders(), this.terminateOnStartFailure);
            map.setChannelSendOptions(this.mapSendOptions);
            map.setAccessTimeout(this.accessTimeout);
            this.sessions = (Map)map;
        }
        catch (final Exception x) {
            this.log.error((Object)BackupManager.sm.getString("backupManager.startUnable", new Object[] { this.getName() }), (Throwable)x);
            throw new LifecycleException(BackupManager.sm.getString("backupManager.startFailed", new Object[] { this.getName() }), (Throwable)x);
        }
        this.setState(LifecycleState.STARTING);
    }
    
    public String getMapName() {
        final String name = this.cluster.getManagerName(this.getName(), (Manager)this) + "-" + "map";
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Backup manager, Setting map name to:" + name));
        }
        return name;
    }
    
    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)BackupManager.sm.getString("backupManager.stopped", new Object[] { this.getName() }));
        }
        this.setState(LifecycleState.STOPPING);
        if (this.sessions instanceof LazyReplicatedMap) {
            final LazyReplicatedMap<String, Session> map = (LazyReplicatedMap<String, Session>)this.sessions;
            map.breakdown();
        }
        super.stopInternal();
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setMapSendOptions(final int mapSendOptions) {
        this.mapSendOptions = mapSendOptions;
    }
    
    public int getMapSendOptions() {
        return this.mapSendOptions;
    }
    
    public void setRpcTimeout(final long rpcTimeout) {
        this.rpcTimeout = rpcTimeout;
    }
    
    public long getRpcTimeout() {
        return this.rpcTimeout;
    }
    
    public void setTerminateOnStartFailure(final boolean terminateOnStartFailure) {
        this.terminateOnStartFailure = terminateOnStartFailure;
    }
    
    public boolean isTerminateOnStartFailure() {
        return this.terminateOnStartFailure;
    }
    
    public long getAccessTimeout() {
        return this.accessTimeout;
    }
    
    public void setAccessTimeout(final long accessTimeout) {
        this.accessTimeout = accessTimeout;
    }
    
    public String[] getInvalidatedSessions() {
        return new String[0];
    }
    
    public ClusterManager cloneFromTemplate() {
        final BackupManager result = new BackupManager();
        this.clone(result);
        result.mapSendOptions = this.mapSendOptions;
        result.rpcTimeout = this.rpcTimeout;
        result.terminateOnStartFailure = this.terminateOnStartFailure;
        result.accessTimeout = this.accessTimeout;
        return result;
    }
    
    public int getActiveSessionsFull() {
        final LazyReplicatedMap<String, Session> map = (LazyReplicatedMap<String, Session>)this.sessions;
        return map.sizeFull();
    }
    
    public Set<String> getSessionIdsFull() {
        final LazyReplicatedMap<String, Session> map = (LazyReplicatedMap<String, Session>)this.sessions;
        final Set<String> sessionIds = new HashSet<String>(map.keySetFull());
        return sessionIds;
    }
    
    static {
        sm = StringManager.getManager((Class)BackupManager.class);
    }
}
