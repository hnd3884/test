package com.sun.corba.se.spi.monitoring;

public interface MonitoredAttribute
{
    MonitoredAttributeInfo getAttributeInfo();
    
    void setValue(final Object p0);
    
    Object getValue();
    
    String getName();
    
    void clearState();
}
