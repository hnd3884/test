package javax.management.monitor;

import javax.management.ObjectName;

public interface MonitorMBean
{
    void start();
    
    void stop();
    
    void addObservedObject(final ObjectName p0) throws IllegalArgumentException;
    
    void removeObservedObject(final ObjectName p0);
    
    boolean containsObservedObject(final ObjectName p0);
    
    ObjectName[] getObservedObjects();
    
    @Deprecated
    ObjectName getObservedObject();
    
    @Deprecated
    void setObservedObject(final ObjectName p0);
    
    String getObservedAttribute();
    
    void setObservedAttribute(final String p0);
    
    long getGranularityPeriod();
    
    void setGranularityPeriod(final long p0) throws IllegalArgumentException;
    
    boolean isActive();
}
