package com.me.mdm.server.deploy;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.io.File;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;

public class MDMMetaDataUtil extends DCMetaDataUtil
{
    public static MDMMetaDataUtil mdmMetaDataUtil;
    private static final Logger LOGGER;
    
    public static synchronized MDMMetaDataUtil getInstance() {
        if (MDMMetaDataUtil.mdmMetaDataUtil == null) {
            MDMMetaDataUtil.mdmMetaDataUtil = new MDMMetaDataUtil();
        }
        return MDMMetaDataUtil.mdmMetaDataUtil;
    }
    
    public String getMdmProfileFolderPath(final Long customerID, final String domainName, final Long collnID) {
        final String filePath = this.getMdmProfilePath(customerID, domainName) + File.separator + collnID;
        return filePath;
    }
    
    public String mdmProfileRelativeDirPath(final Long customerID, final Long collnID) {
        String mdmProfileRelativeDirPath = null;
        try {
            final String profileDataDirRelative = ProfileUtil.getInstance().getProfileRepoRelativeFolderPath(customerID);
            mdmProfileRelativeDirPath = profileDataDirRelative + File.separator + "profiles" + File.separator + collnID;
        }
        catch (final Exception e) {
            MDMMetaDataUtil.LOGGER.log(Level.WARNING, "Exception while creating mdm profile directory ", e);
        }
        return mdmProfileRelativeDirPath;
    }
    
    public String checkAndCreateMdmProfileDir(final Long customerID, final String dirName, final Long collnID) {
        final String metaDataDirStr = ProfileUtil.getInstance().getProfilePathWithParentDir(customerID, dirName) + File.separator + collnID;
        try {
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(metaDataDirStr)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(metaDataDirStr);
            }
        }
        catch (final Exception e) {
            MDMMetaDataUtil.LOGGER.log(Level.WARNING, "Exception while creating mdm profile directory ", e);
        }
        return metaDataDirStr;
    }
    
    public String getFileCanonicalPath(String fileName) {
        final StringBuilder cacheName = new StringBuilder();
        cacheName.append(fileName.substring(fileName.lastIndexOf(File.separator) + 1));
        cacheName.insert(0, "_");
        fileName = fileName.substring(0, fileName.lastIndexOf(File.separator));
        cacheName.insert(0, fileName.substring(fileName.lastIndexOf(File.separator) + 1));
        return cacheName.toString().replaceAll("\\.", "_");
    }
    
    @Deprecated
    public String getProfilePathWithParentDir(final Long customerID, final String dirName) {
        final String profilePath = DCMetaDataUtil.getInstance().getClientDataDir(customerID) + File.separator + "mdm" + File.separator + dirName.toLowerCase();
        return profilePath;
    }
    
    private String getMdmProfilePath(final Long customerID, final String dirName) {
        final String domainPath = ProfileUtil.getInstance().getProfileRepoRelativeFolderPath(customerID) + File.separator + dirName.toLowerCase();
        return domainPath;
    }
    
    public String mdmComplianceRelativeDirPath(final Long customerID, final Long collnID) {
        String mdmComplianceRelativeDirPath = null;
        try {
            final String profileDataDirRelative = ProfileUtil.getInstance().getProfileRepoRelativeFolderPath(customerID);
            mdmComplianceRelativeDirPath = profileDataDirRelative + File.separator + "compliance" + File.separator + collnID;
        }
        catch (final Exception e) {
            MDMMetaDataUtil.LOGGER.log(Level.WARNING, "Exception while creating mdm compliance directory ", e);
        }
        return mdmComplianceRelativeDirPath;
    }
    
    public String getLocationHistoryExportFilePath(final Long exportReqId, final Long customerId) {
        String filePath = this.getClientDataDirRelative(customerId);
        filePath = filePath + File.separator + "mdm" + File.separator + "exports" + File.separator + String.valueOf(exportReqId);
        return filePath;
    }
    
    static {
        MDMMetaDataUtil.mdmMetaDataUtil = null;
        LOGGER = Logger.getLogger(MDMMetaDataUtil.class.getName());
    }
}
