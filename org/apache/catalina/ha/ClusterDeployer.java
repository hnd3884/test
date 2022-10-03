package org.apache.catalina.ha;

import java.io.IOException;
import java.io.File;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.tribes.ChannelListener;

public interface ClusterDeployer extends ChannelListener
{
    void start() throws Exception;
    
    void stop() throws LifecycleException;
    
    void install(final String p0, final File p1) throws IOException;
    
    void remove(final String p0, final boolean p1) throws IOException;
    
    void backgroundProcess();
    
    CatalinaCluster getCluster();
    
    void setCluster(final CatalinaCluster p0);
}
