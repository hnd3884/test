package org.apache.catalina.tribes.transport;

import java.io.IOException;

public interface DataSender
{
    void connect() throws IOException;
    
    void disconnect();
    
    boolean isConnected();
    
    void setRxBufSize(final int p0);
    
    void setTxBufSize(final int p0);
    
    boolean keepalive();
    
    void setTimeout(final long p0);
    
    void setKeepAliveCount(final int p0);
    
    void setKeepAliveTime(final long p0);
    
    int getRequestCount();
    
    long getConnectTime();
}
