package com.sun.org.glassfish.external.statistics.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import com.sun.org.glassfish.external.statistics.BoundaryStatistic;

public final class BoundaryStatisticImpl extends StatisticImpl implements BoundaryStatistic, InvocationHandler
{
    private final long lowerBound;
    private final long upperBound;
    private final BoundaryStatistic bs;
    
    public BoundaryStatisticImpl(final long lower, final long upper, final String name, final String unit, final String desc, final long startTime, final long sampleTime) {
        super(name, unit, desc, startTime, sampleTime);
        this.bs = (BoundaryStatistic)Proxy.newProxyInstance(BoundaryStatistic.class.getClassLoader(), new Class[] { BoundaryStatistic.class }, this);
        this.upperBound = upper;
        this.lowerBound = lower;
    }
    
    public synchronized BoundaryStatistic getStatistic() {
        return this.bs;
    }
    
    @Override
    public synchronized Map getStaticAsMap() {
        final Map m = super.getStaticAsMap();
        m.put("lowerbound", this.getLowerBound());
        m.put("upperbound", this.getUpperBound());
        return m;
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
