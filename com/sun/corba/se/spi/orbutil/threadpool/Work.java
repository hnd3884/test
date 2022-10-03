package com.sun.corba.se.spi.orbutil.threadpool;

public interface Work
{
    void doWork();
    
    void setEnqueueTime(final long p0);
    
    long getEnqueueTime();
    
    String getName();
}
