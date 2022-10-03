package com.sun.corba.se.spi.monitoring;

public abstract class MonitoredAttributeBase implements MonitoredAttribute
{
    String name;
    MonitoredAttributeInfo attributeInfo;
    
    public MonitoredAttributeBase(final String name, final MonitoredAttributeInfo attributeInfo) {
        this.name = name;
        this.attributeInfo = attributeInfo;
    }
    
    MonitoredAttributeBase(final String name) {
        this.name = name;
    }
    
    void setMonitoredAttributeInfo(final MonitoredAttributeInfo attributeInfo) {
        this.attributeInfo = attributeInfo;
    }
    
    @Override
    public void clearState() {
    }
    
    @Override
    public abstract Object getValue();
    
    @Override
    public void setValue(final Object o) {
        if (!this.attributeInfo.isWritable()) {
            throw new IllegalStateException("The Attribute " + this.name + " is not Writable...");
        }
        throw new IllegalStateException("The method implementation is not provided for the attribute " + this.name);
    }
    
    @Override
    public MonitoredAttributeInfo getAttributeInfo() {
        return this.attributeInfo;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
