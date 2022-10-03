package com.sun.corba.se.spi.monitoring;

import java.util.Collection;

public interface MonitoredObject
{
    String getName();
    
    String getDescription();
    
    void addChild(final MonitoredObject p0);
    
    void removeChild(final String p0);
    
    MonitoredObject getChild(final String p0);
    
    Collection getChildren();
    
    void setParent(final MonitoredObject p0);
    
    MonitoredObject getParent();
    
    void addAttribute(final MonitoredAttribute p0);
    
    void removeAttribute(final String p0);
    
    MonitoredAttribute getAttribute(final String p0);
    
    Collection getAttributes();
    
    void clearState();
}
