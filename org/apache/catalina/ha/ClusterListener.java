package org.apache.catalina.ha;

import org.apache.juli.logging.LogFactory;
import org.apache.catalina.tribes.Member;
import java.io.Serializable;
import org.apache.juli.logging.Log;
import org.apache.catalina.tribes.ChannelListener;

public abstract class ClusterListener implements ChannelListener
{
    private static final Log log;
    protected CatalinaCluster cluster;
    
    public ClusterListener() {
        this.cluster = null;
    }
    
    public CatalinaCluster getCluster() {
        return this.cluster;
    }
    
    public void setCluster(final CatalinaCluster cluster) {
        if (ClusterListener.log.isDebugEnabled()) {
            if (cluster != null) {
                ClusterListener.log.debug((Object)("add ClusterListener " + this.toString() + " to cluster" + cluster));
            }
            else {
                ClusterListener.log.debug((Object)("remove ClusterListener " + this.toString() + " from cluster"));
            }
        }
        this.cluster = cluster;
    }
    
    public final void messageReceived(final Serializable msg, final Member member) {
        if (msg instanceof ClusterMessage) {
            this.messageReceived((ClusterMessage)msg);
        }
    }
    
    public final boolean accept(final Serializable msg, final Member member) {
        return msg instanceof ClusterMessage;
    }
    
    public abstract void messageReceived(final ClusterMessage p0);
    
    public abstract boolean accept(final ClusterMessage p0);
    
    static {
        log = LogFactory.getLog((Class)ClusterListener.class);
    }
}
