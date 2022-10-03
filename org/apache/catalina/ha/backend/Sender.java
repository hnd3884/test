package org.apache.catalina.ha.backend;

public interface Sender
{
    void init(final HeartbeatListener p0) throws Exception;
    
    int send(final String p0) throws Exception;
}
