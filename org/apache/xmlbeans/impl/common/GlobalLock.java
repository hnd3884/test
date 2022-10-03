package org.apache.xmlbeans.impl.common;

public class GlobalLock
{
    private static final Mutex GLOBAL_MUTEX;
    
    public static void acquire() throws InterruptedException {
        GlobalLock.GLOBAL_MUTEX.acquire();
    }
    
    public static void tryToAcquire() {
        GlobalLock.GLOBAL_MUTEX.tryToAcquire();
    }
    
    public static void release() {
        GlobalLock.GLOBAL_MUTEX.release();
    }
    
    static {
        GLOBAL_MUTEX = new Mutex();
    }
}
