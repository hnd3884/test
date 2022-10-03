package com.me.mdm.server.windows.cspmeta;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.io.File;
import java.util.logging.Level;
import java.text.MessageFormat;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class DDFDownloader
{
    private static final String DDF_FILES_ZIP_URL_KEY = "MDM_WINDOWS_DDF_FILE_ZIP";
    private static final String MDM_DDF_ZIP_FILE_NAME = "ddfFiles.zip";
    private static String mdm_DDF_DIRECTORY;
    private static String mdm_DDF_ZIP_FILE_PATH;
    private static final Logger LOGGER;
    
    public DDFDownloader() {
        final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        DDFDownloader.mdm_DDF_DIRECTORY = MessageFormat.format(DDFDownloader.mdm_DDF_DIRECTORY, serverHome);
        DDFDownloader.mdm_DDF_ZIP_FILE_PATH = MessageFormat.format(DDFDownloader.mdm_DDF_ZIP_FILE_PATH, serverHome);
    }
    
    public String getDDFFolderPath() throws Exception {
        DDFDownloader.LOGGER.log(Level.FINE, "Checking if ddf files are downloaded");
        final File ddfFilesDirectory = new File(DDFDownloader.mdm_DDF_DIRECTORY);
        if (ddfFilesDirectory.exists() && ddfFilesDirectory.isDirectory()) {
            DDFDownloader.LOGGER.log(Level.FINE, "DDF folder is already present");
        }
        else {
            if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("CSPMetaDDFFileSupport")) {
                return null;
            }
            DDFDownloader.LOGGER.log(Level.FINE, "DDF folder not present, now downloading");
            this.deleteDDFZipFileIfExists();
            final String downloadURL = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("MDM_WINDOWS_DDF_FILE_ZIP");
            final DownloadStatus downloadstatus = DownloadManager.getInstance().downloadFile(downloadURL, DDFDownloader.mdm_DDF_ZIP_FILE_PATH, new SSLValidationType[0]);
            if (downloadstatus.getStatus() != 0) {
                DDFDownloader.LOGGER.log(Level.WARNING, "Unable to download ddf zip{0}", downloadstatus.getErrorMessage());
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            DDFDownloader.LOGGER.log(Level.INFO, "DDF folder zip downloaded successfully, Starting to unzip...");
            if (!ddfFilesDirectory.mkdir()) {
                DDFDownloader.LOGGER.log(Level.WARNING, "Unable to create ddf folder ");
                this.deleteDDFZipFileIfExists();
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            this.unzipDDFFiles();
            this.deleteDDFZipFileIfExists();
            DDFDownloader.LOGGER.log(Level.INFO, "unzipped and deleted zip file");
        }
        return DDFDownloader.mdm_DDF_DIRECTORY;
    }
    
    public void deleteDDFFilesFolder() {
        try {
            final File file = new File(DDFDownloader.mdm_DDF_DIRECTORY);
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            }
        }
        catch (final Exception ex) {
            DDFDownloader.LOGGER.log(Level.SEVERE, "Exception in deleteFolder...", ex);
        }
    }
    
    private void unzipDDFFiles() throws Exception {
        if (!ApiFactoryProvider.getZipUtilAPI().unzip(DDFDownloader.mdm_DDF_ZIP_FILE_PATH, DDFDownloader.mdm_DDF_DIRECTORY, true, true, new String[0])) {
            this.deleteDDFFilesFolder();
            this.deleteDDFZipFileIfExists();
            DDFDownloader.LOGGER.log(Level.SEVERE, "Extraction of DDF files from zip failed");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void deleteDDFZipFileIfExists() throws Exception {
        Files.deleteIfExists(Paths.get(DDFDownloader.mdm_DDF_ZIP_FILE_PATH, new String[0]));
    }
    
    static {
        DDFDownloader.mdm_DDF_DIRECTORY = "{0}" + File.separator + "mdm" + File.separator + "ddffiles";
        DDFDownloader.mdm_DDF_ZIP_FILE_PATH = "{0}" + File.separator + "mdm" + File.separator + "ddfFiles.zip";
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
