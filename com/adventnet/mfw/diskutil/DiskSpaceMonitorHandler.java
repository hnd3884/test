package com.adventnet.mfw.diskutil;

public interface DiskSpaceMonitorHandler
{
    void monitoredDiskUsage(final DiskSpaceStatus p0);
    
    DiskSpaceMonitorConstants.DISKSPACE_STATUS preInvokeServerShutDown();
}
