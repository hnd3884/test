package com.sun.org.glassfish.external.statistics.impl;

import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import com.sun.org.glassfish.external.statistics.Statistic;

public abstract class StatisticImpl implements Statistic
{
    private final String statisticName;
    private final String statisticUnit;
    private final String statisticDesc;
    protected long sampleTime;
    private long startTime;
    public static final String UNIT_COUNT = "count";
    public static final String UNIT_SECOND = "second";
    public static final String UNIT_MILLISECOND = "millisecond";
    public static final String UNIT_MICROSECOND = "microsecond";
    public static final String UNIT_NANOSECOND = "nanosecond";
    public static final String START_TIME = "starttime";
    public static final String LAST_SAMPLE_TIME = "lastsampletime";
    protected final Map<String, Object> statMap;
    protected static final String NEWLINE;
    
    protected StatisticImpl(final String name, final String unit, final String desc, final long start_time, final long sample_time) {
        this.sampleTime = -1L;
        this.statMap = new ConcurrentHashMap<String, Object>();
        if (isValidString(name)) {
            this.statisticName = name;
        }
        else {
            this.statisticName = "name";
        }
        if (isValidString(unit)) {
            this.statisticUnit = unit;
        }
        else {
            this.statisticUnit = "unit";
        }
        if (isValidString(desc)) {
            this.statisticDesc = desc;
        }
        else {
            this.statisticDesc = "description";
        }
        this.startTime = start_time;
        this.sampleTime = sample_time;
    }
    
    protected StatisticImpl(final String name, final String unit, final String desc) {
        this(name, unit, desc, System.currentTimeMillis(), System.currentTimeMillis());
    }
    
    public synchronized Map getStaticAsMap() {
        if (isValidString(this.statisticName)) {
            this.statMap.put("name", this.statisticName);
        }
        if (isValidString(this.statisticUnit)) {
            this.statMap.put("unit", this.statisticUnit);
        }
        if (isValidString(this.statisticDesc)) {
            this.statMap.put("description", this.statisticDesc);
        }
        this.statMap.put("starttime", this.startTime);
        this.statMap.put("lastsampletime", this.sampleTime);
        return this.statMap;
    }
    
    @Override
    public String getName() {
        return this.statisticName;
    }
    
    @Override
    public String getDescription() {
        return this.statisticDesc;
    }
    
    @Override
    public String getUnit() {
        return this.statisticUnit;
    }
    
    @Override
    public synchronized long getLastSampleTime() {
        return this.sampleTime;
    }
    
    @Override
    public synchronized long getStartTime() {
        return this.startTime;
    }
    
    public synchronized void reset() {
        this.startTime = System.currentTimeMillis();
    }
    
    @Override
    public synchronized String toString() {
        return "Statistic " + this.getClass().getName() + StatisticImpl.NEWLINE + "Name: " + this.getName() + StatisticImpl.NEWLINE + "Description: " + this.getDescription() + StatisticImpl.NEWLINE + "Unit: " + this.getUnit() + StatisticImpl.NEWLINE + "LastSampleTime: " + this.getLastSampleTime() + StatisticImpl.NEWLINE + "StartTime: " + this.getStartTime();
    }
    
    protected static boolean isValidString(final String str) {
        return str != null && str.length() > 0;
    }
    
    protected void checkMethod(final Method method) {
        if (method == null || method.getDeclaringClass() == null || !Statistic.class.isAssignableFrom(method.getDeclaringClass()) || Modifier.isStatic(method.getModifiers())) {
            throw new RuntimeException("Invalid method on invoke");
        }
    }
    
    static {
        NEWLINE = System.getProperty("line.separator");
    }
}
