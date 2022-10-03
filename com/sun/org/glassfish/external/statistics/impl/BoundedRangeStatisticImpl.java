package com.sun.org.glassfish.external.statistics.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import com.sun.org.glassfish.external.statistics.BoundedRangeStatistic;

public final class BoundedRangeStatisticImpl extends StatisticImpl implements BoundedRangeStatistic, InvocationHandler
{
    private long lowerBound;
    private long upperBound;
    private long currentVal;
    private long highWaterMark;
    private long lowWaterMark;
    private final long initLowerBound;
    private final long initUpperBound;
    private final long initCurrentVal;
    private final long initHighWaterMark;
    private final long initLowWaterMark;
    private final BoundedRangeStatistic bs;
    
    @Override
    public synchronized String toString() {
        return super.toString() + BoundedRangeStatisticImpl.NEWLINE + "Current: " + this.getCurrent() + BoundedRangeStatisticImpl.NEWLINE + "LowWaterMark: " + this.getLowWaterMark() + BoundedRangeStatisticImpl.NEWLINE + "HighWaterMark: " + this.getHighWaterMark() + BoundedRangeStatisticImpl.NEWLINE + "LowerBound: " + this.getLowerBound() + BoundedRangeStatisticImpl.NEWLINE + "UpperBound: " + this.getUpperBound();
    }
    
    public BoundedRangeStatisticImpl(final long curVal, final long highMark, final long lowMark, final long upper, final long lower, final String name, final String unit, final String desc, final long startTime, final long sampleTime) {
        super(name, unit, desc, startTime, sampleTime);
        this.lowerBound = 0L;
        this.upperBound = 0L;
        this.currentVal = 0L;
        this.highWaterMark = Long.MIN_VALUE;
        this.lowWaterMark = Long.MAX_VALUE;
        this.bs = (BoundedRangeStatistic)Proxy.newProxyInstance(BoundedRangeStatistic.class.getClassLoader(), new Class[] { BoundedRangeStatistic.class }, this);
        this.currentVal = curVal;
        this.initCurrentVal = curVal;
        this.highWaterMark = highMark;
        this.initHighWaterMark = highMark;
        this.lowWaterMark = lowMark;
        this.initLowWaterMark = lowMark;
        this.upperBound = upper;
        this.initUpperBound = upper;
        this.lowerBound = lower;
        this.initLowerBound = lower;
    }
    
    public synchronized BoundedRangeStatistic getStatistic() {
        return this.bs;
    }
    
    @Override
    public synchronized Map getStaticAsMap() {
        final Map m = super.getStaticAsMap();
        m.put("current", this.getCurrent());
        m.put("lowerbound", this.getLowerBound());
        m.put("upperbound", this.getUpperBound());
        m.put("lowwatermark", this.getLowWaterMark());
        m.put("highwatermark", this.getHighWaterMark());
        return m;
    }
    
    @Override
    public synchronized long getCurrent() {
        return this.currentVal;
    }
    
    public synchronized void setCurrent(final long curVal) {
        this.currentVal = curVal;
        this.lowWaterMark = ((curVal >= this.lowWaterMark) ? this.lowWaterMark : curVal);
        this.highWaterMark = ((curVal >= this.highWaterMark) ? curVal : this.highWaterMark);
        this.sampleTime = System.currentTimeMillis();
    }
    
    @Override
    public synchronized long getHighWaterMark() {
        return this.highWaterMark;
    }
    
    public synchronized void setHighWaterMark(final long hwm) {
        this.highWaterMark = hwm;
    }
    
    @Override
    public synchronized long getLowWaterMark() {
        return this.lowWaterMark;
    }
    
    public synchronized void setLowWaterMark(final long lwm) {
        this.lowWaterMark = lwm;
    }
    
    @Override
    public synchronized long getLowerBound() {
        return this.lowerBound;
    }
    
    @Override
    public synchronized long getUpperBound() {
        return this.upperBound;
    }
    
    @Override
    public synchronized void reset() {
        super.reset();
        this.lowerBound = this.initLowerBound;
        this.upperBound = this.initUpperBound;
        this.currentVal = this.initCurrentVal;
        this.highWaterMark = this.initHighWaterMark;
        this.lowWaterMark = this.initLowWaterMark;
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
