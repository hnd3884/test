package com.me.devicemanagement.framework.server.dms;

import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import java.util.Properties;

public class DMSDownloadEvent
{
    private final String featureName;
    private final String crsFilePath;
    private final String destinationLocation;
    private Properties headers;
    private Properties formData;
    private Properties proxyProperties;
    private Integer proxyType;
    private Boolean isClosedNetworkType;
    private Integer statusCode;
    private DownloadStatus downloadStatus;
    
    public DMSDownloadEvent(final String featureName, final String crsFilePath, final String destinationLocation) {
        this.featureName = featureName;
        this.crsFilePath = crsFilePath;
        this.destinationLocation = destinationLocation;
    }
    
    public String getFeatureName() {
        return this.featureName;
    }
    
    public String getCrsFilePath() {
        return this.crsFilePath;
    }
    
    public String getDestinationLocation() {
        return this.destinationLocation;
    }
    
    public Properties getHeaders() {
        return this.headers;
    }
    
    public void setHeaders(final Properties headers) {
        this.headers = headers;
    }
    
    public Properties getFormData() {
        return this.formData;
    }
    
    public void setFormData(final Properties formData) {
        this.formData = formData;
    }
    
    public Properties getProxyProperties() {
        return this.proxyProperties;
    }
    
    public void setProxyProperties(final Properties proxyProperties) {
        this.proxyProperties = proxyProperties;
    }
    
    public Integer getProxyType() {
        return this.proxyType;
    }
    
    public void setProxyType(final Integer proxyType) {
        this.proxyType = proxyType;
    }
    
    public Boolean getClosedNetworkType() {
        return this.isClosedNetworkType;
    }
    
    public void setClosedNetworkType(final Boolean closedNetworkType) {
        this.isClosedNetworkType = closedNetworkType;
    }
    
    public Integer getStatusCode() {
        return this.statusCode;
    }
    
    public void setStatusCode(final Integer statusCode) {
        this.statusCode = statusCode;
    }
    
    public DownloadStatus getDownloadStatus() {
        return this.downloadStatus;
    }
    
    public void setDownloadStatus(final DownloadStatus downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
}
