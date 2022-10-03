package com.sun.org.glassfish.external.statistics.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import com.sun.org.glassfish.external.statistics.CountStatistic;

public final class CountStatisticImpl extends StatisticImpl implements CountStatistic, InvocationHandler
{
    private long count;
    private final long initCount;
    private final CountStatistic cs;
    
    public CountStatisticImpl(final long countVal, final String name, final String unit, final String desc, final long sampleTime, final long startTime) {
        super(name, unit, desc, startTime, sampleTime);
        this.count = 0L;
        this.cs = (CountStatistic)Proxy.newProxyInstance(CountStatistic.class.getClassLoader(), new Class[] { CountStatistic.class }, this);
        this.count = countVal;
        this.initCount = countVal;
    }
    
    public CountStatisticImpl(final String name, final String unit, final String desc) {
        this(0L, name, unit, desc, -1L, System.currentTimeMillis());
    }
    
    public synchronized CountStatistic getStatistic() {
        return this.cs;
    }
    
    @Override
    public synchronized Map getStaticAsMap() {
        final Map m = super.getStaticAsMap();
        m.put("count", this.getCount());
        return m;
    }
    
    @Override
    public synchronized String toString() {
        return super.toString() + CountStatisticImpl.NEWLINE + "Count: " + this.getCount();
    }
    
    @Override
    public synchronized long getCount() {
        return this.count;
    }
    
    public synchronized void setCount(final long countVal) {
        this.count = countVal;
        this.sampleTime = System.currentTimeMillis();
    }
    
    public synchronized void increment() {
        ++this.count;
        this.sampleTime = System.currentTimeMillis();
    }
    
    public synchronized void increment(final long delta) {
        this.count += delta;
        this.sampleTime = System.currentTimeMillis();
    }
    
    public synchronized void decrement() {
        --this.count;
        this.sampleTime = System.currentTimeMillis();
    }
    
    @Override
    public synchronized void reset() {
        super.reset();
        this.count = this.initCount;
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
