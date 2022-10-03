package com.me.ems.onpremise.license;

import java.util.Hashtable;
import com.adventnet.tools.prevalent.Wield;
import com.adventnet.tools.prevalent.CMDClass;
import com.adventnet.tools.prevalent.ReadLicense;
import com.adventnet.tools.prevalent.Indication;
import com.adventnet.tools.prevalent.DataClass;
import com.adventnet.tools.prevalent.InputFileParser;
import com.adventnet.tools.prevalent.LUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.ProductCodeMapping;
import java.io.File;
import java.util.logging.Logger;

public class LicenseMigration
{
    private static final String EMS_LICENSE_SUPPORTED = "isEMSLicenseSupported";
    private static final String PRODUCT_DAT = "product.dat";
    private static final String PETINFO_DAT = "petinfo.dat";
    private static Logger logger;
    private static final String FREE_LICENSE = "Free.xml";
    
    private static void migrateProductDat(final String productNameAttr) throws Exception {
        final String serverHome = System.getProperty("server.home");
        final String licenseFolderLocation = serverHome + File.separator + "lib";
        String emsLicenseFolderPath = licenseFolderLocation + File.separator + "seamlessmigration";
        if (productNameAttr.equals("Unified Endpoint Management and Security")) {
            emsLicenseFolderPath = emsLicenseFolderPath + File.separator + "uemslicense";
        }
        else {
            emsLicenseFolderPath = emsLicenseFolderPath + File.separator + "oldlicense" + File.separator + ProductCodeMapping.getProductCode(productNameAttr).get(0).toLowerCase();
        }
        try {
            final File emsLicenseFolder = new File(emsLicenseFolderPath);
            if (!emsLicenseFolder.exists()) {
                throw new Exception("EMS License Not Bundled for SeamLessMigration!");
            }
            String src = emsLicenseFolderPath + File.separator + "product.dat";
            String dst = licenseFolderLocation + File.separator + "product.dat";
            if (ApiFactoryProvider.getFileAccessAPI().copyFile(src, dst)) {
                src = emsLicenseFolderPath + File.separator + "petinfo.dat";
                dst = licenseFolderLocation + File.separator + "petinfo.dat";
                ApiFactoryProvider.getFileAccessAPI().copyFile(src, dst);
            }
        }
        catch (final Exception e) {
            LicenseMigration.logger.log(Level.INFO, "Exception while copying log file", e);
        }
    }
    
    private static void backupLicenseMigrationFile() {
        final String serverHome = System.getProperty("server.home");
        final String licenseFolderLocation = serverHome + File.separator + "lib";
        final String licenseBackupFolderPath = licenseFolderLocation + File.separator + "seamlessmigration" + File.separator + "backup";
        try {
            final File licenseBackupFolder = new File(licenseBackupFolderPath);
            licenseBackupFolder.deleteOnExit();
            licenseBackupFolder.mkdir();
            String src = licenseFolderLocation + File.separator + "product.dat";
            String dst = licenseBackupFolderPath + File.separator + "product.dat";
            ApiFactoryProvider.getFileAccessAPI().copyFile(src, dst);
            src = licenseFolderLocation + File.separator + "petinfo.dat";
            dst = licenseBackupFolderPath + File.separator + "petinfo.dat";
            ApiFactoryProvider.getFileAccessAPI().copyFile(src, dst);
        }
        catch (final Exception e) {
            LicenseMigration.logger.log(Level.INFO, "Exception while copying log file", e);
        }
    }
    
