package com.zoho.security.eventfw.dispatcher;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

public class EventDispatcherExecutor
{
    private static ExecutorService threadpool;
    
    public static Future<?> submitRunnableTask(final Runnable runnableTask) {
        return EventDispatcherExecutor.threadpool.submit(runnableTask);
    }
    
    public static void executeRunnableTask(final Runnable runnableTask) {
        EventDispatcherExecutor.threadpool.execute(runnableTask);
    }
    
    public static void shutdownExecutorService() {
        EventDispatcherExecutor.threadpool.shutdown();
    }
    
    static {
        EventDispatcherExecutor.threadpool = null;
        EventDispatcherExecutor.threadpool = Executors.newFixedThreadPool(2);
    }
}
