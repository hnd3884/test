package org.tanukisoftware.wrapper.jmx;

public interface WrapperManagerTestingMBean
{
    void appearHung();
    
    void accessViolationNative();
    
    void stopImmediate(final int p0);
}