    private static void restoreLicenseMigrationFile() {
        LicenseMigration.logger.log(Level.INFO, "Going to restore ");
        final String serverHome = System.getProperty("server.home");
        final String licenseFolderLocation = serverHome + File.separator + "lib";
        final String licenseBackupFolderPath = licenseFolderLocation + File.separator + "seamlessmigration" + File.separator + "backup";
        try {
            final File licenseBackupFolder = new File(licenseBackupFolderPath);
            String src = licenseBackupFolderPath + File.separator + "product.dat";
            String dst = licenseFolderLocation + File.separator + "product.dat";
            ApiFactoryProvider.getFileAccessAPI().copyFile(src, dst);
            src = licenseBackupFolderPath + File.separator + "petinfo.dat";
            dst = licenseFolderLocation + File.separator + "petinfo.dat";
            ApiFactoryProvider.getFileAccessAPI().copyFile(src, dst);
            LicenseMigration.logger.log(Level.INFO, "License Migration Failed, and previous backup has been restored");
        }
        catch (final Exception e) {
            LicenseMigration.logger.log(Level.INFO, "Exception while copying log file during restore", e);
        }
    }
    
    public static void handleMigration(final String licensePath) {
        try {
            final boolean isFreeLicenseChanged = false;
            if (!isTrialVersion(licensePath) && EMSProductUtil.isEMSFlowSupportedForCurrentProduct()) {
                try {
                    final String productSettingsFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "productSettings.conf";
                    final Properties productProps = FileAccessUtil.readProperties(productSettingsFile);
                    boolean result = true;
                    if (isMigrationReq(licensePath)) {
                        backupLicenseMigrationFile();
                        migrateProductDat(getProductNameAttr(licensePath));
                        result = verifyLicenseInPath(licensePath);
                        if (!result) {
                            LicenseMigration.logger.log(Level.INFO, "License is not valid");
                            restoreLicenseMigrationFile();
                            throw new Exception("Error : License Migration Failed!");
                        }
                    }
                    changeFreeLicense(licensePath);
                    ((Hashtable<String, String>)productProps).put("isEMSLicenseSupported", "true");
                    FileAccessUtil.storeProperties(productProps, productSettingsFile, false);
                }
                catch (final Exception e) {
                    LicenseMigration.logger.log(Level.INFO, "Exception while handling migration", e);
                    if (isFreeLicenseChanged) {
                        restoreFreeLicense();
                    }
                }
            }
        }
        catch (final Exception e2) {
            LicenseMigration.logger.log(Level.INFO, "Exception while checking trial version", e2);
        }
    }
    
    private static void restoreFreeLicense() {
        LicenseMigration.logger.log(Level.INFO, "Going to restore ");
        final String serverHome = System.getProperty("server.home");
        final String licenseFolderLocation = serverHome + File.separator + "lib";
        final String licenseBackupFolderPath = licenseFolderLocation + File.separator + "seamlessmigration" + File.separator + "backup";
        try {
            final File licenseBackupFolder = new File(licenseBackupFolderPath);
            if (!licenseBackupFolder.exists() || licenseBackupFolder.mkdir()) {
                final String src = licenseBackupFolderPath + File.separator + "Free.xml";
                final String dst = licenseFolderLocation + File.separator + "Free.xml";
                ApiFactoryProvider.getFileAccessAPI().copyFile(src, dst);
                LicenseMigration.logger.log(Level.INFO, "License Migration Failed, and previous backup has been restored");
            }
        }
        catch (final Exception e) {
            LicenseMigration.logger.log(Level.INFO, "Exception while copying log file during restore", e);
        }
    }
    
    private static void backupFreeLicense() {
        final String serverHome = System.getProperty("server.home");
        final String licenseFolderLocation = serverHome + File.separator + "lib";
        final String licenseBackupFolderPath = licenseFolderLocation + File.separator + "seamlessmigration" + File.separator + "backup";
        try {
            final File licenseBackupFolder = new File(licenseBackupFolderPath);
            if (licenseBackupFolder.exists() || licenseBackupFolder.mkdir()) {
                final String src = licenseFolderLocation + File.separator + "Free.xml";
                final String dst = licenseBackupFolderPath + File.separator + "Free.xml";
                ApiFactoryProvider.getFileAccessAPI().copyFile(src, dst);
            }
        }
        catch (final Exception e) {
            LicenseMigration.logger.log(Level.INFO, "Exception while copying log file", e);
        }
    }
    
