package com.sun.org.glassfish.external.statistics.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import com.sun.org.glassfish.external.statistics.RangeStatistic;

public final class RangeStatisticImpl extends StatisticImpl implements RangeStatistic, InvocationHandler
{
    private long currentVal;
    private long highWaterMark;
    private long lowWaterMark;
    private final long initCurrentVal;
    private final long initHighWaterMark;
    private final long initLowWaterMark;
    private final RangeStatistic rs;
    
    public RangeStatisticImpl(final long curVal, final long highMark, final long lowMark, final String name, final String unit, final String desc, final long startTime, final long sampleTime) {
        super(name, unit, desc, startTime, sampleTime);
        this.currentVal = 0L;
        this.highWaterMark = Long.MIN_VALUE;
        this.lowWaterMark = Long.MAX_VALUE;
        this.rs = (RangeStatistic)Proxy.newProxyInstance(RangeStatistic.class.getClassLoader(), new Class[] { RangeStatistic.class }, this);
        this.currentVal = curVal;
        this.initCurrentVal = curVal;
        this.highWaterMark = highMark;
        this.initHighWaterMark = highMark;
        this.lowWaterMark = lowMark;
        this.initLowWaterMark = lowMark;
    }
    
    public synchronized RangeStatistic getStatistic() {
        return this.rs;
    }
    
    @Override
    public synchronized Map getStaticAsMap() {
        final Map m = super.getStaticAsMap();
        m.put("current", this.getCurrent());
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
    public synchronized void reset() {
        super.reset();
        this.currentVal = this.initCurrentVal;
        this.highWaterMark = this.initHighWaterMark;
        this.lowWaterMark = this.initLowWaterMark;
        this.sampleTime = -1L;
    }
    
    @Override
    public synchronized String toString() {
        return super.toString() + RangeStatisticImpl.NEWLINE + "Current: " + this.getCurrent() + RangeStatisticImpl.NEWLINE + "LowWaterMark: " + this.getLowWaterMark() + RangeStatisticImpl.NEWLINE + "HighWaterMark: " + this.getHighWaterMark();
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
