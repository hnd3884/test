package com.sun.org.glassfish.external.statistics.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import com.sun.org.glassfish.external.statistics.TimeStatistic;

public final class TimeStatisticImpl extends StatisticImpl implements TimeStatistic, InvocationHandler
{
    private long count;
    private long maxTime;
    private long minTime;
    private long totTime;
    private final long initCount;
    private final long initMaxTime;
    private final long initMinTime;
    private final long initTotTime;
    private final TimeStatistic ts;
    
    @Override
    public final synchronized String toString() {
        return super.toString() + TimeStatisticImpl.NEWLINE + "Count: " + this.getCount() + TimeStatisticImpl.NEWLINE + "MinTime: " + this.getMinTime() + TimeStatisticImpl.NEWLINE + "MaxTime: " + this.getMaxTime() + TimeStatisticImpl.NEWLINE + "TotalTime: " + this.getTotalTime();
    }
    
    public TimeStatisticImpl(final long counter, final long maximumTime, final long minimumTime, final long totalTime, final String name, final String unit, final String desc, final long startTime, final long sampleTime) {
        super(name, unit, desc, startTime, sampleTime);
        this.count = 0L;
        this.maxTime = 0L;
        this.minTime = 0L;
        this.totTime = 0L;
        this.ts = (TimeStatistic)Proxy.newProxyInstance(TimeStatistic.class.getClassLoader(), new Class[] { TimeStatistic.class }, this);
        this.count = counter;
        this.initCount = counter;
        this.maxTime = maximumTime;
        this.initMaxTime = maximumTime;
        this.minTime = minimumTime;
        this.initMinTime = minimumTime;
        this.totTime = totalTime;
        this.initTotTime = totalTime;
    }
    
    public synchronized TimeStatistic getStatistic() {
        return this.ts;
    }
    
    @Override
    public synchronized Map getStaticAsMap() {
        final Map m = super.getStaticAsMap();
        m.put("count", this.getCount());
        m.put("maxtime", this.getMaxTime());
        m.put("mintime", this.getMinTime());
        m.put("totaltime", this.getTotalTime());
        return m;
    }
    
    public synchronized void incrementCount(final long current) {
        if (this.count == 0L) {
            this.totTime = current;
            this.maxTime = current;
            this.minTime = current;
        }
        else {
            this.totTime += current;
            this.maxTime = ((current >= this.maxTime) ? current : this.maxTime);
            this.minTime = ((current >= this.minTime) ? this.minTime : current);
        }
        ++this.count;
        this.sampleTime = System.currentTimeMillis();
    }
    
    @Override
    public synchronized long getCount() {
        return this.count;
    }
    
    @Override
    public synchronized long getMaxTime() {
        return this.maxTime;
    }
    
    @Override
    public synchronized long getMinTime() {
        return this.minTime;
    }
    
    @Override
    public synchronized long getTotalTime() {
        return this.totTime;
    }
    
    @Override
    public synchronized void reset() {
        super.reset();
        this.count = this.initCount;
        this.maxTime = this.initMaxTime;
        this.minTime = this.initMinTime;
        this.totTime = this.initTotTime;
        this.sampleTime = -1L;
    }
    
    @Override
    public Object invoke(final Object proxy, final Method m, final Object[] args) throws Throwable {
        this.checkMethod(m);
        Object result;
        try {
            result = m.invoke(this, args);
        }
        catch (final InvocationTargetException e) {
            throw e.getTargetException();
        }
        catch (final Exception e2) {
            throw new RuntimeException("unexpected invocation exception: " + e2.getMessage());
        }
        return result;
    }
}
