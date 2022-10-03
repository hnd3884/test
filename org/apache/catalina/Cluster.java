package org.apache.catalina;

public interface Cluster extends Contained
{
    String getClusterName();
    
    void setClusterName(final String p0);
    
    Manager createManager(final String p0);
    
    void registerManager(final Manager p0);
    
    void removeManager(final Manager p0);
    
    void backgroundProcess();
}
