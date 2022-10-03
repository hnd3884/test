package com.sun.org.glassfish.external.statistics.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import com.sun.org.glassfish.external.statistics.AverageRangeStatistic;

public final class AverageRangeStatisticImpl extends StatisticImpl implements AverageRangeStatistic, InvocationHandler
{
    private long currentVal;
    private long highWaterMark;
    private long lowWaterMark;
    private long numberOfSamples;
    private long runningTotal;
    private final long initCurrentVal;
    private final long initHighWaterMark;
    private final long initLowWaterMark;
    private final long initNumberOfSamples;
    private final long initRunningTotal;
    private final AverageRangeStatistic as;
    
    public AverageRangeStatisticImpl(final long curVal, final long highMark, final long lowMark, final String name, final String unit, final String desc, final long startTime, final long sampleTime) {
        super(name, unit, desc, startTime, sampleTime);
        this.currentVal = 0L;
        this.highWaterMark = Long.MIN_VALUE;
        this.lowWaterMark = Long.MAX_VALUE;
        this.numberOfSamples = 0L;
        this.runningTotal = 0L;
        this.as = (AverageRangeStatistic)Proxy.newProxyInstance(AverageRangeStatistic.class.getClassLoader(), new Class[] { AverageRangeStatistic.class }, this);
        this.currentVal = curVal;
        this.initCurrentVal = curVal;
        this.highWaterMark = highMark;
        this.initHighWaterMark = highMark;
        this.lowWaterMark = lowMark;
        this.initLowWaterMark = lowMark;
        this.numberOfSamples = 0L;
        this.initNumberOfSamples = this.numberOfSamples;
        this.runningTotal = 0L;
        this.initRunningTotal = this.runningTotal;
    }
    
    public synchronized AverageRangeStatistic getStatistic() {
        return this.as;
    }
    
    @Override
    public synchronized String toString() {
        return super.toString() + AverageRangeStatisticImpl.NEWLINE + "Current: " + this.getCurrent() + AverageRangeStatisticImpl.NEWLINE + "LowWaterMark: " + this.getLowWaterMark() + AverageRangeStatisticImpl.NEWLINE + "HighWaterMark: " + this.getHighWaterMark() + AverageRangeStatisticImpl.NEWLINE + "Average:" + this.getAverage();
    }
    
    @Override
    public synchronized Map getStaticAsMap() {
        final Map m = super.getStaticAsMap();
        m.put("current", this.getCurrent());
        m.put("lowwatermark", this.getLowWaterMark());
        m.put("highwatermark", this.getHighWaterMark());
        m.put("average", this.getAverage());
        return m;
    }
    
    @Override
    public synchronized void reset() {
        super.reset();
        this.currentVal = this.initCurrentVal;
        this.highWaterMark = this.initHighWaterMark;
        this.lowWaterMark = this.initLowWaterMark;
        this.numberOfSamples = this.initNumberOfSamples;
        this.runningTotal = this.initRunningTotal;
        this.sampleTime = -1L;
    }
    
    @Override
    public synchronized long getAverage() {
        if (this.numberOfSamples == 0L) {
            return -1L;
        }
        return this.runningTotal / this.numberOfSamples;
    }
    
    @Override
    public synchronized long getCurrent() {
        return this.currentVal;
    }
    
    public synchronized void setCurrent(final long curVal) {
        this.currentVal = curVal;
        this.lowWaterMark = ((curVal >= this.lowWaterMark) ? this.lowWaterMark : curVal);
        this.highWaterMark = ((curVal >= this.highWaterMark) ? curVal : this.highWaterMark);
        ++this.numberOfSamples;
        this.runningTotal += curVal;
        this.sampleTime = System.currentTimeMillis();
    }
    
    @Override
    public synchronized long getHighWaterMark() {
        return this.highWaterMark;
    }
    
    @Override
    public synchronized long getLowWaterMark() {
        return this.lowWaterMark;
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        this.checkMethod(method);
        Object result;
        try {
            result = method.invoke(this, args);
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
