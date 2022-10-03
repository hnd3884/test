package com.sun.corba.se.spi.orbutil.threadpool;

public interface ThreadPoolChooser
{
    ThreadPool getThreadPool();
    
    ThreadPool getThreadPool(final int p0);
    
    String[] getThreadPoolIds();
}
