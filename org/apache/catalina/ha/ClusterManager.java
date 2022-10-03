package org.apache.catalina.ha;

import java.io.IOException;
import org.apache.catalina.tribes.io.ReplicationStream;
import org.apache.catalina.Manager;

public interface ClusterManager extends Manager
{
    void messageDataReceived(final ClusterMessage p0);
    
    ClusterMessage requestCompleted(final String p0);
    
    String[] getInvalidatedSessions();
    
    String getName();
    
    void setName(final String p0);
    
    CatalinaCluster getCluster();
    
    void setCluster(final CatalinaCluster p0);
    
    ReplicationStream getReplicationStream(final byte[] p0) throws IOException;
    
    ReplicationStream getReplicationStream(final byte[] p0, final int p1, final int p2) throws IOException;
    
    boolean isNotifyListenersOnReplication();
    
    ClusterManager cloneFromTemplate();
}
