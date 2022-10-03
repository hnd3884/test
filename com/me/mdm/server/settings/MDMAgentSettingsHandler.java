package com.me.mdm.server.settings;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import com.me.mdm.server.doc.DocMgmt;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.common.MDMEventConstant;
import org.json.JSONException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.persistence.ReadOnlyPersistence;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Iterator;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.util.Properties;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.ArrayList;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.apps.handler.AppsAutoDeployment;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import java.util.logging.Level;
import java.util.List;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMAgentSettingsHandler
{
    public Logger logger;
    private static MDMAgentSettingsHandler agentSettingsHandler;
    public static final String PLAYSTORE_SAMSUNG_URL = "https://play.google.com/store/apps/details?id=com.manageengine.mdm.android";
    public static final String PLAYSTORE_ANDROID_URL = "https://play.google.com/store/apps/details?id=com.manageengine.mdm.android";
    public static final int MDM_AGENT_AUTO_UPDATE = 1;
    public static final int MDM_AGENT_DONT_UPDATE = 2;
    
    public static MDMAgentSettingsHandler getInstance() {
        if (MDMAgentSettingsHandler.agentSettingsHandler == null) {
            MDMAgentSettingsHandler.agentSettingsHandler = new MDMAgentSettingsHandler();
        }
        return MDMAgentSettingsHandler.agentSettingsHandler;
    }
    
    public MDMAgentSettingsHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public List processAndroidSettings(final JSONObject settingsData) {
        this.logger.log(Level.INFO, "## ANDROID AGENT SETTING HANDLING BEGINS ##");
        this.addOrUpdateAndroidAgentSettings(settingsData);
        final List statusInfo = this.handleAgentRebranding(settingsData);
        this.handleAndroidNotificationSetting(settingsData);
        this.wakeupAndroidDevicesForAgentSettings(settingsData.optLong("CUSTOMER_ID"));
        MdComplianceRulesHandler.getInstance().addOrUpdateComplianceRules(settingsData);
        this.handleAndroidSafetyNetSettings(settingsData);
        this.logger.log(Level.INFO, "Rebranding status Info : {0}", statusInfo);
        this.logger.log(Level.INFO, "## ANDROID AGENT SETTING HANDLING ENDS ##");
        return statusInfo;
    }
    
    public void processiOSSettings(final JSONObject settingsData) {
        this.addOrUpdateiOSSettings(settingsData);
        MdComplianceRulesHandler.getInstance().addOrUpdateComplianceRules(settingsData);
        IosNativeAppHandler.getInstance().handleiOSNativeAgent(settingsData);
    }
    
    public void processMacOSSettings(final JSONObject settingsData) throws Exception {
        AppsAutoDeployment.getInstance().handleNativeAgent(settingsData);
    }
    
    public void processWindowsSettings(final JSONObject settingsData) {
        this.addOrUpdateWindowsSettings(settingsData);
        this.handleWindowsNotificationSetting(settingsData);
    }
    
    public void toggleMDMMacAgentAutoDistributionStatus(final Long customerID, final Boolean isEnable) {
        try {
            if (customerID != null) {
                final JSONObject jsData = new JSONObject();
                final Boolean isFeatureEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MacMDMAgent");
                jsData.put("AGENT_TYPE", 2);
                jsData.put("IS_NATIVE_APP_ENABLE", isEnable && isFeatureEnabled);
                jsData.put("CUSTOMER_ID", (Object)customerID);
                getInstance().processMacOSSettings(jsData);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Unable to toggle MDM Mac agent settings", e);
        }
    }
    
    private List handleAgentRebranding(final JSONObject settingData) {
        this.logger.log(Level.INFO, "## AGENT REBRANDING BEGINS ##");
        final List statusInfo = new ArrayList();
        final Properties prop = this.getDefaultRebrandingSetting();
        final Boolean hideApp = settingData.optBoolean("HIDE_MDM_APP");
        final String agentIconImgPath = settingData.optString("MDM_APP_ICON");
        final String agentSplashImgPath = settingData.optString("MDM_APP_SPLASH_IMAGE");
        final Integer isIconChange = settingData.optInt("IS_ICON_CHANGE");
        final Integer isSplashImgChange = settingData.optInt("IS_SPLASH_IMAGE_CHANGE");
        final Long customerID = settingData.optLong("CUSTOMER_ID");
        try {
            if (agentIconImgPath != null && !agentIconImgPath.equals("") && isIconChange == 1) {
                this.logger.log(Level.INFO, "MDM App Icon Changed -> true");
                final JSONObject imageFileInfo = this.copyRebrandImageFromTemp(agentIconImgPath, customerID);
                final String fileName = imageFileInfo.optString("FILE_NAME");
                final String desPath = imageFileInfo.optString("PATH");
                settingData.put("MDM_APP_ICON_FILE_NAME", (Object)fileName);
                settingData.put("REBRANDING_PATH", (Object)desPath);
                ((Hashtable<String, String>)prop).put("IS_ICON_CHANGED", "1");
                String iconURL = desPath + "/" + fileName;
                final HashMap hm = new HashMap();
                hm.put("path", iconURL);
                hm.put("IS_SERVER", true);
                hm.put("IS_AUTHTOKEN", false);
                iconURL = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
                ((Hashtable<String, String>)prop).put("ICON_PATH", iconURL);
            }
            else if (agentIconImgPath != null && !agentIconImgPath.equals("") && isIconChange == 0) {
                ((Hashtable<String, String>)prop).put("ICON_PATH", agentIconImgPath);
                settingData.put("REBRANDING_PATH", (Object)agentIconImgPath.substring(0, agentIconImgPath.lastIndexOf(File.separator)));
            }
            if (agentSplashImgPath != null && !agentSplashImgPath.equals("") && isSplashImgChange == 1) {
                this.logger.log(Level.INFO, "MDM App Splash Image Changed -> true");
                final JSONObject imageFileInfo = this.copyRebrandImageFromTemp(agentSplashImgPath, customerID);
                final String fileName = imageFileInfo.optString("FILE_NAME");
                final String desPath = imageFileInfo.optString("PATH");
                settingData.put("MDM_APP_SPLASH_IMAGE_FILE_NAME", (Object)fileName);
                settingData.put("REBRANDING_PATH", (Object)desPath);
                ((Hashtable<String, String>)prop).put("IS_SPLASH_IMAGE_CHANGED", "1");
                String splashImageURL = desPath + "/" + fileName;
                final HashMap hm = new HashMap();
                hm.put("path", splashImageURL);
                hm.put("IS_SERVER", true);
                hm.put("IS_AUTHTOKEN", false);
                splashImageURL = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
                ((Hashtable<String, String>)prop).put("SPLASH_IMAG_PATH", splashImageURL);
            }
            else if (agentSplashImgPath != null && !agentSplashImgPath.equals("") && isSplashImgChange == 0) {
                ((Hashtable<String, String>)prop).put("SPLASH_IMAG_PATH", agentSplashImgPath);
                settingData.put("REBRANDING_PATH", (Object)agentSplashImgPath.substring(0, agentSplashImgPath.lastIndexOf(File.separator)));
            }
            this.addOrUpdateAgentRebranding(settingData);
            ((Hashtable<String, String>)prop).put("STATUS_CODE", "1");
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while handleAgentRebranding", ex);
        }
        this.logger.log(Level.INFO, "## AGENT REBRANDING ENDS##");
        statusInfo.add(prop);
        return statusInfo;
    }
    
    private JSONObject copyRebrandImageFromTemp(final String imagePath, final Long customerID) throws Exception {
        final JSONObject imageInfo = new JSONObject();
        final String imgPath = this.getRebrandingImageFolderPath(customerID);
        final String desFolder = this.getRebrandingImageDBPath(customerID);
        final HashMap fileMap = new MDMAppMgmtHandler().copyAppRepositoryFiles(imagePath, imgPath, desFolder, false, true);
        final String desPath = desFolder.replaceAll("\\\\", "/");
        imageInfo.put("FILE_NAME", (Object)new File(fileMap.get("destFileName")).getName());
        imageInfo.put("PATH", (Object)desPath);
        return imageInfo;
    }
    
    private Properties getDefaultRebrandingSetting() {
        final Properties prop = new Properties();
        ((Hashtable<String, String>)prop).put("STATUS_CODE", "");
        ((Hashtable<String, String>)prop).put("IS_ICON_CHANGED", "");
        ((Hashtable<String, String>)prop).put("ICON_PATH", "");
        ((Hashtable<String, String>)prop).put("IS_SPLASH_IMAGE_CHANGED", "");
        ((Hashtable<String, String>)prop).put("SPLASH_IMAG_PATH", "");
        return prop;
    }
    
    public boolean addOrUpdateAndroidAgentSettings(final JSONObject settingData) {
        this.logger.log(Level.INFO, "## Add/Update Android Agent Setings Begins ##");
        DMSecurityLogger.info(this.logger, "MDMAgentSettingsHandler", "addOrUpdateAndroidAgentSettings", "Android Agent Settings Data : {0}", (Object)settingData);
        boolean status = true;
        final boolean allowAdminDisable = Boolean.valueOf(settingData.optString("ALLOW_ADMIN_DISABLE"));
        final Long customerID = settingData.optLong("CUSTOMER_ID");
        final Criteria custCri = new Criteria(Column.getColumn("AndroidAgentSettings", "CUSTOMER_ID"), (Object)customerID, 0);
        try {
            final DataObject settingsDO = MDMUtil.getPersistence().get("AndroidAgentSettings", custCri);
            if (settingsDO.isEmpty()) {
                final Row row = new Row("AndroidAgentSettings");
                row.set("GRACE_TIME", (Object)Integer.parseInt(settingData.optString("GRACE_TIME")));
                row.set("USER_REM_TIME", (Object)Integer.parseInt(settingData.optString("USER_REM_TIME", "30")));
                row.set("USER_REM_COUNT", (Object)Integer.parseInt(settingData.optString("USER_REM_COUNT", "5")));
                if (settingData.optString("DEACTIVATION_MESSAGE") != null) {
                    row.set("DEACTIVATION_MESSAGE", (Object)settingData.optString("DEACTIVATION_MESSAGE").trim());
                }
                else {
                    row.set("DEACTIVATION_MESSAGE", (Object)settingData.optString("DEACTIVATION_MESSAGE"));
                }
                if (settingData.optString("RECOVERY_PASSWORD_ENCRYPTED") != null) {
                    row.set("RECOVERY_PASSWORD_ENCRYPTED", (Object)settingData.optString("RECOVERY_PASSWORD_ENCRYPTED").trim());
                }
                else {
                    row.set("RECOVERY_PASSWORD_ENCRYPTED", (Object)settingData.optString("RECOVERY_PASSWORD_ENCRYPTED"));
                }
                row.set("ALLOW_ADMIN_DISABLE", (Object)!allowAdminDisable);
                row.set("HIDE_SERVER_DETAILS", (Object)false);
                row.set("HIDE_SERVER_INFO", (Object)Boolean.valueOf(settingData.optString("HIDE_SERVER_DETAILS")));
                row.set("HIDE_MDM_APP", (Object)Boolean.valueOf(settingData.optString("HIDE_MDM_APP")));
                row.set("VALIDATE_CHECKSUM", (Object)settingData.optBoolean("VALIDATE_CHECKSUM", true));
                row.set("UPDATE_TYPE", (Object)Integer.parseInt(settingData.optString("UPDATE_TYPE")));
                row.set("CUSTOMER_ID", (Object)customerID);
                if (settingData.optString("SHORT_SUPPORT_MESSAGE") != null) {
                    row.set("SHORT_SUPPORT_MESSAGE", (Object)settingData.optString("SHORT_SUPPORT_MESSAGE").trim());
                }
                else {
                    row.set("SHORT_SUPPORT_MESSAGE", (Object)settingData.optString("SHORT_SUPPORT_MESSAGE"));
                }
                if (settingData.optString("LONG_SUPPORT_MESSAGE") != null) {
                    row.set("LONG_SUPPORT_MESSAGE", (Object)settingData.optString("LONG_SUPPORT_MESSAGE").trim());
                }
                else {
                    row.set("LONG_SUPPORT_MESSAGE", (Object)settingData.optString("LONG_SUPPORT_MESSAGE"));
                }
                settingsDO.addRow(row);
                MDMUtil.getPersistence().add(settingsDO);
            }
            else {
                final Row row = settingsDO.getFirstRow("AndroidAgentSettings");
                row.set("GRACE_TIME", (Object)Integer.parseInt(settingData.optString("GRACE_TIME")));
                row.set("USER_REM_TIME", (Object)Integer.parseInt(settingData.optString("USER_REM_TIME", "30")));
                row.set("USER_REM_COUNT", (Object)Integer.parseInt(settingData.optString("USER_REM_COUNT", "5")));
                if (settingData.optString("DEACTIVATION_MESSAGE") != null) {
                    row.set("DEACTIVATION_MESSAGE", (Object)settingData.optString("DEACTIVATION_MESSAGE").trim());
                }
                else {
                    row.set("DEACTIVATION_MESSAGE", (Object)settingData.optString("DEACTIVATION_MESSAGE"));
                }
                if (settingData.optString("RECOVERY_PASSWORD_ENCRYPTED") != null) {
                    row.set("RECOVERY_PASSWORD_ENCRYPTED", (Object)settingData.optString("RECOVERY_PASSWORD_ENCRYPTED").trim());
                }
                else {
                    row.set("RECOVERY_PASSWORD_ENCRYPTED", (Object)settingData.optString("RECOVERY_PASSWORD_ENCRYPTED"));
                }
                row.set("ALLOW_ADMIN_DISABLE", (Object)!allowAdminDisable);
                row.set("HIDE_SERVER_INFO", (Object)Boolean.valueOf(settingData.optString("HIDE_SERVER_DETAILS")));
                row.set("HIDE_MDM_APP", (Object)Boolean.valueOf(settingData.optString("HIDE_MDM_APP")));
                row.set("UPDATE_TYPE", (Object)Integer.parseInt(settingData.optString("UPDATE_TYPE")));
                row.set("VALIDATE_CHECKSUM", (Object)settingData.optBoolean("VALIDATE_CHECKSUM", true));
                row.set("CUSTOMER_ID", (Object)customerID);
                if (settingData.optString("SHORT_SUPPORT_MESSAGE") != null) {
                    row.set("SHORT_SUPPORT_MESSAGE", (Object)settingData.optString("SHORT_SUPPORT_MESSAGE").trim());
                }
                else {
                    row.set("SHORT_SUPPORT_MESSAGE", (Object)settingData.optString("SHORT_SUPPORT_MESSAGE"));
                }
                if (settingData.optString("LONG_SUPPORT_MESSAGE") != null) {
                    row.set("LONG_SUPPORT_MESSAGE", (Object)settingData.optString("LONG_SUPPORT_MESSAGE").trim());
                }
                else {
                    row.set("LONG_SUPPORT_MESSAGE", (Object)settingData.optString("LONG_SUPPORT_MESSAGE"));
                }
                settingsDO.updateRow(row);
                MDMUtil.getPersistence().update(settingsDO);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while addOrUpdateAndroidAgentSettings", ex);
            status = false;
        }
        this.logger.log(Level.INFO, "## Add/Update Android Agent Setings Ends ##");
        return status;
    }
    
    public boolean addOrUpdateAgentRebranding(final JSONObject rebrandingData) {
        this.logger.log(Level.INFO, "## Add/Update Agent Rebranding Begins ##");
        DMSecurityLogger.info(this.logger, "MDMAgentSettingsHandler", "addOrUpdateAgentRebranding", "Rebranding Data : {0}", (Object)rebrandingData);
        boolean status = true;
        final Long customerID = rebrandingData.optLong("CUSTOMER_ID");
        final Integer isIconChange = rebrandingData.optInt("IS_ICON_CHANGE");
        final Integer isSplashImgChange = rebrandingData.optInt("IS_SPLASH_IMAGE_CHANGE");
        final Criteria custCri = new Criteria(Column.getColumn("AgentRebranding", "CUSTOMER_ID"), (Object)customerID, 0);
        try {
            final DataObject rebrandingDO = MDMUtil.getPersistence().get("AgentRebranding", custCri);
            if (rebrandingDO.isEmpty()) {
                final Row rebrandingDataRow = new Row("AgentRebranding");
                String memdmAppName = rebrandingData.optString("MDM_APP_NAME", (String)null);
                if (memdmAppName != null) {
                    memdmAppName = memdmAppName.trim();
                }
                rebrandingDataRow.set("MDM_APP_NAME", (Object)memdmAppName);
                if (isIconChange == 1 || isSplashImgChange == 1) {
                    rebrandingDataRow.set("REBRANDING_PATH", (Object)rebrandingData.optString("REBRANDING_PATH", (String)null));
                }
                if (isIconChange == 1 || isIconChange == 2) {
                    rebrandingDataRow.set("MDM_APP_ICON_FILE_NAME", (Object)rebrandingData.optString("MDM_APP_ICON_FILE_NAME", (String)null));
                }
                if (isSplashImgChange == 1 || isSplashImgChange == 2) {
                    rebrandingDataRow.set("MDM_APP_SPLASH_IMAGE_FILE_NAME", (Object)rebrandingData.optString("MDM_APP_SPLASH_IMAGE_FILE_NAME", (String)null));
                }
                rebrandingDataRow.set("CUSTOMER_ID", (Object)customerID);
                rebrandingDO.addRow(rebrandingDataRow);
                MDMUtil.getPersistence().add(rebrandingDO);
            }
            else {
                final Row rebrandingDataRow = rebrandingDO.getFirstRow("AgentRebranding");
                String iconFile = (String)rebrandingDataRow.get("MDM_APP_ICON_FILE_NAME");
                String splashFile = (String)rebrandingDataRow.get("MDM_APP_SPLASH_IMAGE_FILE_NAME");
                String memdmAppName2 = rebrandingData.optString("MDM_APP_NAME", (String)null);
                if (memdmAppName2 != null) {
                    memdmAppName2 = memdmAppName2.trim();
                }
                rebrandingDataRow.set("MDM_APP_NAME", (Object)memdmAppName2);
                if (isIconChange == 1 || isSplashImgChange == 1) {
                    rebrandingDataRow.set("REBRANDING_PATH", (Object)rebrandingData.optString("REBRANDING_PATH", (String)null));
                }
                if (isIconChange == 1 || isIconChange == 2) {
                    rebrandingDataRow.set("MDM_APP_ICON_FILE_NAME", (Object)rebrandingData.optString("MDM_APP_ICON_FILE_NAME", (String)null));
                }
                if (isSplashImgChange == 1 || isSplashImgChange == 2) {
                    rebrandingDataRow.set("MDM_APP_SPLASH_IMAGE_FILE_NAME", (Object)rebrandingData.optString("MDM_APP_SPLASH_IMAGE_FILE_NAME", (String)null));
                }
                rebrandingDataRow.set("CUSTOMER_ID", (Object)customerID);
                rebrandingDO.updateRow(rebrandingDataRow);
                MDMUtil.getPersistence().update(rebrandingDO);
                final String imgPath = this.getRebrandingImageFolderPath(customerID);
                if (isIconChange == 2 && isSplashImgChange == 2) {
                    if (iconFile != null) {
                        ApiFactoryProvider.getFileAccessAPI().deleteFile(imgPath + File.separator + iconFile);
                    }
                    if (!(imgPath + File.separator + iconFile).equals(imgPath + File.separator + splashFile) && splashFile != null) {
                        ApiFactoryProvider.getFileAccessAPI().deleteFile(imgPath + File.separator + splashFile);
                    }
                }
                else {
                    if (iconFile == null) {
                        iconFile = "";
                    }
                    if (splashFile == null) {
                        splashFile = "";
                    }
                    if (!iconFile.equals(splashFile)) {
                        if (isIconChange == 2) {
                            ApiFactoryProvider.getFileAccessAPI().deleteFile(imgPath + File.separator + iconFile);
                        }
                        if (isSplashImgChange == 2) {
                            ApiFactoryProvider.getFileAccessAPI().deleteFile(imgPath + File.separator + splashFile);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while addOrUpdateAgentRebranding", ex);
            status = false;
        }
        this.logger.log(Level.INFO, "## Add/Update Agent Rebranding Ends ##");
        return status;
    }
    
    private void handleAndroidNotificationSetting(final JSONObject formData) {
        this.logger.log(Level.INFO, "-- Android Notificatoin Handling initiated --");
        final int platform = 2;
        final Long customerID = formData.optLong("CUSTOMER_ID");
        final int androidNotificationService = formData.optInt("ANDROID_NOTIFICATION_SERVICE", 1);
        final int downloadAgentMode = formData.optInt("ANDROID_AGENT_DOWNLOAD_MODE", 3);
        this.logger.log(Level.INFO, "Andorid Notification Service :{0}; Download Mode:{1}", new Object[] { androidNotificationService, downloadAgentMode });
        final MDAgentCommunicationModeData notificationData = new MDAgentCommunicationModeData();
        notificationData.addCustomerID(customerID).addPlatform(platform).addCommunicationMode(androidNotificationService);
        this.updateNotificationService(notificationData);
        final MdAgentDownloadInfoData mdAgentData = new MdAgentDownloadInfoData();
        mdAgentData.addCustomerID(customerID).addPlatform(platform).addDownloadMode(downloadAgentMode);
        this.addOrUpdateMdAgentDownloadInfo(mdAgentData);
        this.logger.log(Level.INFO, "-- Andorid Notificatoin Handling Completed --");
    }
    
    private void handleWindowsNotificationSetting(final JSONObject formData) {
        this.logger.log(Level.INFO, "-- Windows Notification Handling initiated --");
        final int platform = 3;
        final Long customerID = formData.optLong("CUSTOMER_ID");
        final int windowsNotificationService = formData.optInt("WINDOWS_PHONE_NOTIFICATION_SERVICE", 1);
        this.logger.log(Level.INFO, "Windows Notification Service :{0}", windowsNotificationService);
        final int prevNotificationService = this.getNotificaitonServiceType(platform, customerID);
        final MDAgentCommunicationModeData notificationData = new MDAgentCommunicationModeData();
        notificationData.addCustomerID(customerID).addPlatform(platform).addCommunicationMode(windowsNotificationService);
        this.updateNotificationService(notificationData);
        if (windowsNotificationService == 1 && prevNotificationService == 2) {
            List resourceIdList = null;
            resourceIdList = ManagedDeviceHandler.getInstance().getWindowsPhone81AboveDevices(customerID);
            DeviceCommandRepository.getInstance().addDeviceCommunicationCommand(resourceIdList);
        }
        this.logger.log(Level.INFO, "-- Windows Notification Handling Completed --");
    }
    
    private void updateNotificationService(final MDAgentCommunicationModeData notificationData) {
        final String tableName = "MDCommunicationMode";
        this.logger.log(Level.INFO, "Update {0}  intiated", tableName);
        this.logger.log(Level.INFO, "Platform={0}; Typee={1}", new Object[] { notificationData.platform, notificationData.communicationMode });
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MDCommunicationMode");
            uQuery.setUpdateColumn("SERVICE_TYPE", (Object)notificationData.communicationMode);
            final Criteria platformCriteria = new Criteria(Column.getColumn("MDCommunicationMode", "PLATFORM_TYPE"), (Object)notificationData.platform, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MDCommunicationMode", "CUSTOMER_ID"), (Object)notificationData.customerId, 0);
            uQuery.setCriteria(platformCriteria.and(customerCriteria));
            MDMUtil.getPersistence().update(uQuery);
            this.logger.log(Level.INFO, "{0} update completed", "MDCommunicationMode");
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while updateNotificationService", ex);
        }
    }
    
    public void addOrUpdateCommunicationMode(final MDAgentCommunicationModeData[] notificationDataArr) {
        this.logger.log(Level.INFO, "Add Or update communication mode config begins");
        final Long customerId = notificationDataArr[0].customerId;
        try {
            final Criteria customerCriteria = new Criteria(Column.getColumn("MDCommunicationMode", "CUSTOMER_ID"), (Object)customerId, 0);
            final DataObject commModeExistingDO = MDMUtil.getPersistence().get("MDCommunicationMode", customerCriteria);
            if (commModeExistingDO.isEmpty()) {
                final DataObject commModeDO = MDMUtil.getPersistence().constructDataObject();
                for (final MDAgentCommunicationModeData notificationData : notificationDataArr) {
                    this.logger.log(Level.INFO, "Adding communication mode config: {0}", notificationData.toString());
                    final Row commModeRow = this.getCommModeConfigRow(notificationData);
                    commModeDO.addRow(commModeRow);
                }
                if (!commModeDO.isEmpty()) {
                    MDMUtil.getPersistence().add(commModeDO);
                }
                else {
                    this.logger.log(Level.WARNING, "MDCommunicationMode default data population - No Platform value configured");
                }
            }
            else {
                for (final MDAgentCommunicationModeData notificationData2 : notificationDataArr) {
                    this.logger.log(Level.INFO, "Updating communication mode config: {0}", notificationData2.toString());
                    this.updateNotificationService(notificationData2);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occurred while addMDAgentDownloadInfo", e);
        }
        this.logger.log(Level.INFO, "Add config to MDCommunicationMode ends");
    }
    
    private Row getCommModeConfigRow(final MDAgentCommunicationModeData notificationData) {
        final String tableName = "MDCommunicationMode";
        final Row commModeConfigRow = new Row(tableName);
        commModeConfigRow.set("CUSTOMER_ID", (Object)notificationData.customerId);
        commModeConfigRow.set("PLATFORM_TYPE", (Object)notificationData.platform);
        commModeConfigRow.set("SERVICE_TYPE", (Object)notificationData.communicationMode);
        return commModeConfigRow;
    }
    
    public void addMDAgentDownloadInfo(final MdAgentDownloadInfoData[] mdAgentDataArr) {
        this.logger.log(Level.INFO, "Populate default values int MDAgentDownloadInfo begins");
        try {
            DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
            for (final MdAgentDownloadInfoData mdAgentData : mdAgentDataArr) {
                dataObject = this.addDownloadInfoRow(mdAgentData, dataObject);
                this.logger.log(Level.INFO, "Default data inputs : ", mdAgentData.toString());
            }
            if (!dataObject.isEmpty()) {
                MDMUtil.getPersistence().add(dataObject);
                this.logger.log(Level.INFO, "Default data population get succeeded");
            }
            else {
                this.logger.log(Level.WARNING, "No Platform value configured in MdAgentDownloadInfoData class object");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occurred while addMDAgentDownloadInfo", e);
        }
    }
    
    public void addOrUpdateMdAgentDownloadInfo(final MdAgentDownloadInfoData mdAgentData) {
        this.logger.log(Level.INFO, "Set Agent Download info begins");
        this.logger.log(Level.INFO, "Data :{0}", mdAgentData.toString());
        try {
            final Criteria customerIdCrit = new Criteria(Column.getColumn("MDAgentDownloadInfo", "CUSTOMER_ID"), (Object)mdAgentData.customerId, 0);
            final Criteria platformCrit = new Criteria(Column.getColumn("MDAgentDownloadInfo", "PLATFORM_TYPE"), (Object)mdAgentData.platformType, 0);
            final Criteria criteria = customerIdCrit.and(platformCrit);
            DataObject downloadInfoDO = MDMUtil.getPersistence().get("MDAgentDownloadInfo", criteria);
            if (downloadInfoDO.isEmpty()) {
                this.logger.log(Level.INFO, "DataObject is empty. Create a new row");
                downloadInfoDO = this.addDownloadInfoRow(mdAgentData, downloadInfoDO);
                if (!downloadInfoDO.isEmpty()) {
                    MDMUtil.getPersistence().add(downloadInfoDO);
                }
                else {
                    this.logger.log(Level.WARNING, "Dataobject empty for this platform={0}", mdAgentData.platformType);
                }
            }
            else {
                this.logger.log(Level.INFO, "MDAGENTDOWNLOADINFO - Update the existing data");
                final Iterator downloadInfoIterator = downloadInfoDO.getRows("MDAgentDownloadInfo");
                while (downloadInfoIterator.hasNext()) {
                    final Row row = downloadInfoIterator.next();
                    row.set("DOWNLOAD_MODE", (Object)mdAgentData.downloadMode);
                    downloadInfoDO.updateRow(row);
                }
                MDMUtil.getPersistence().update(downloadInfoDO);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while addOrUpdateMdAgentDownloadInfo", ex);
        }
    }
    
    private DataObject addDownloadInfoRow(final MdAgentDownloadInfoData mdAgentData, final DataObject downloadInfo) throws Exception {
        final int platform = mdAgentData.platformType;
        if (platform == 2) {
            final Row samsungAgentRow = new Row("MDAgentDownloadInfo");
            samsungAgentRow.set("CUSTOMER_ID", (Object)mdAgentData.customerId);
            samsungAgentRow.set("PLATFORM_TYPE", (Object)mdAgentData.platformType);
            samsungAgentRow.set("AGENT_TYPE", (Object)3);
            samsungAgentRow.set("DOWNLOAD_URL", (Object)"https://play.google.com/store/apps/details?id=com.manageengine.mdm.android");
            samsungAgentRow.set("DOWNLOAD_MODE", (Object)mdAgentData.downloadMode);
            downloadInfo.addRow(samsungAgentRow);
            final Row androidAgentRow = new Row("MDAgentDownloadInfo");
            androidAgentRow.set("CUSTOMER_ID", (Object)mdAgentData.customerId);
            androidAgentRow.set("PLATFORM_TYPE", (Object)mdAgentData.platformType);
            androidAgentRow.set("AGENT_TYPE", (Object)2);
            androidAgentRow.set("DOWNLOAD_URL", (Object)"https://play.google.com/store/apps/details?id=com.manageengine.mdm.android");
            androidAgentRow.set("DOWNLOAD_MODE", (Object)mdAgentData.downloadMode);
            downloadInfo.addRow(androidAgentRow);
        }
        if (platform == 3) {
            final Row windowAgentRow = new Row("MDAgentDownloadInfo");
            windowAgentRow.set("CUSTOMER_ID", (Object)mdAgentData.customerId);
            windowAgentRow.set("PLATFORM_TYPE", (Object)mdAgentData.platformType);
            windowAgentRow.set("AGENT_TYPE", (Object)4);
            windowAgentRow.set("DOWNLOAD_URL", (Object)"/mdm/app");
            windowAgentRow.set("DOWNLOAD_MODE", (Object)mdAgentData.downloadMode);
            downloadInfo.addRow(windowAgentRow);
        }
        return downloadInfo;
    }
    
    public int getAndroidAgentDownloadMode() {
        int downloadMode = this.getAndroidDefaultDownloadMode();
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final Criteria customerIdCrit = new Criteria(Column.getColumn("MDAgentDownloadInfo", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria platformCrit = new Criteria(Column.getColumn("MDAgentDownloadInfo", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria criteria = customerIdCrit.and(platformCrit);
            final ReadOnlyPersistence cachedPersistence = MDMUtil.getCachedPersistence();
            final DataObject dataObject = cachedPersistence.get("MDAgentDownloadInfo", criteria);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MDAgentDownloadInfo");
                downloadMode = (int)row.get("DOWNLOAD_MODE");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getMDMAgentDownloadMode ", ex);
        }
        return downloadMode;
    }
    
    public MdAgentDownloadInfoData getAndroidAgentDownloadData(final Long customerID) {
        MdAgentDownloadInfoData mdAgentData = null;
        try {
            final Criteria customerIdCrit = new Criteria(Column.getColumn("MDAgentDownloadInfo", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria platformCrit = new Criteria(Column.getColumn("MDAgentDownloadInfo", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria criteria = customerIdCrit.and(platformCrit);
            final ReadOnlyPersistence cachedPersistence = MDMUtil.getCachedPersistence();
            final DataObject dataObject = cachedPersistence.get("MDAgentDownloadInfo", criteria);
            if (dataObject != null && !dataObject.isEmpty()) {
                mdAgentData = new MdAgentDownloadInfoData();
                mdAgentData.addCustomerID(customerID).addPlatform(2).addDownloadMode((int)dataObject.getFirstRow("MDAgentDownloadInfo").get("DOWNLOAD_MODE"));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occurred while getAndroidAgentDownloadSettings ", e);
        }
        return mdAgentData;
    }
    
    public void updatePollingConfiguration(final JSONObject configData) {
        this.logger.log(Level.INFO, "Update Polling Config begins");
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MDScheduledPollingConfig");
            uQuery.setUpdateColumn("POLLING_INTERVAL", (Object)configData.optInt("POLLING_INTERVAL", 90));
            uQuery.setUpdateColumn("INITIAL_POLLING_INTERVAL", (Object)configData.optInt("INITIAL_POLLING_INTERVAL", 30));
            uQuery.setUpdateColumn("MAX_INITIAL_RETRIES", (Object)configData.optInt("MAX_INITIAL_RETRIES", 60));
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while updatePollingConfiguration", ex);
        }
        this.logger.log(Level.INFO, "Update Polling Config ends");
    }
    
    public void wakeupAndroidDevicesForAgentSettings(final Long customerID) {
        this.logger.log(Level.INFO, "## WAKE_UP ANDROID DEVICES BEGINS FOR AGENT SETTINGS CHANGED ##");
        final List resList = ManagedDeviceHandler.getInstance().getAndroidManagedDevicesForCustomer(customerID);
        this.logger.log(Level.INFO, "Wake-up Resource List : {0}", resList);
        DeviceCommandRepository.getInstance().addSyncAgentSettingsCommandForAndroid(resList);
        try {
            NotificationHandler.getInstance().SendNotification(resList, 2);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in wakeupAndroidDevicesForAgentSettings", ex);
        }
        this.logger.log(Level.INFO, "## WAKE_UP ANDROID DEVICES ENDS FOR AGENT SETTINGS CHANGED ##");
    }
    
    public void addOrUpdateiOSSettings(final JSONObject settingData) {
        this.logger.log(Level.INFO, "## ADD/UPDATE IOSAGENTSETTINGS TABLE BEGINS ##");
        this.logger.log(Level.INFO, "IOSSETTINGSDATA : {0}", settingData);
        final Long customerID = settingData.optLong("CUSTOMER_ID");
        final Criteria custCri = new Criteria(Column.getColumn("IOSAgentSettings", "CUSTOMER_ID"), (Object)customerID, 0);
        try {
            final DataObject settingsDO = MDMUtil.getPersistence().get("IOSAgentSettings", custCri);
            if (settingsDO.isEmpty()) {
                final Row row = new Row("IOSAgentSettings");
                row.set("CUSTOMER_ID", (Object)customerID);
                row.set("IS_NATIVE_APP_ENABLE", (Object)settingData.optBoolean("IS_NATIVE_APP_ENABLE"));
                row.set("VALIDATE_CHECKSUM", (Object)settingData.optBoolean("VALIDATE_CHECKSUM", true));
                settingsDO.addRow(row);
                MDMUtil.getPersistence().add(settingsDO);
            }
            else {
                final Row row = settingsDO.getFirstRow("IOSAgentSettings");
                row.set("IS_NATIVE_APP_ENABLE", (Object)settingData.optBoolean("IS_NATIVE_APP_ENABLE"));
                row.set("VALIDATE_CHECKSUM", (Object)settingData.optBoolean("VALIDATE_CHECKSUM", (boolean)row.get("VALIDATE_CHECKSUM")));
                settingsDO.updateRow(row);
                MDMUtil.getPersistence().update(settingsDO);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in addOrUpdateiOSSettings", ex);
        }
        this.logger.log(Level.INFO, "## ADD/UPDATE IOSAGENTSETTINGS TABLE ENDS ##");
    }
    
    public String getRebrandingImageDBPath(final Long customerID) {
        return File.separator + "MDM" + File.separator + "agentrebranding" + File.separator + customerID;
    }
    
    public String getRebrandingImageFolderPath(final Long customerId) throws Exception {
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String appRepositoryFolder = webappsDir + File.separator + "MDM" + File.separator + "agentrebranding" + File.separator + customerId;
        return appRepositoryFolder;
    }
    
    public JSONObject getAndroidAgentSetting(final Long customerID) {
        final JSONObject settingData = new JSONObject();
        try {
            final Criteria custCri = new Criteria(Column.getColumn("AndroidAgentSettings", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject androidAgentSettingDO = MDMUtil.getPersistence().get("AndroidAgentSettings", custCri);
            if (!androidAgentSettingDO.isEmpty() && androidAgentSettingDO != null) {
                final Row row = androidAgentSettingDO.getFirstRow("AndroidAgentSettings");
                settingData.put("GRACE_TIME", row.get("GRACE_TIME"));
                settingData.put("USER_REM_TIME", row.get("USER_REM_TIME"));
                settingData.put("USER_REM_COUNT", row.get("USER_REM_COUNT"));
                settingData.put("DEACTIVATION_MESSAGE", (Object)I18N.getMsg((String)row.get("DEACTIVATION_MESSAGE"), new Object[0]));
                if (row.get("RECOVERY_PASSWORD_ENCRYPTED") == null) {
                    final String passEncrypted = MDMUtil.generateNewRandomToken("AndroidAgentSettings", "RECOVERY_PASSWORD_ENCRYPTED", "SETTINGS_ID");
                    final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("AndroidAgentSettings");
                    updateQuery.setCriteria(custCri.and(new Criteria(Column.getColumn("AndroidAgentSettings", "RECOVERY_PASSWORD_ENCRYPTED"), (Object)null, 0)));
                    updateQuery.setUpdateColumn("RECOVERY_PASSWORD_ENCRYPTED", (Object)passEncrypted);
                    DataAccess.update(updateQuery);
                    row.set("RECOVERY_PASSWORD_ENCRYPTED", (Object)passEncrypted);
                }
                settingData.put("RECOVERY_PASSWORD_ENCRYPTED", row.get("RECOVERY_PASSWORD_ENCRYPTED"));
                settingData.put("ALLOW_ADMIN_DISABLE", row.get("ALLOW_ADMIN_DISABLE"));
                settingData.put("HIDE_SERVER_INFO", row.get("HIDE_SERVER_INFO"));
                settingData.put("HIDE_SERVER_DETAILS", row.get("HIDE_SERVER_DETAILS"));
                settingData.put("HIDE_MDM_APP", row.get("HIDE_MDM_APP"));
                settingData.put("UPDATE_TYPE", row.get("UPDATE_TYPE"));
                settingData.put("VALIDATE_CHECKSUM", row.get("VALIDATE_CHECKSUM"));
                settingData.put("SHORT_SUPPORT_MESSAGE", (Object)I18N.getMsg((String)row.get("SHORT_SUPPORT_MESSAGE"), new Object[0]));
                settingData.put("LONG_SUPPORT_MESSAGE", (Object)I18N.getMsg((String)row.get("LONG_SUPPORT_MESSAGE"), new Object[0]));
                settingData.put("RECOVERY_PASSWORD", row.get("RECOVERY_PASSWORD_ENCRYPTED"));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getAndroidAgentSetting", ex);
        }
        return settingData;
    }
    
    public JSONObject getAgentRebrandingSetting(final Long customerID) {
        final JSONObject rebrandingSetting = new JSONObject();
        try {
            final Criteria cusCri = new Criteria(Column.getColumn("AgentRebranding", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject agentRebrandingDO = MDMUtil.getPersistence().get("AgentRebranding", cusCri);
            if (!agentRebrandingDO.isEmpty() && agentRebrandingDO != null) {
                final Row row = agentRebrandingDO.getFirstRow("AgentRebranding");
                rebrandingSetting.put("MDM_APP_NAME", row.get("MDM_APP_NAME"));
                rebrandingSetting.put("REBRANDING_PATH", row.get("REBRANDING_PATH"));
                rebrandingSetting.put("MDM_APP_ICON_FILE_NAME", row.get("MDM_APP_ICON_FILE_NAME"));
                rebrandingSetting.put("MDM_APP_SPLASH_IMAGE_FILE_NAME", row.get("MDM_APP_SPLASH_IMAGE_FILE_NAME"));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getAgentRebrandingSetting", ex);
        }
        return rebrandingSetting;
    }
    
    public JSONObject getAndroidNotificationSetting(final Long customerId) {
        this.logger.log(Level.INFO, "Getting Android Notification Setting initiated");
        final int notificationService = this.getNotificaitonServiceType(2, customerId);
        final int downloadMode = this.getAndroidAgentDownloadMode();
        final JSONObject notificationData = new JSONObject();
        try {
            notificationData.put("ANDROID_NOTIFICATION_SERVICE", notificationService);
            notificationData.put("ANDROID_AGENT_DOWNLOAD_MODE", downloadMode);
            this.logger.log(Level.INFO, "Returned Data :{0}", notificationData.toString());
            this.logger.log(Level.INFO, "Getting Android Notification Setting Completed");
        }
        catch (final JSONException ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getAndroidNotificationSetting", (Throwable)ex);
        }
        return notificationData;
    }
    
    public void addOrUpdateWindowsSettings(final JSONObject settingData) {
        this.logger.log(Level.INFO, "## ADD/UPDATE WPCLIENTSETTINGS TABLE BEGINS ##");
        this.logger.log(Level.INFO, "WPCLIENTSETTINGS DATA : {0}", settingData);
        final Long customerId = settingData.optLong("CUSTOMER_ID");
        try {
            if (customerId != null) {
                final Criteria custCri = new Criteria(Column.getColumn("WPClientSettings", "CUSTOMER_ID"), (Object)customerId, 0);
                final DataObject settingsDO = MDMUtil.getPersistence().get("WPClientSettings", custCri);
                Long userId = null;
                try {
                    userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                }
                catch (final Exception e) {
                    this.logger.log(Level.WARNING, "Exception while getting the userId. System user woild be used instead");
                }
                if (userId == null) {
                    userId = DMUserHandler.getUserID(MDMEventConstant.DC_SYSTEM_USER);
                }
                if (settingsDO.isEmpty()) {
                    final Row row = new Row("WPClientSettings");
                    row.set("CUSTOMER_ID", (Object)customerId);
                    row.set("USER_UNENROLL", (Object)settingData.optBoolean("USER_UNENROLL"));
                    row.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                    row.set("UPDATED_BY", (Object)userId);
                    settingsDO.addRow(row);
                    MDMUtil.getPersistence().add(settingsDO);
                }
                else {
                    final Row row = settingsDO.getFirstRow("WPClientSettings");
                    row.set("USER_UNENROLL", (Object)settingData.optBoolean("USER_UNENROLL"));
                    row.set("UPDATED_TIME", (Object)System.currentTimeMillis());
                    row.set("UPDATED_BY", (Object)userId);
                    settingsDO.updateRow(row);
                    MDMUtil.getPersistence().update(settingsDO);
                }
            }
            final List resList = ManagedDeviceHandler.getInstance().getWindowsPhone81AboveDevices(customerId);
            if (resList != null) {
                DeviceCommandRepository.getInstance().addWindowsCommand(resList, "DeviceClientSettings");
                NotificationHandler.getInstance().SendNotification(resList, 3);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while addOrUpdateWindowsSettings", ex);
        }
    }
    
    public JSONObject getWindowsSettings(final Long customerID) throws Exception {
        final JSONObject settingData = new JSONObject();
        final Criteria custCri = new Criteria(new Column("WPClientSettings", "CUSTOMER_ID"), (Object)customerID, 0);
        final DataObject settingsDO = MDMUtil.getPersistence().get("WPClientSettings", custCri);
        if (!settingsDO.isEmpty()) {
            final Row row = settingsDO.getFirstRow("WPClientSettings");
            settingData.put("USER_UNENROLL", row.get("USER_UNENROLL"));
            settingData.put("UPDATED_TIME", row.get("UPDATED_TIME"));
            settingData.put("UPDATED_BY", row.get("UPDATED_BY"));
            final int notificationService = this.getNotificaitonServiceType(3, customerID);
            settingData.put("WINDOWS_PHONE_NOTIFICATION_SERVICE", notificationService);
        }
        return settingData;
    }
    
    public boolean isWPUserUnEnroll(final Long customerId) throws Exception {
        Boolean isWPUserUnEnroll = true;
        if (customerId != null) {
            isWPUserUnEnroll = (Boolean)DBUtil.getValueFromDB("WPClientSettings", "CUSTOMER_ID", (Object)customerId, "USER_UNENROLL");
            if (isWPUserUnEnroll == null) {
                isWPUserUnEnroll = true;
            }
        }
        return isWPUserUnEnroll;
    }
    
    public int getNotificaitonServiceType(final int platform, final Long customerID) {
        int service = 1;
        try {
            final ReadOnlyPersistence cachedPersistence = MDMUtil.getCachedPersistence();
            final Criteria platformCriteria = new Criteria(Column.getColumn("MDCommunicationMode", "PLATFORM_TYPE"), (Object)platform, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MDCommunicationMode", "CUSTOMER_ID"), (Object)customerID, 0);
            final DataObject dataObject = cachedPersistence.get("MDCommunicationMode", platformCriteria.and(customerCriteria));
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MDCommunicationMode");
                service = (int)row.get("SERVICE_TYPE");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getNotificaitonServiceType ", ex);
        }
        return service;
    }
    
    public JSONObject getAndroidPushNotificationConfig(final Long customerId) {
        this.logger.log(Level.INFO, "Get AndroidWakeUpPolicy begins");
        final JSONObject wakeUpConfig = new JSONObject();
        final int notificationType = getInstance().getNotificaitonServiceType(2, customerId);
        this.logger.log(Level.INFO, "Get AndroidWakeUpPolicy notification type: {0}", notificationType);
        try {
            if (notificationType == 2) {
                final JSONObject pollingConfig = this.getScheduledPollingConfig();
                wakeUpConfig.put("DeviceWakeUpPolicy", (Object)"ScheduledPolling");
                wakeUpConfig.put("PollingIntervalMinutes", pollingConfig.optInt("POLLING_INTERVAL"));
                wakeUpConfig.put("InitialPollingIntervalSeconds", pollingConfig.optInt("INITIAL_POLLING_INTERVAL"));
                wakeUpConfig.put("InitialRetries", pollingConfig.optInt("MAX_INITIAL_RETRIES"));
            }
            else if (notificationType == 3) {
                final JSONObject nsConfig = MDMApiFactoryProvider.getNSwakeupAPI().getNSConfig();
                wakeUpConfig.put("DeviceWakeUpPolicy", (Object)"NSService");
                wakeUpConfig.put("NsPort", (Object)nsConfig.optString("NsPort"));
                wakeUpConfig.put("NsSyncIntervalSeconds", (Object)nsConfig.optString("NsSyncIntervalSeconds"));
            }
            else {
                wakeUpConfig.put("DeviceWakeUpPolicy", (Object)"GCMServer");
                wakeUpConfig.put("OnDemandPolicy", (Object)"FCM");
                wakeUpConfig.put("GCMProjectId", (Object)MDMApiFactoryProvider.getSecureKeyProviderAPI().getSecret("GCMProjectId"));
            }
            this.logger.log(Level.INFO, "Getting Android Wakeup Config successfully : {0}", wakeUpConfig);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getAndroidWakeUpPolicy.", ex);
        }
        return wakeUpConfig;
    }
    
    public JSONObject getFCMPushNotificationConfig(final String apiKey, final String projectId, final String senderId, final String appId) {
        final JSONObject pushConfig = new JSONObject();
        pushConfig.put("APIKey", (Object)apiKey);
        pushConfig.put("SenderId", (Object)senderId);
        pushConfig.put("ProjectId", (Object)projectId);
        pushConfig.put("AppId", (Object)appId);
        return pushConfig;
    }
    
    public JSONObject getScheduledPollingConfig() {
        final JSONObject pollData = new JSONObject();
        try {
            final ReadOnlyPersistence cachedPersistence = MDMUtil.getCachedPersistence();
            final DataObject dataObject = cachedPersistence.get("MDScheduledPollingConfig", (Criteria)null);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MDScheduledPollingConfig");
                pollData.put("INITIAL_POLLING_INTERVAL", row.get("INITIAL_POLLING_INTERVAL"));
                pollData.put("MAX_INITIAL_RETRIES", row.get("MAX_INITIAL_RETRIES"));
                pollData.put("POLLING_INTERVAL", row.get("POLLING_INTERVAL"));
            }
            else {
                this.logger.log(Level.INFO, "{0} Table may empty or null", "MDScheduledPollingConfig");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getScheduledPollingConfig", ex);
        }
        return pollData;
    }
    
    public int getAndroidDefaultDownloadMode() {
        return 3;
    }
    
    public int getAndroidDefaultUpdateType() {
        return 1;
    }
    
    public MDAgentCommunicationModeData[] getDefaultAgentCommunicationMode(final Long customerId) {
        final MDAgentCommunicationModeData androidNotificationData = new MDAgentCommunicationModeData();
        androidNotificationData.addCustomerID(customerId).addPlatform(2).addCommunicationMode(1);
        final MDAgentCommunicationModeData iosNotificationData = new MDAgentCommunicationModeData();
        iosNotificationData.addCustomerID(customerId).addPlatform(1).addCommunicationMode(1);
        final MDAgentCommunicationModeData windowsNotificationData = new MDAgentCommunicationModeData();
        windowsNotificationData.addCustomerID(customerId).addPlatform(3).addCommunicationMode(1);
        final MDAgentCommunicationModeData[] notificationData = { androidNotificationData, iosNotificationData, windowsNotificationData };
        return notificationData;
    }
    
    public MDAgentCommunicationModeData[] getNotificationServiceData() throws Exception {
        final Integer androidNotificationService = (Integer)DBUtil.getValueFromDB("MDNotificationService", "PLATFORM_TYPE", (Object)2, "SERVICE_TYPE");
        final Integer iOSNotificationService = (Integer)DBUtil.getValueFromDB("MDNotificationService", "PLATFORM_TYPE", (Object)1, "SERVICE_TYPE");
        final Integer windowsNotificationService = (Integer)DBUtil.getValueFromDB("MDNotificationService", "PLATFORM_TYPE", (Object)3, "SERVICE_TYPE");
        final MDAgentCommunicationModeData androidCommModeData = new MDAgentCommunicationModeData();
        final MDAgentCommunicationModeData iOSCommModeData = new MDAgentCommunicationModeData();
        final MDAgentCommunicationModeData windowsCommModeData = new MDAgentCommunicationModeData();
        androidCommModeData.addPlatform(2).addCommunicationMode(androidNotificationService);
        iOSCommModeData.addPlatform(1).addCommunicationMode(iOSNotificationService);
        windowsCommModeData.addPlatform(3).addCommunicationMode(windowsNotificationService);
        final MDAgentCommunicationModeData[] notificationData = { androidCommModeData, iOSCommModeData, windowsCommModeData };
        return notificationData;
    }
    
    public void enableioSMEMDMAppSettings(final Long customerID) throws JSONException {
        final MDMAgentSettingsHandler agentHandler = new MDMAgentSettingsHandler();
        final JSONObject iosData = new JSONObject();
        iosData.put("CUSTOMER_ID", (Object)customerID);
        iosData.put("IS_NATIVE_APP_ENABLE", true);
        agentHandler.processiOSSettings(iosData);
    }
    
    @Deprecated
    public void DistributeNativeApp(final Long customerId) {
        try {
            final MDMAgentSettingsHandler agentHandler = new MDMAgentSettingsHandler();
            final JSONObject iosData = new JSONObject();
            iosData.put("CUSTOMER_ID", (Object)customerId);
            iosData.put("IS_NATIVE_APP_ENABLE", true);
            agentHandler.processiOSSettings(iosData);
        }
        catch (final Exception ex) {
            Logger.getLogger(DocMgmt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getAndroidIconPath(final String path, final String icon) {
        String iconPath = path + "/" + icon;
        iconPath = iconPath.replace("/", File.separator);
        final HashMap hm = new HashMap();
        hm.put("path", iconPath);
        hm.put("IS_SERVER", true);
        hm.put("IS_AUTHTOKEN", false);
        hm.put("isApi", true);
        iconPath = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
        return iconPath;
    }
    
    public String getAndroidSplashScreenurl(final String path, final String splashImg) {
        String splashPath = path + "/" + splashImg;
        splashPath = splashPath.replace("/", File.separator);
        final HashMap hm = new HashMap();
        hm.put("path", splashPath);
        hm.put("IS_SERVER", true);
        hm.put("IS_AUTHTOKEN", false);
        hm.put("isApi", true);
        splashPath = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
        return splashPath;
    }
    
    public JSONObject getNotificaitonServiceJSON(final int platform, final Long customerID) {
        final JSONObject responseJSON = new JSONObject();
        try {
            final int serviceType = this.getNotificaitonServiceType(platform, customerID);
            responseJSON.put("CUSTOMER_ID".toLowerCase(), (Object)customerID);
            responseJSON.put("PLATFORM_TYPE".toLowerCase(), platform);
            responseJSON.put("SERVICE_TYPE".toLowerCase(), serviceType);
        }
        catch (final JSONException ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getNotificaitonServiceJSON ", (Throwable)ex);
        }
        return responseJSON;
    }
    
    private void handleAndroidSafetyNetSettings(final JSONObject settings) {
        if (!settings.optBoolean("WIPE_INTEGRITY_FAILED_DEVICES", false)) {
            if (!settings.optBoolean("WIPE_CTS_FAILED_DEVICES", false)) {
                return;
            }
        }
        try {
            this.logger.log(Level.INFO, " User has elected to wipe security failed devices, start a task to perform wipe");
            final JSONObject complianceSettings = new JSONObject();
            complianceSettings.put("WIPE_INTEGRITY_FAILED_DEVICES", settings.optBoolean("WIPE_INTEGRITY_FAILED_DEVICES"));
            complianceSettings.put("WIPE_CTS_FAILED_DEVICES", settings.optBoolean("WIPE_CTS_FAILED_DEVICES"));
            final CommonQueueData safetyNetData = new CommonQueueData();
            safetyNetData.setCustomerId(settings.optLong("CUSTOMER_ID"));
            safetyNetData.setClassName("com.adventnet.sym.server.mdm.security.safetynet.SafetyNetQueueActions");
            safetyNetData.setJsonQueueData(complianceSettings);
            safetyNetData.setTaskName("WipeAllDevices");
            CommonQueueUtil.getInstance().addToQueue(safetyNetData, CommonQueues.MDM_ENROLLMENT);
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, " Exception while starting task to wipe integrity compromised devices ", exp);
        }
    }
    
    static {
        MDMAgentSettingsHandler.agentSettingsHandler = null;
    }
}
