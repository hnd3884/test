package com.adventnet.mfw.diskutil;

import java.sql.Timestamp;

public class DiskSpaceStatus
{
    private String monitoringPath;
    private long lowerLimit;
    private long upperLimit;
    private DiskSpaceMonitorConstants.DISKSPACE_STATUS statusMessage;
    private long monitoredTime;
    
    public DiskSpaceStatus(final String monitoringPath, final long monitoredTime, final long lowerLimit, final long upperLimit, final DiskSpaceMonitorConstants.DISKSPACE_STATUS statusMessage) {
        this.monitoringPath = monitoringPath;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.statusMessage = statusMessage;
        this.monitoredTime = monitoredTime;
    }
    
    public void setMonitoredTime(final long value) {
        this.monitoredTime = value;
    }
    
    public void setMonitoringPath(final String path) {
        this.monitoringPath = path;
    }
    
    public void setLowerLimit(final long lowerLimit) {
        this.lowerLimit = lowerLimit;
    }
    
    public void setUpperLimit(final long upperLimit) {
        this.upperLimit = upperLimit;
    }
    
    public void setMessage(final DiskSpaceMonitorConstants.DISKSPACE_STATUS status) {
        this.statusMessage = status;
    }
    
    public String getMonitoringPath() {
        return this.monitoringPath;
    }
    
    public long getLowerLimit() {
        return this.lowerLimit;
    }
    
    public long getUpperLimit() {
        return this.upperLimit;
    }
    
    public String getMessage() {
        return this.statusMessage.name();
    }
    
    public DiskSpaceMonitorConstants.DISKSPACE_STATUS getDiskSpaceStatusConstant() {
        return this.statusMessage;
    }
    
    public long getMonitoredTime() {
        return this.monitoredTime;
    }
    
    @Override
    public boolean equals(final Object obj) {
        DiskSpaceStatus diskStatus = null;
        if (obj instanceof DiskSpaceStatus) {
            diskStatus = (DiskSpaceStatus)obj;
            return diskStatus.lowerLimit == this.lowerLimit && diskStatus.upperLimit == this.upperLimit && diskStatus.monitoredTime == this.monitoredTime && diskStatus.monitoringPath != null && diskStatus.monitoringPath.equals(this.monitoringPath) && diskStatus.statusMessage != null && diskStatus.statusMessage.equals(this.statusMessage);
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        res.append("DISK-SPACE MONITOR STATUS ::");
        res.append(" TimeStamp : " + new Timestamp(this.monitoredTime).toString());
        res.append(" Message :" + this.statusMessage.name());
        res.append(" Path : " + this.monitoringPath);
        res.append(" Lower Limit :" + this.lowerLimit / 1048576L + " MB");
        res.append(" Upper Limit :" + this.upperLimit / 1048576L + " MB");
        return res.toString();
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 5 + ((this.monitoringPath != null) ? this.monitoringPath.hashCode() : 0);
        hash = hash * 13 + ((this.statusMessage != null) ? this.statusMessage.hashCode() : 0);
        return hash;
    }
}
