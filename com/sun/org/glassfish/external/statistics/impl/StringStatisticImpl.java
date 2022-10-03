package com.sun.org.glassfish.external.statistics.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import com.sun.org.glassfish.external.statistics.StringStatistic;

public final class StringStatisticImpl extends StatisticImpl implements StringStatistic, InvocationHandler
{
    private volatile String str;
    private final String initStr;
    private final StringStatistic ss;
    
    public StringStatisticImpl(final String str, final String name, final String unit, final String desc, final long sampleTime, final long startTime) {
        super(name, unit, desc, startTime, sampleTime);
        this.str = null;
        this.ss = (StringStatistic)Proxy.newProxyInstance(StringStatistic.class.getClassLoader(), new Class[] { StringStatistic.class }, this);
        this.str = str;
        this.initStr = str;
    }
    
    public StringStatisticImpl(final String name, final String unit, final String desc) {
        this("", name, unit, desc, System.currentTimeMillis(), System.currentTimeMillis());
    }
    
    public synchronized StringStatistic getStatistic() {
        return this.ss;
    }
    
    @Override
    public synchronized Map getStaticAsMap() {
        final Map m = super.getStaticAsMap();
        if (this.getCurrent() != null) {
            m.put("current", this.getCurrent());
        }
        return m;
    }
    
    @Override
    public synchronized String toString() {
        return super.toString() + StringStatisticImpl.NEWLINE + "Current-value: " + this.getCurrent();
    }
    
    @Override
    public String getCurrent() {
        return this.str;
    }
    
    public void setCurrent(final String str) {
        this.str = str;
        this.sampleTime = System.currentTimeMillis();
    }
    
    @Override
    public synchronized void reset() {
        super.reset();
        this.str = this.initStr;
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
