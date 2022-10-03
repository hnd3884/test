package com.me.mdm.files;

import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.FileInputStream;
import java.io.File;
import java.util.logging.Logger;

public class MDMFileUtil
{
    private static final Logger LOGGER;
    public static Integer fileCacheTimeTTL;
    public static Integer fileSizeCacheThreshold;
    public static Boolean fileCacheEnabled;
    
    public static boolean uploadFileToDirectory(final File file, final String destination, final String fileName) {
        boolean fileUploaded = false;
        final String folderPath = destination;
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(folderPath)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(folderPath);
            }
            final String completeFilePath = folderPath + File.separator + fileName;
            ApiFactoryProvider.getFileAccessAPI().writeFile(completeFilePath, (InputStream)stream);
            fileUploaded = true;
            MDMFileUtil.LOGGER.log(Level.INFO, "{0} - File uploaded successfully.", fileName);
        }
        catch (final Exception e) {
            fileUploaded = false;
            MDMFileUtil.LOGGER.log(Level.WARNING, "Exception while uploading certificate File", e);
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (final IOException e2) {
                MDMFileUtil.LOGGER.log(Level.SEVERE, "Exception in closing input stream", e2);
            }
        }
        finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (final IOException e3) {
                MDMFileUtil.LOGGER.log(Level.SEVERE, "Exception in closing input stream", e3);
            }
        }
        return fileUploaded;
    }
    
    public static boolean uploadFileToDirectory(final String sourcePath, final String destinationPath, final String fileName) {
        final String folderPath = destinationPath;
        boolean fileUploaded;
        try {
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(folderPath)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(folderPath);
            }
            final String completeFilePath = folderPath + File.separator + fileName;
            ApiFactoryProvider.getFileAccessAPI().copyFile(sourcePath, completeFilePath);
            final String webdir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
            if (!sourcePath.contains(webdir)) {
                ApiFactoryProvider.getFileAccessAPI().deleteDirectory(ApiFactoryProvider.getFileAccessAPI().getParent(sourcePath));
            }
            fileUploaded = true;
            MDMFileUtil.LOGGER.log(Level.INFO, "{0} - File uploaded successfully.", fileName);
        }
        catch (final Exception e) {
            fileUploaded = false;
            MDMFileUtil.LOGGER.log(Level.WARNING, "Exception while uploading Webclips File", e);
        }
        return fileUploaded;
    }
    
    public static Boolean testForPathTraversal(final String filePath) {
        Boolean allow = Boolean.FALSE;
        if (filePath.contains("../") || filePath.contains("..\\")) {
            if (filePath.lastIndexOf("../") == 0 || filePath.lastIndexOf("..\\") == 0) {
                allow = Boolean.TRUE;
            }
        }
        else {
            allow = Boolean.TRUE;
        }
        return allow;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
        MDMFileUtil.fileCacheTimeTTL = 900;
        MDMFileUtil.fileSizeCacheThreshold = 102400;
        MDMFileUtil.fileCacheEnabled = false;
        try {
            MDMFileUtil.fileCacheTimeTTL = Integer.parseInt(MDMUtil.getInstance().getMDMApplicationProperties().getProperty("install.application.command.file.cacheTimeTTL"));
            MDMFileUtil.fileSizeCacheThreshold = Integer.parseInt(MDMUtil.getInstance().getMDMApplicationProperties().getProperty("install.application.command.file.fileSizeThreshold"));
            MDMFileUtil.fileCacheEnabled = MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("install.application.command.file.cache.enabled");
        }
        catch (final Exception e) {
            MDMFileUtil.LOGGER.log(Level.SEVERE, "Exception while fetching the value - ", e);
        }
    }
}
