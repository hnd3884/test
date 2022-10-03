package com.sun.corba.se.spi.monitoring;

public abstract class StringMonitoredAttributeBase extends MonitoredAttributeBase
{
    public StringMonitoredAttributeBase(final String s, final String s2) {
        super(s);
        this.setMonitoredAttributeInfo(MonitoringFactories.getMonitoredAttributeInfoFactory().createMonitoredAttributeInfo(s2, String.class, false, false));
    }
}
