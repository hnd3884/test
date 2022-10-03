package org.apache.catalina.tribes.transport.nio;

public interface PooledParallelSenderMBean
{
    int getRxBufSize();
    
    int getTxBufSize();
    
    int getUdpRxBufSize();
    
    int getUdpTxBufSize();
    
    boolean getDirectBuffer();
    
    int getKeepAliveCount();
    
    long getKeepAliveTime();
    
    long getTimeout();
    
    int getMaxRetryAttempts();
    
    boolean getOoBInline();
    
    boolean getSoKeepAlive();
    
    boolean getSoLingerOn();
    
    int getSoLingerTime();
    
    boolean getSoReuseAddress();
    
    int getSoTrafficClass();
    
    boolean getTcpNoDelay();
    
    boolean getThrowOnFailedAck();
    
    int getPoolSize();
    
    long getMaxWait();
    
    boolean isConnected();
    
    int getInPoolSize();
    
    int getInUsePoolSize();
}
