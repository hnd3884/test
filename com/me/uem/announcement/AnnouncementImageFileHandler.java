package com.me.uem.announcement;

import java.io.File;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;

public class AnnouncementImageFileHandler extends DCMetaDataUtil
{
    private static AnnouncementImageFileHandler handler;
    private static final String ANNOUNCEMENT_IMG = "announcementImg";
    
    public static AnnouncementImageFileHandler getInstance() {
        if (AnnouncementImageFileHandler.handler == null) {
            AnnouncementImageFileHandler.handler = new AnnouncementImageFileHandler();
        }
        return AnnouncementImageFileHandler.handler;
    }
    
    public String checkAndCreateAnnouncementImgDir(final Long customerID) {
        final String metaDataDirStr = this.getAnnouncementImagePathWithParentDir(customerID);
        try {
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(metaDataDirStr)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(metaDataDirStr);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while creating Announcement directory ", e);
        }
        return metaDataDirStr;
    }
    
    private String getAnnouncementImagePathWithParentDir(final Long customerID) {
        final String announcementImgPath = DCMetaDataUtil.getInstance().getClientDataDir(customerID) + File.separator + "announcementImg";
        return announcementImgPath;
    }
    
    public String getAnnouncementImgRelativeDirPath(final Long customerID) {
        String annRelativeDirPath = null;
        try {
            final String clientDataDirRelative = this.getClientDataDirRelative(customerID);
            annRelativeDirPath = clientDataDirRelative + File.separator + "announcementImg";
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while creating Announcement directory ", e);
        }
        return annRelativeDirPath;
    }
    
    private String generateUniqueFile(final String sourceFilePath, final Long customerId) {
        final String newFilePath = this.checkAndCreateAnnouncementImgDir(customerId);
        final File srcFile = new File(sourceFilePath);
        final String fileName = ApiFactoryProvider.getFileAccessAPI().getFileName(sourceFilePath);
        final String extension = fileName.split("\\.")[fileName.split("\\.").length - 1];
        final String newFileName = "ANN_" + System.currentTimeMillis() + "." + extension;
        return newFilePath + File.separator + newFileName;
    }
    
    public String saveAnnouncementImg(final String souceFilePath, final Long customerId) throws AnnouncementException {
        try {
            String destinationloc = this.generateUniqueFile(souceFilePath, customerId);
            ApiFactoryProvider.getFileAccessAPI().writeFile(destinationloc, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(souceFilePath));
            final File srcFile = new File(destinationloc);
            final String fileName = ApiFactoryProvider.getFileAccessAPI().getFileName(destinationloc);
            destinationloc = "https://%ServerName%:%ServerPort%" + File.separator + this.getAnnouncementImgRelativeDirPath(customerId) + File.separator + fileName;
            return destinationloc;
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new AnnouncementException(e.getMessage());
        }
    }
}
