package com.me.ems.onpremise.server.util;

import java.util.Hashtable;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.metrack.METrackerHandler;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.exception.NativeException;
import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.logging.Logger;

public class ServerSettingsUtil
{
    private static Logger logger;
    
    public static boolean getDefaultClientSettings() throws SyMException {
        final String confFile = "../conf/trayicon.conf";
        try {
            final Properties props = FileAccessUtil.readProperties(confFile);
            final String launchBrowser = props.getProperty("start.webclient");
            ServerSettingsUtil.logger.log(Level.INFO, "Launch Browser value in Conf File: " + launchBrowser);
            return launchBrowser == null || !launchBrowser.equalsIgnoreCase("false");
        }
        catch (final Exception e) {
            ServerSettingsUtil.logger.log(Level.WARNING, "Exception while getting client startup settings from conf file: " + confFile, e);
            throw new SyMException(1002, (Throwable)e);
        }
    }
    
    public static boolean getDefaultDblocksUploadSettings() {
        try {
            final DataObject dbLockSettingsDo = SyMUtil.getPersistence().get("DbLockSettings", (Criteria)null);
            final Row settingsRow = dbLockSettingsDo.getRow("DbLockSettings");
            final Boolean automatic_mail = (Boolean)settingsRow.get("IS_AUTOMATIC");
            return automatic_mail;
        }
        catch (final DataAccessException ex) {
            ServerSettingsUtil.logger.log(Level.SEVERE, "Exception while retrieving dblock settings table", (Throwable)ex);
            return false;
        }
    }
    
    public static boolean getMETrackSettings() {
        boolean isTrackingEnabled = true;
        String setting = SyMUtil.getSyMParameter("ME_TRACK_SETTINGS");
        if (setting == null) {
            SyMUtil.updateSyMParameter("ME_TRACK_SETTINGS", "true");
            setting = "true";
        }
        isTrackingEnabled = Boolean.valueOf(setting);
        return isTrackingEnabled;
    }
    
    public static boolean isStartServerOnBootup() throws NativeException, Exception {
        String startServerOnBootup = null;
        try {
            try {
                final String serviceName = getServiceName();
                final String startupType = WinAccessProvider.getInstance().getServiceStartupType(serviceName);
                ServerSettingsUtil.logger.log(Level.FINEST, "Service startup type retrieved using native call is: " + startupType + " For server :" + serviceName);
                if (startupType != null && startupType.equalsIgnoreCase("Manual")) {
                    startServerOnBootup = "false";
                }
                else {
                    startServerOnBootup = "true";
                }
            }
            catch (final NativeException ex) {
                ServerSettingsUtil.logger.log(Level.WARNING, "Caught NativeException while getting service startup type from the native.", (Throwable)ex);
            }
            catch (final Exception ex2) {
                ServerSettingsUtil.logger.log(Level.WARNING, "Caught exception while getting service startup type from the native.", ex2);
            }
            String startUpTypeFromDB = SyMUtil.getSyMParameter("START_SERVER_ON_BOOTUP");
            if (startServerOnBootup == null && startUpTypeFromDB != null) {
                startServerOnBootup = startUpTypeFromDB;
            }
            if (startServerOnBootup == null) {
                startServerOnBootup = "true";
            }
            if (startUpTypeFromDB == null || !startUpTypeFromDB.equalsIgnoreCase(startServerOnBootup)) {
                startUpTypeFromDB = startServerOnBootup;
                SyMUtil.updateSyMParameter("START_SERVER_ON_BOOTUP", startServerOnBootup);
            }
        }
        catch (final Exception ex2) {
            throw ex2;
        }
        return Boolean.valueOf(startServerOnBootup);
    }
    
    public static String getServiceName() {
        String value = null;
        try {
            final String path = new File(System.getProperty("server.home")).getCanonicalPath();
            String fname = path + File.separator + "conf" + File.separator + "custom_wrapperservice.conf";
            if (!new File(fname).exists()) {
                fname = System.getProperty("server.home") + File.separator + "conf" + File.separator + "wrapper.conf";
            }
            final Properties props = StartupUtil.getProperties(fname);
            value = props.getProperty("wrapper.name");
        }
        catch (final Exception ex) {
            ServerSettingsUtil.logger.log(Level.SEVERE, "Exception in getServiceName", ex);
        }
        return value;
    }
    
    public static void clientSettings(final Boolean launchDefaultBrowser) throws SyMException {
        final String confFile = "../conf/trayicon.conf";
        try {
            ServerSettingsUtil.logger.log(Level.INFO, "Launch Browser value from client: " + launchDefaultBrowser);
            final Properties props = new Properties();
            props.setProperty("start.webclient", launchDefaultBrowser.toString());
            FileAccessUtil.storeProperties(props, confFile, true);
        }
        catch (final Exception e) {
            ServerSettingsUtil.logger.log(Level.WARNING, "Exception while updating launch browser client flag in conf file: " + confFile, e);
            throw new SyMException(1002, (Throwable)e);
        }
    }
    
