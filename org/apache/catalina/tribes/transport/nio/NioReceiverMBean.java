package org.apache.catalina.tribes.transport.nio;

public interface NioReceiverMBean
{
    String getAddress();
    
    boolean getDirect();
    
    int getPort();
    
    int getAutoBind();
    
    int getSecurePort();
    
    int getUdpPort();
    
    long getSelectorTimeout();
    
    int getMaxThreads();
    
    int getMinThreads();
    
    long getMaxIdleTime();
    
    boolean getOoBInline();
    
    int getRxBufSize();
    
    int getTxBufSize();
    
    int getUdpRxBufSize();
    
    int getUdpTxBufSize();
    
    boolean getSoKeepAlive();
    
    boolean getSoLingerOn();
    
    int getSoLingerTime();
    
    boolean getSoReuseAddress();
    
    boolean getTcpNoDelay();
    
    int getTimeout();
    
    boolean getUseBufferPool();
    
    boolean isListening();
    
    int getPoolSize();
    
    int getActiveCount();
    
    long getTaskCount();
    
    long getCompletedTaskCount();
}
