package javax.management.monitor;

import javax.management.ObjectName;

public interface CounterMonitorMBean extends MonitorMBean
{
    @Deprecated
    Number getDerivedGauge();
    
    @Deprecated
    long getDerivedGaugeTimeStamp();
    
    @Deprecated
    Number getThreshold();
    
    @Deprecated
    void setThreshold(final Number p0) throws IllegalArgumentException;
    
    Number getDerivedGauge(final ObjectName p0);
    
    long getDerivedGaugeTimeStamp(final ObjectName p0);
    
    Number getThreshold(final ObjectName p0);
    
    Number getInitThreshold();
    
    void setInitThreshold(final Number p0) throws IllegalArgumentException;
    
    Number getOffset();
    
    void setOffset(final Number p0) throws IllegalArgumentException;
    
    Number getModulus();
    
    void setModulus(final Number p0) throws IllegalArgumentException;
    
    boolean getNotify();
    
    void setNotify(final boolean p0);
    
    boolean getDifferenceMode();
    
    void setDifferenceMode(final boolean p0);
}
