package com.me.mdm.onpremise.server.admin.task;

import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.FileUtil;
import java.io.File;
import com.me.mdm.server.util.MDMUpdatesParamHandler;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.mdm.onpremise.server.admin.MDMExternalDownloadsUtil;
import java.util.logging.Logger;

public class MDMFlashMessageUpdateTask
{
    private final Logger logger;
    MDMExternalDownloadsUtil downloadsUtil;
    
    public MDMFlashMessageUpdateTask() {
        this.logger = Logger.getLogger("MDMDynamicJarExecutionLogger");
        this.downloadsUtil = new MDMExternalDownloadsUtil();
    }
    
    public void MDMFlashMessageupdate() {
        try {
            final HashMap messageFiles = this.downloadsUtil.dynamicJarExecution();
            if (!messageFiles.isEmpty()) {
                this.logger.log(Level.INFO, "JAR executed Successfully and Update Message files");
                this.mdmFlashMessageUpdate(messageFiles);
            }
            else {
                this.logger.log(Level.INFO, "No messages Satisfied criteria");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private void mdmFlashMessageUpdate(final HashMap messageFiles) {
        Boolean downloadstatus = false;
        try {
            final String existingMDMFlashVersion = MDMUpdatesParamHandler.getMDMUpdParameter("MDM_FLASH_NEWS_VERSION");
            final String flashVersion = messageFiles.get("Version");
            final String flashHtmlFile = messageFiles.get("FileName");
            final String flashImage = messageFiles.get("Images");
            if (existingMDMFlashVersion == null || !existingMDMFlashVersion.equalsIgnoreCase(flashVersion)) {
                downloadstatus = this.downloadFlashMsgFiles(flashHtmlFile, flashImage);
            }
            if (downloadstatus) {
                MDMUpdatesParamHandler.addorUpdateMDMUpdParams("MDM_FLASH_NEWS_VERSION", flashVersion);
                this.logger.log(Level.INFO, "Flash Message Files download successfully");
            }
            else {
                this.logger.log(Level.INFO, "Flash Message Files download failed");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean downloadFlashMsgFiles(final String flashHtmlFile, final String falshImages) {
        boolean downloadStatus = Boolean.FALSE;
        try {
            final String outFileName = this.downloadsUtil.mdmtemporaryDownloadspath() + "dcmdmflashmsg" + File.separator;
            final FileUtil fileUtil = new FileUtil();
            fileUtil.deleteFolderFiles(new File(outFileName));
            final String sourceUrlPropertyfile = this.downloadsUtil.mdmtemporaryDownloadspath() + "mdmexternaldownloads.props";
            final Properties sourceUrlProperty = FileAccessUtil.readProperties(sourceUrlPropertyfile);
            final String inFileURL = sourceUrlProperty.getProperty("messageFilesUrl");
            final String[] imageList = falshImages.split(",");
            final String htmlpath = inFileURL + flashHtmlFile;
            final String outHtmlPath = outFileName + "dcmdmflashMsg.html";
            final String lastModifiedTime = MDMUpdatesParamHandler.getMDMUpdParameter("MDM_FLASHFILES_MODIFIED_AT");
            this.downloadsUtil.copyRemotefiles(htmlpath, outHtmlPath, lastModifiedTime);
            downloadStatus = new File(outFileName).exists();
            if (downloadStatus) {
                for (final String imageList2 : imageList) {
                    this.downloadsUtil.copyRemotefiles(inFileURL + imageList2, outFileName + imageList2, lastModifiedTime);
                    downloadStatus = new File(outFileName + imageList2).exists();
                    if (!downloadStatus) {
                        break;
                    }
                }
            }
            if (!downloadStatus) {
                this.logger.log(Level.WARNING, "Download of Flash Message Files Failed", flashHtmlFile);
                fileUtil.deleteFolderFiles(new File(outFileName));
            }
            else {
                MDMUpdatesParamHandler.addorUpdateMDMUpdParams("MDM_FLASHFILES_MODIFIED_AT", System.currentTimeMillis() + "");
            }
            return downloadStatus;
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "FlashMessage: Caught exception while downloadFlashMsgFiles", ex);
            try {
                final String outFileName2 = this.downloadsUtil.mdmtemporaryDownloadspath() + "dcmdmflashmsg" + File.separator;
                new FileUtil().deleteFolderFiles(new File(outFileName2));
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "FlashMessage: Caught exception while downloadFlashMsgFiles & delete file ", e);
            }
            return Boolean.FALSE;
        }
    }
}
