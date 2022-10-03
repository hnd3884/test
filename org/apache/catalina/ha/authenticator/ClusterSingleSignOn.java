package org.apache.catalina.ha.authenticator;

import java.util.Map;
import org.apache.catalina.authenticator.SingleSignOnEntry;
import org.apache.catalina.Container;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Host;
import org.apache.catalina.SessionListener;
import java.security.Principal;
import org.apache.catalina.tribes.tipis.ReplicatedMap;
import org.apache.catalina.Session;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.tomcat.util.res.StringManager;
import org.apache.catalina.tribes.tipis.AbstractReplicatedMap;
import org.apache.catalina.ha.ClusterValve;
import org.apache.catalina.authenticator.SingleSignOn;

public class ClusterSingleSignOn extends SingleSignOn implements ClusterValve, AbstractReplicatedMap.MapOwner
{
    private static final StringManager sm;
    private CatalinaCluster cluster;
    private long rpcTimeout;
    private int mapSendOptions;
    private boolean terminateOnStartFailure;
    private long accessTimeout;
    
    public ClusterSingleSignOn() {
        this.cluster = null;
        this.rpcTimeout = 15000L;
        this.mapSendOptions = 6;
        this.terminateOnStartFailure = false;
        this.accessTimeout = 5000L;
    }
    
    public CatalinaCluster getCluster() {
        return this.cluster;
    }
    
    public void setCluster(final CatalinaCluster cluster) {
        this.cluster = cluster;
    }
    
    public long getRpcTimeout() {
        return this.rpcTimeout;
    }
    
    public void setRpcTimeout(final long rpcTimeout) {
        this.rpcTimeout = rpcTimeout;
    }
    
    public int getMapSendOptions() {
        return this.mapSendOptions;
    }
    
    public void setMapSendOptions(final int mapSendOptions) {
        this.mapSendOptions = mapSendOptions;
    }
    
    public boolean getTerminateOnStartFailure() {
        return this.terminateOnStartFailure;
    }
    
    public void setTerminateOnStartFailure(final boolean terminateOnStartFailure) {
        this.terminateOnStartFailure = terminateOnStartFailure;
    }
    
    public long getAccessTimeout() {
        return this.accessTimeout;
    }
    
    public void setAccessTimeout(final long accessTimeout) {
        this.accessTimeout = accessTimeout;
    }
    
    protected boolean associate(final String ssoId, final Session session) {
        final boolean result = super.associate(ssoId, session);
        if (result) {
            ((ReplicatedMap)this.cache).replicate((Object)ssoId, true);
        }
        return result;
    }
    
    protected boolean update(final String ssoId, final Principal principal, final String authType, final String username, final String password) {
        final boolean result = super.update(ssoId, principal, authType, username, password);
        if (result) {
            ((ReplicatedMap)this.cache).replicate((Object)ssoId, true);
        }
        return result;
    }
    
    protected SessionListener getSessionListener(final String ssoId) {
        return (SessionListener)new ClusterSingleSignOnListener(ssoId);
    }
    
    public void objectMadePrimary(final Object key, final Object value) {
    }
    
    protected synchronized void startInternal() throws LifecycleException {
        try {
            if (this.cluster == null) {
                final Container host = this.getContainer();
                if (host instanceof Host && host.getCluster() instanceof CatalinaCluster) {
                    this.setCluster((CatalinaCluster)host.getCluster());
                }
            }
            if (this.cluster == null) {
                throw new LifecycleException(ClusterSingleSignOn.sm.getString("clusterSingleSignOn.nocluster"));
            }
            final ClassLoader[] cls = { this.getClass().getClassLoader() };
            final ReplicatedMap<String, SingleSignOnEntry> cache = (ReplicatedMap<String, SingleSignOnEntry>)new ReplicatedMap((AbstractReplicatedMap.MapOwner)this, this.cluster.getChannel(), this.rpcTimeout, this.cluster.getClusterName() + "-SSO-cache", cls, this.terminateOnStartFailure);
            cache.setChannelSendOptions(this.mapSendOptions);
            cache.setAccessTimeout(this.accessTimeout);
            this.cache = (Map)cache;
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            throw new LifecycleException(ClusterSingleSignOn.sm.getString("clusterSingleSignOn.clusterLoad.fail"), t);
        }
        super.startInternal();
    }
    
    protected synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        if (this.getCluster() != null) {
            ((ReplicatedMap)this.cache).breakdown();
        }
    }
    
    static {
        sm = StringManager.getManager((Class)ClusterSingleSignOn.class);
    }
}
