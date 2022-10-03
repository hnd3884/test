package com.sun.corba.se.spi.orbutil.threadpool;

public interface WorkQueue
{
    void addWork(final Work p0);
    
    String getName();
    
    long totalWorkItemsAdded();
    
    int workItemsInQueue();
    
    long averageTimeInQueue();
    
    void setThreadPool(final ThreadPool p0);
    
    ThreadPool getThreadPool();
}
