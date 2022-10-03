package com.sun.corba.se.spi.orbutil.threadpool;

import java.io.Closeable;

public interface ThreadPoolManager extends Closeable
{
    ThreadPool getThreadPool(final String p0) throws NoSuchThreadPoolException;
    
    ThreadPool getThreadPool(final int p0) throws NoSuchThreadPoolException;
    
    int getThreadPoolNumericId(final String p0);
    
    String getThreadPoolStringId(final int p0);
    
    ThreadPool getDefaultThreadPool();
    
    ThreadPoolChooser getThreadPoolChooser(final String p0);
    
    ThreadPoolChooser getThreadPoolChooser(final int p0);
    
    void setThreadPoolChooser(final String p0, final ThreadPoolChooser p1);
    
    int getThreadPoolChooserNumericId(final String p0);
}