    private static boolean isMigrationReq(final String licensePath) throws Exception {
        final String productNameAttrOfNewLicense = getProductNameAttr(licensePath);
        final String productNameAttrOfOldLicense = getProductNameAttr(System.getProperty("server.home") + File.separator + LUtil.getLicenseDir() + File.separator + "AdventNetLicense.xml");
        return !productNameAttrOfNewLicense.equals(productNameAttrOfOldLicense);
    }
    
    private static boolean isTrialVersion(final String licensePath) throws Exception {
        final InputFileParser parser = new InputFileParser(licensePath);
        final DataClass data = parser.getDataClass();
        return data.getUsers().get(0).toString().equals("Evaluation User");
    }
    
    private static String getProductNameAttr(final String licensePath) {
        String name = "";
        try {
            final InputFileParser parser = new InputFileParser(licensePath);
            final DataClass data = parser.getDataClass();
            final String productType = data.getDetails("ID").getProductLicenseType();
            if (productType.equals("Vulnerability")) {
                return "ManageEngine Vulnerability Manager Plus";
            }
            name = data.getDetails("ID").getProductName();
        }
        catch (final Exception e) {
            LicenseMigration.logger.log(Level.SEVERE, "Exception while getting EMS License Version", e);
        }
        return name;
    }
    
    public static boolean licenseMig(final String licDir) throws Exception {
        LUtil.setLicenseDir(licDir);
        final Indication ind = Indication.getInstance();
        ind.deSerialize();
        final int productNameInt = new ReadLicense().readLicense().getLicenseObject()[0];
        ind.addProductEntry(productNameInt);
        ind.serialize();
        ind.deSerialize();
        return ind.getProductNameInt() == productNameInt;
    }
    
    private static boolean changeFreeLicense(final String licensePath) throws Exception {
        String productName;
        final String productNameAttr = productName = getProductNameAttr(licensePath);
        if (productNameAttr.equals("Unified Endpoint Management and Security")) {
            productName = getCategoryAttr(licensePath);
        }
        final String serverHome = System.getProperty("server.home");
        String newFreeLicenseLocation = serverHome + File.separator + "lib" + File.separator + "seamlessmigration";
        try {
            if (productNameAttr.equals("Unified Endpoint Management and Security")) {
                newFreeLicenseLocation = newFreeLicenseLocation + File.separator + "uemslicense";
            }
            else {
                newFreeLicenseLocation = newFreeLicenseLocation + File.separator + "oldlicense";
            }
            newFreeLicenseLocation = newFreeLicenseLocation + File.separator + ProductCodeMapping.getProductCode(productName).get(0).toLowerCase() + File.separator + "Free.xml";
            if (new File(newFreeLicenseLocation).exists()) {
                final String freeLicense = serverHome + File.separator + "lib" + File.separator + "Free.xml";
                ApiFactoryProvider.getFileAccessAPI().copyFile(newFreeLicenseLocation, freeLicense);
            }
        }
        catch (final Exception ex) {
            LicenseMigration.logger.log(Level.SEVERE, "Exception while changing free license", ex);
            throw ex;
        }
        return true;
    }
    
    private static boolean verifyLicenseInPath(final String licensePath) {
        final String home = System.getProperty("server.home");
        final CMDClass cmd = new CMDClass();
        final Wield w = Wield.getInstance();
        final String user = cmd.getUserList(licensePath).elementAt(0);
        return w.doValidation(home, user, licensePath, false, true);
    }
    
    private static String getCategoryAttr(final String licensePath) {
        String categoryAttr = "ManageEngine Desktop Central";
        try {
            final InputFileParser parser = new InputFileParser(licensePath);
            final DataClass data = parser.getDataClass();
            categoryAttr = data.getDetails("ID").getProductCategory();
        }
        catch (final Exception var4) {
            LicenseMigration.logger.log(Level.SEVERE, "Exception while getting EMS License Version", var4);
        }
        return categoryAttr;
    }
    
    static {
        LicenseMigration.logger = Logger.getLogger("LicenseLogger");
    }
}
