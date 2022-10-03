package org.apache.catalina.ha;

import org.apache.catalina.tribes.Channel;
import org.apache.catalina.Manager;
import java.util.Map;
import org.apache.catalina.Valve;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.Cluster;

public interface CatalinaCluster extends Cluster
{
    void send(final ClusterMessage p0);
    
    void send(final ClusterMessage p0, final Member p1);
    
    boolean hasMembers();
    
    Member[] getMembers();
    
    Member getLocalMember();
    
    void addValve(final Valve p0);
    
    void addClusterListener(final ClusterListener p0);
    
    void removeClusterListener(final ClusterListener p0);
    
    void setClusterDeployer(final ClusterDeployer p0);
    
    ClusterDeployer getClusterDeployer();
    
    Map<String, ClusterManager> getManagers();
    
    Manager getManager(final String p0);
    
    String getManagerName(final String p0, final Manager p1);
    
    Valve[] getValves();
    
    void setChannel(final Channel p0);
    
    Channel getChannel();
}
