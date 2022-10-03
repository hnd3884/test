package com.sun.corba.se.spi.monitoring;

public abstract class LongMonitoredAttributeBase extends MonitoredAttributeBase
{
    public LongMonitoredAttributeBase(final String s, final String s2) {
        super(s);
        this.setMonitoredAttributeInfo(MonitoringFactories.getMonitoredAttributeInfoFactory().createMonitoredAttributeInfo(s2, Long.class, false, false));
    }
}
