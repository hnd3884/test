package com.me.devicemanagement.framework.server.dms;

public class DMSDownloadMeta
{
    private final String componentName;
    private final String featureName;
    private final String checkSum;
    private final String checkSumType;
    private final Long fileVersion;
    private Long lastModifiedTime;
    
    public DMSDownloadMeta(final String componentName, final String featureName, final String checkSum, final String checkSumType, final Long fileVersion) {
        this.componentName = componentName;
        this.featureName = featureName;
        this.checkSum = checkSum;
        this.checkSumType = checkSumType;
        this.fileVersion = fileVersion;
    }
    
    public String getComponentName() {
        return this.componentName;
    }
    
    public String getFeatureName() {
        return this.featureName;
    }
    
    public String getCheckSum() {
        return this.checkSum;
    }
    
    public String getCheckSumType() {
        return this.checkSumType;
    }
    
    public Long getFileVersion() {
        return this.fileVersion;
    }
    
    public Long getLastModifiedTime() {
        return this.lastModifiedTime;
    }
    
    public void setLastModifiedTime(final Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
}
