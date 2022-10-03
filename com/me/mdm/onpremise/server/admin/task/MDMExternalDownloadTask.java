package com.me.mdm.onpremise.server.admin.task;

import com.me.mdm.server.util.MDMUpdatesParamHandler;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.Properties;
import com.me.mdm.onpremise.server.admin.MDMExternalDownloadsUtil;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MDMExternalDownloadTask implements SchedulerExecutionInterface
{
    private final Logger logger;
    MDMExternalDownloadsUtil downloadsUtil;
    
    public MDMExternalDownloadTask() {
        this.logger = Logger.getLogger("MDMExternalDownloadsLogger");
        this.downloadsUtil = new MDMExternalDownloadsUtil();
    }
    
    public void executeTask(final Properties taskProps) {
        try {
            final String sourceUrlFilePath = ProductUrlLoader.getInstance().getValue("mdm_updates_check_url");
            if (this.checkAndUpdateSourceUrls(sourceUrlFilePath) == 0) {
                this.logger.log(Level.INFO, "********************MDM Dynamic Task property file Updated Successfully********************");
            }
            else {
                this.logger.log(Level.INFO, "********************MDM Dynamic Task property file not replced********************");
            }
            final String sourceUrlPropertyfile = this.downloadsUtil.mdmtemporaryDownloadspath() + "mdmexternaldownloads.props";
            final Properties sourceUrlProperty = FileAccessUtil.readProperties(sourceUrlPropertyfile);
            if (sourceUrlProperty != null) {
                final int checkAndUpdateJar = this.checkAndUpdateJar(sourceUrlProperty);
                if (checkAndUpdateJar == 0) {
                    this.logger.log(Level.INFO, "********************MDM Dynamic Jar Updated Successfully********************");
                    if (this.replaceNewJar()) {
                        this.logger.log(Level.INFO, "*******************MDM Dynamic JAR replaced Successfully*******************");
                    }
                    else {
                        this.logger.log(Level.INFO, "********************Error in JAR replacement********************");
                    }
                }
                else {
                    this.logger.log(Level.INFO, "MDM Dynamic JAR Not updated with Error code", checkAndUpdateJar);
                }
                final int checkAndUpdateJson = this.checkAndUpdateJson(sourceUrlProperty);
                if (checkAndUpdateJson == 0) {
                    this.logger.log(Level.INFO, "********************Flash Message Json Updated Successfully********************");
                }
                else {
                    this.logger.log(Level.INFO, "Flash Message JSON Not updated with Error code", checkAndUpdateJson);
                }
                final MDMFlashMessageUpdateTask flashMessageTask = new MDMFlashMessageUpdateTask();
                flashMessageTask.MDMFlashMessageupdate();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private int checkAndUpdateJar(final Properties sourceUrlProperty) throws Exception {
        final String updChkURL = sourceUrlProperty.getProperty("jarUrl");
        final String outFileName = this.downloadsUtil.mdmtemporaryDownloadspath() + "MEMDMDynamicTaskHandler.jar";
        final String lastModifiedTime = MDMUpdatesParamHandler.getMDMUpdParameter("MDMDYNAMIC_JAR_MODIFIED_AT");
        final int downloadStatus = this.downloadsUtil.copyRemotefiles(updChkURL, outFileName, lastModifiedTime);
        if (downloadStatus == 0) {
            MDMUpdatesParamHandler.addorUpdateMDMUpdParams("MDMDYNAMIC_JAR_MODIFIED_AT", System.currentTimeMillis() + "");
        }
        return downloadStatus;
    }
    
    private int checkAndUpdateJson(final Properties sourceUrlProperty) throws Exception {
        final String updChkURL = sourceUrlProperty.getProperty("flashMessagejsonUrl");
        final String outFileName = this.downloadsUtil.mdmtemporaryDownloadspath() + "dcmdmflashupdates.json";
        final String lastModifiedTime = MDMUpdatesParamHandler.getMDMUpdParameter("MDMFLASH_JSON_MODIFIED_AT");
        final int downloadStatus = this.downloadsUtil.copyRemotefiles(updChkURL, outFileName, lastModifiedTime);
        if (downloadStatus == 0) {
            MDMUpdatesParamHandler.addorUpdateMDMUpdParams("MDMFLASH_JSON_MODIFIED_AT", System.currentTimeMillis() + "");
        }
        return downloadStatus;
    }
    
    private int checkAndUpdateSourceUrls(final String updChkURL) throws Exception {
        final String outFileName = this.downloadsUtil.mdmtemporaryDownloadspath() + "mdmexternaldownloads.props";
        final String lastModifiedTime = MDMUpdatesParamHandler.getMDMUpdParameter("MDM_PROPERTY_FILE__MODIFIED_AT");
        final int downloadStatus = this.downloadsUtil.copyRemotefiles(updChkURL, outFileName, lastModifiedTime);
        if (downloadStatus == 0) {
            MDMUpdatesParamHandler.addorUpdateMDMUpdParams("MDM_PROPERTY_FILE__MODIFIED_AT", System.currentTimeMillis() + "");
        }
        return downloadStatus;
    }
    
    private boolean replaceNewJar() {
        Boolean jarReplaceStatus = false;
        try {
            final String temporaryPath = this.downloadsUtil.mdmtemporaryDownloadspath() + "MEMDMDynamicTaskHandler.jar";
            final String PermenantPath = this.downloadsUtil.mdmpermenantDownloadspath() + "MEMDMDynamicTaskHandler.jar";
            final String urlString = this.downloadsUtil.mdmtemporaryDownloadspath() + "mdmexternaldownloads.props";
            if (this.downloadsUtil.jarSignVerification(temporaryPath) && this.downloadsUtil.jarCheckSumVerification(urlString, temporaryPath)) {
                jarReplaceStatus = this.downloadsUtil.FileReplacement(temporaryPath, PermenantPath);
            }
            else {
                this.logger.log(Level.SEVERE, "......Checksum Mismatch occurs....");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
        return jarReplaceStatus;
    }
}