    public static void setAutomaticUpload(final boolean isDblocksUpload) {
        try {
            final DataObject dbLockSettingsDo = SyMUtil.getPersistence().get("DbLockSettings", (Criteria)null);
            final Row settingsRow = dbLockSettingsDo.getRow("DbLockSettings");
            settingsRow.set("IS_AUTOMATIC", (Object)isDblocksUpload);
            dbLockSettingsDo.updateRow(settingsRow);
            SyMUtil.getPersistence().update(dbLockSettingsDo);
        }
        catch (final DataAccessException ex) {
            ServerSettingsUtil.logger.log(Level.SEVERE, "Exception while updating dblock settings table..", (Throwable)ex);
        }
    }
    
    public static void setMETrackSettings(final boolean isTrackingEnabled) {
        String setting = "true";
        if (!isTrackingEnabled) {
            setting = "false";
            METrackerHandler.disableTracking();
        }
        else {
            METrackerHandler.enableTracking();
        }
        SyMUtil.updateSyMParameter("ME_TRACK_SETTINGS", setting);
    }
    
    public static void setServerStartOnBootup(final boolean bool) throws NativeException, Exception {
        try {
            String startupType = "Automatic";
            if (!bool) {
                startupType = "Manual";
            }
            final String serviceName = getServiceName();
            ServerSettingsUtil.logger.log(Level.FINEST, "Going to change the startup type of service: " + serviceName + " to: " + startupType);
            WinAccessProvider.getInstance().setServiceStartupType(serviceName, startupType);
            SyMUtil.updateSyMParameter("START_SERVER_ON_BOOTUP", String.valueOf(bool));
        }
        catch (final NativeException ex) {
            ServerSettingsUtil.logger.log(Level.WARNING, "NativeException in setServerStartOnBootup... " + ex);
            throw ex;
        }
        catch (final Exception ex2) {
            ServerSettingsUtil.logger.log(Level.WARNING, "Exception in setServerStartOnBootup... " + ex2);
            throw ex2;
        }
    }
    
    public static void updateEmailAlertInfo(final boolean isEmailAlertEnabled, String emailIDs) throws Exception {
        try {
            emailIDs = emailIDs.trim();
            SyMUtil.addOrUpdateEmailAddr("ServerStartupFailure", isEmailAlertEnabled, emailIDs);
            final Properties props = ApiFactoryProvider.getMailSettingAPI().getMailServerDetailsProps();
            props.setProperty("receiverAddress", emailIDs);
            props.setProperty("isEmailAlertEnabled", String.valueOf(isEmailAlertEnabled));
            saveSmtpConfigIntoFile(props);
        }
        catch (final Exception e) {
            ServerSettingsUtil.logger.log(Level.SEVERE, "Exception occurred while updating the email alert info for server startup failure..", e);
        }
    }
    
    public static void saveSmtpConfigIntoFile(final Properties prop) {
        try {
            final String mailConfig = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "user-conf" + File.separator + "mail-settings.props";
            ServerSettingsUtil.logger.log(Level.INFO, "Mail Config = " + mailConfig);
            ServerSettingsUtil.logger.log(Level.INFO, "Props = " + prop);
            final File mailSettingsFile = new File(mailConfig);
            if (!mailSettingsFile.getParentFile().exists()) {
                ServerSettingsUtil.logger.log(Level.INFO, "Folder not available : " + mailSettingsFile.getParentFile().exists());
                ServerSettingsUtil.logger.log(Level.INFO, "Is Folder Created : " + mailSettingsFile.getParentFile().mkdir());
            }
            if (!mailSettingsFile.exists()) {
                mailSettingsFile.createNewFile();
            }
            final String password = CryptoUtil.encrypt(prop.getProperty("mail.smtp.password"));
            final String host = CryptoUtil.encrypt(prop.getProperty("mail.smtp.host"));
            final String port = CryptoUtil.encrypt(prop.getProperty("mail.smtp.port"));
            final Properties tmp = new Properties();
            tmp.setProperty("isEmailAlertEnabled", prop.getProperty("isEmailAlertEnabled"));
            tmp.setProperty("smtpHost", host);
            tmp.setProperty("smtpPort", port);
            tmp.setProperty("smtpUserName", prop.getProperty("mail.smtp.user"));
            tmp.setProperty("needAuthentication", prop.getProperty("mail.smtp.auth"));
            tmp.setProperty("smtpPassword", password);
            if (prop.containsKey("mail.fromAddress")) {
                tmp.setProperty("senderAddress", prop.getProperty("mail.fromAddress"));
            }
            tmp.setProperty("receiverAddress", prop.getProperty("receiverAddress"));
            tmp.setProperty("tlsEnabled", String.valueOf(((Hashtable<K, Object>)prop).get("mail.smtp.starttls.enable")));
            tmp.setProperty("smtpsEnabled", String.valueOf(((Hashtable<K, Object>)prop).get("mail.smtp.isSMTPSEnabled")));
            StartupUtil.storeProperties(tmp, mailConfig);
        }
        catch (final Exception e) {
            ServerSettingsUtil.logger.log(Level.SEVERE, "Exception occurred while creating mail-config file..", e);
        }
    }
    
    static {
        ServerSettingsUtil.logger = Logger.getLogger(ServerSettingsUtil.class.getName());
    }
}
