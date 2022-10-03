package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredAttributeInfo;

public class MonitoredAttributeInfoImpl implements MonitoredAttributeInfo
{
    private final String description;
    private final Class type;
    private final boolean writableFlag;
    private final boolean statisticFlag;
    
    MonitoredAttributeInfoImpl(final String description, final Class type, final boolean writableFlag, final boolean statisticFlag) {
        this.description = description;
        this.type = type;
        this.writableFlag = writableFlag;
        this.statisticFlag = statisticFlag;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public Class type() {
        return this.type;
    }
    
    @Override
    public boolean isWritable() {
        return this.writableFlag;
    }
    
    @Override
    public boolean isStatistic() {
        return this.statisticFlag;
    }
}
