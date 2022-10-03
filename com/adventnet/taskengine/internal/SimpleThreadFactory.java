package com.adventnet.taskengine.internal;

import java.util.concurrent.ThreadFactory;

public class SimpleThreadFactory implements ThreadFactory
{
    private String factoryName;
    private int threadID;
    
    public SimpleThreadFactory(final String fName) {
        this.factoryName = null;
        this.threadID = 1;
        this.factoryName = fName;
    }
    
    @Override
    public Thread newThread(final Runnable runnable) {
        return new Thread(runnable, this.factoryName + "-" + this.threadID++);
    }
}
