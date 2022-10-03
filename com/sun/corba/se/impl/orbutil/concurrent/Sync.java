package com.sun.corba.se.impl.orbutil.concurrent;

public interface Sync
{
    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60000L;
    public static final long ONE_HOUR = 3600000L;
    public static final long ONE_DAY = 86400000L;
    public static final long ONE_WEEK = 604800000L;
    public static final long ONE_YEAR = 31556952000L;
    public static final long ONE_CENTURY = 3155695200000L;
    
    void acquire() throws InterruptedException;
    
    boolean attempt(final long p0) throws InterruptedException;
    
    void release();
}
