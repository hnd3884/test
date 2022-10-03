package javax.management.monitor;

import javax.management.ObjectName;

public interface GaugeMonitorMBean extends MonitorMBean
{
    @Deprecated
    Number getDerivedGauge();
    
    @Deprecated
    long getDerivedGaugeTimeStamp();
    
    Number getDerivedGauge(final ObjectName p0);
    
    long getDerivedGaugeTimeStamp(final ObjectName p0);
    
    Number getHighThreshold();
    
    Number getLowThreshold();
    
    void setThresholds(final Number p0, final Number p1) throws IllegalArgumentException;
    
    boolean getNotifyHigh();
    
    void setNotifyHigh(final boolean p0);
    
    boolean getNotifyLow();
    
    void setNotifyLow(final boolean p0);
    
    boolean getDifferenceMode();
    
    void setDifferenceMode(final boolean p0);
}
