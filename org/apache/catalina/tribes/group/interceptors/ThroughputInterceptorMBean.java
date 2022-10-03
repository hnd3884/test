package org.apache.catalina.tribes.group.interceptors;

import java.util.concurrent.atomic.AtomicLong;

public interface ThroughputInterceptorMBean
{
    int getOptionFlag();
    
    int getInterval();
    
    void setInterval(final int p0);
    
    double getLastCnt();
    
    double getMbAppTx();
    
    double getMbRx();
    
    double getMbTx();
    
    AtomicLong getMsgRxCnt();
    
    AtomicLong getMsgTxCnt();
    
    AtomicLong getMsgTxErr();
    
    long getRxStart();
    
    double getTimeTx();
    
    long getTxStart();
    
    void report(final double p0);
}
