package com.me.devicemanagement.framework.server.dms;

import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import java.util.Properties;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.util.logging.Level;
import java.util.Date;
import java.util.logging.Logger;
import java.util.concurrent.Callable;

public class DMSTaskRunner implements Callable<String>
{
    private static final Logger LOGGER;
    private DMSDownloadListener listener;
    private DMSDownloadEvent event;
    private DMSDownloadMeta meta;
    private Date taskStartedAt;
    
    public void setListener(final DMSDownloadListener listener) {
        this.listener = listener;
    }
    
    public void setEvent(final DMSDownloadEvent event) {
        this.event = event;
    }
    
    public void setMeta(final DMSDownloadMeta meta) {
        this.meta = meta;
    }
    
    public void setTaskStartedAt(final Date taskStartedAt) {
        this.taskStartedAt = taskStartedAt;
    }
    
    public String getComponentName() {
        return (this.meta != null) ? this.meta.getComponentName() : "";
    }
    
    public String getFeatureName() {
        return (this.meta != null) ? this.meta.getFeatureName() : "";
    }
    
    @Override
    public String call() throws Exception {
        final String featureName = this.getFeatureName();
        DMSTaskRunner.LOGGER.log(Level.INFO, "DMS Feature task starts for the feature : " + featureName + " for DMSFeatureDownloader started at : " + this.taskStartedAt);
        this.event = this.listener.preFileDownload(this.event);
        DMSTaskRunner.LOGGER.log(Level.INFO, "Going to access the URL : " + this.event.getCrsFilePath());
        DMSTaskRunner.LOGGER.log(Level.INFO, "Destination File : " + this.event.getDestinationLocation());
        final DownloadManager downloadManager = DownloadManager.getInstance();
        final String crsFilePath = this.event.getCrsFilePath();
        final String destinationLocation = this.event.getDestinationLocation();
        final String checksum = this.meta.getCheckSum();
        final String checksumType = this.meta.getCheckSumType();
        final Properties formData = this.event.getFormData();
        final Properties headers = this.event.getHeaders();
        final Properties proxyProperties = this.event.getProxyProperties();
        final Integer proxyType = this.event.getProxyType();
        final Boolean networkType = this.event.getClosedNetworkType();
        if (proxyProperties != null) {
            downloadManager.setProxyConfiguration(proxyProperties);
        }
        if (proxyType != null) {
            downloadManager.setProxyType(proxyType);
        }
        if (networkType != null) {
            downloadManager.setNetworkType(networkType);
        }
        DMSTaskRunner.LOGGER.log(Level.INFO, "Download for Feature task starts for the feature : " + featureName + " for DMSFeatureDownloader started at : " + this.taskStartedAt);
        final DownloadStatus status = downloadManager.downloadFileWithCheckSumValidation(crsFilePath, destinationLocation, checksum, checksumType, formData, headers, new SSLValidationType[0]);
        DMSTaskRunner.LOGGER.log(Level.INFO, "Download for Feature task ends for the feature : " + featureName + " for DMSFeatureDownloader started at : " + this.taskStartedAt);
        this.event.setStatusCode(status.getStatus());
        this.event.setDownloadStatus(status);
        DMSTaskRunner.LOGGER.log(Level.INFO, "DMS Post Download Feature task starts for the feature : " + featureName + " for DMSFeatureDownloader started at : " + this.taskStartedAt);
        this.listener.postDownloadEvent(this.event);
        DMSTaskRunner.LOGGER.log(Level.INFO, "DMS Feature task is complete for the feature : " + featureName + " for DMSFeatureDownloader started at : " + this.taskStartedAt);
        DMSDownloadUtil.getInstance().updateFileVersionAndDownloadStatusForFeature(this.getComponentName(), featureName, this.meta.getFileVersion(), this.event.getStatusCode());
        return featureName;
    }
    
    static {
        LOGGER = Logger.getLogger("dms");
    }
}
