package com.sun.corba.se.spi.monitoring;

public class StatisticMonitoredAttribute extends MonitoredAttributeBase
{
    private StatisticsAccumulator statisticsAccumulator;
    private Object mutex;
    
    public StatisticMonitoredAttribute(final String s, final String s2, final StatisticsAccumulator statisticsAccumulator, final Object mutex) {
        super(s);
        this.setMonitoredAttributeInfo(MonitoringFactories.getMonitoredAttributeInfoFactory().createMonitoredAttributeInfo(s2, String.class, false, true));
        this.statisticsAccumulator = statisticsAccumulator;
        this.mutex = mutex;
    }
    
    @Override
    public Object getValue() {
        synchronized (this.mutex) {
            return this.statisticsAccumulator.getValue();
        }
    }
    
    @Override
    public void clearState() {
        synchronized (this.mutex) {
            this.statisticsAccumulator.clearState();
        }
    }
    
    public StatisticsAccumulator getStatisticsAccumulator() {
        return this.statisticsAccumulator;
    }
}
