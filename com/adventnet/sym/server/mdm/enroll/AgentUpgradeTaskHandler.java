package com.adventnet.sym.server.mdm.enroll;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleManagedAccountHandler;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.apps.BlacklistWhitelistAppHandler;
import java.util.Properties;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.featuresettings.MDMFeatureSettingsDBHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.me.mdm.server.android.agentmigrate.AgentMigrationHandler;
import com.adventnet.sym.server.mdm.android.AndroidInventory;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AgentUpgradeTaskHandler
{
    Long resourceID;
    private boolean isSettingsToBeProcessed;
    private static final Logger LOGGER;
    
    public AgentUpgradeTaskHandler() {
        this.resourceID = null;
        this.isSettingsToBeProcessed = true;
    }
    
    public void processAgentUpgrade(final JSONObject resData) {
        try {
            final String udid = resData.optString("UDID");
            final Row row = DBUtil.getRowFromDB("ManagedDevice", "UDID", (Object)udid);
            if (row != null) {
                resData.put("AGENT_TYPE", (Object)row.get("AGENT_TYPE"));
                resData.put("RESOURCE_ID", (Object)row.get("RESOURCE_ID"));
                resData.put("AGENT_VERSION_CODE", (Object)row.get("AGENT_VERSION_CODE"));
                resData.put("PLATFORM_TYPE", (Object)row.get("PLATFORM_TYPE"));
                this.handleTaskAfterAgentUpgraded(resData);
            }
        }
        catch (final Exception ex) {
            AgentUpgradeTaskHandler.LOGGER.log(Level.SEVERE, "Exception in processAgentUpgrade ", ex);
        }
    }
    
    public void handleTaskAfterAgentUpgraded(final JSONObject resData) {
        this.resourceID = resData.optLong("RESOURCE_ID");
        final Integer agentType = resData.optInt("AGENT_TYPE");
        if (agentType == 3) {
            this.processBlacklistActionForNewAgent(resData);
        }
        this.processSyncSettings(resData, 20);
        this.processGeoLocationActionForNewAgent(resData);
        this.processSyncSettings(resData, 25);
        this.processLanguagePackCommand(resData);
        this.processKNOXAvailability(resData, 40);
        this.processSyncSettings(resData, 40);
        this.processAddAFWAccountCommand(resData);
        this.processSyncSettings(resData, 244);
        this.processSyncSettings(resData, 248);
        this.processPublicKeyDistributor(resData, 268);
        this.processSyncSettings(resData, 279);
        this.processPrivacySettingsNewAgent(resData);
        AndroidInventory.getInstance().initSystemAppCommand(resData.optLong("RESOURCE_ID"));
        this.processAgentMigrate(resData, AgentMigrationHandler.SAFE_AGENT_MIGRATION_SUPPORTED_VERSION);
        this.processSyncSettings(resData, 331);
        this.processRefreshTokenUpdate(resData, 452);
        this.processCapabilitiesInfo(resData, 431);
        this.processSyncSettings(resData, 455);
        this.processSyncSettings(resData, 536);
        this.processSyncDownloadSettings(resData, 530);
        this.processBatterySettings(resData, 514);
        this.processSyncSettings(resData, 581);
        this.processAndroidPasscodeRecoveryCommand(resData, 563);
        this.processKNOXAvailability(resData, 2300579);
        this.processSyncSettings(resData, 594);
        this.processSyncSettings(resData, 606);
    }
    
    private void processAndroidPasscodeRecoveryCommand(final JSONObject resData, final int compatibleVersionCode) {
        final Long lastVersionCode = resData.optLong("LAST_VERSION_CODE") % 10000L;
        final Long newVersionCode = resData.optLong("AGENT_VERSION_CODE") % 10000L;
        final Long resourceID = resData.optLong("RESOURCE_ID");
        try {
            if (lastVersionCode < compatibleVersionCode && newVersionCode >= compatibleVersionCode) {
                final ArrayList<Long> devices = new ArrayList<Long>();
                devices.add(resourceID);
                DeviceCommandRepository.getInstance().addAndroidPasscodeRecoveryCommand(devices, 1);
            }
        }
        catch (final Exception e) {
            AgentUpgradeTaskHandler.LOGGER.log(Level.WARNING, "Exception while adding Android Passcode Recovery command during agent update ", e);
        }
    }
    
    private void processBatterySettings(final JSONObject resData, final int compatibleVersionCode) {
        final Long lastVersionCode = resData.optLong("LAST_VERSION_CODE") % 10000L;
        final Long newVersionCode = resData.optLong("AGENT_VERSION_CODE") % 10000L;
        final Long resourceID = resData.optLong("RESOURCE_ID");
        try {
            final boolean isFeatureEnabledForDevice = MDMFeatureSettingsDBHandler.checkIfFeatureEnabledForDevice(1, resourceID);
            if (lastVersionCode < compatibleVersionCode && newVersionCode >= compatibleVersionCode && isFeatureEnabledForDevice) {
                final ArrayList<Long> devices = new ArrayList<Long>();
                devices.add(resourceID);
                DeviceCommandRepository.getInstance().addBatteryConfigurationCommand(devices, 1);
                NotificationHandler.getInstance().SendNotification(devices, 2);
            }
        }
        catch (final Exception e) {
            AgentUpgradeTaskHandler.LOGGER.log(Level.WARNING, "Exception while sending notification for battery configuration");
        }
    }
    
    private void processPublicKeyDistributor(final JSONObject resData, final int versionCode) {
        final Long lastVersionCode = resData.optLong("LAST_VERSION_CODE") % 10000L;
        final Long newVersionCode = resData.optLong("AGENT_VERSION_CODE") % 10000L;
        if (lastVersionCode < versionCode && newVersionCode >= versionCode) {
            DeviceCommandRepository.getInstance().addSmsPublicKeyDistributorCommand(resData.optLong("RESOURCE_ID"));
            final List resList = new ArrayList();
            resList.add(resData.optLong("RESOURCE_ID"));
            try {
                NotificationHandler.getInstance().SendNotification(resList, 2);
            }
            catch (final Exception exp) {
                AgentUpgradeTaskHandler.LOGGER.log(Level.SEVERE, "Exception in pushing commands to devices : {0}", exp.getMessage());
            }
        }
        else {
            AgentUpgradeTaskHandler.LOGGER.log(Level.INFO, "The app is not yet in the latest version");
        }
    }
    
    private void processKNOXAvailability(final JSONObject resData, final int versionCode) {
        final Long lastVersioCode = resData.optLong("LAST_VERSION_CODE");
        final Long newVersionCode = resData.optLong("AGENT_VERSION_CODE");
        if (lastVersioCode < versionCode && newVersionCode >= versionCode) {
            final List resList = new ArrayList();
            resList.add(resData.optLong("RESOURCE_ID"));
            DeviceCommandRepository.getInstance().addKNOXAvailabilityCommand(resList, "GetKnoxAvailabilityUpgrade");
            try {
                NotificationHandler.getInstance().SendNotification(resList, 2);
            }
            catch (final Exception ex) {
                AgentUpgradeTaskHandler.LOGGER.log(Level.SEVERE, "Exception in processKNOXAvailability", ex);
            }
        }
    }
    
    private void processAgentMigrate(final JSONObject resData, final int versionCode) {
        final Long lastVersioCode = resData.optLong("LAST_VERSION_CODE");
        final Long newVersionCode = resData.optLong("AGENT_VERSION_CODE");
        if (lastVersioCode < versionCode && newVersionCode >= versionCode) {
            final Long resourceId = resData.optLong("RESOURCE_ID");
            try {
                final List<Long> resList = new ArrayList<Long>();
                resList.add(resourceId);
                AgentMigrationHandler.getInstance().initiateAgentMigrationForDevice(resList);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void processSyncSettings(final JSONObject resData, final int versionCode) {
        if (this.isSettingsToBeProcessed) {
            final Long lastVersioCode = resData.optLong("LAST_VERSION_CODE") % 100000L;
            final Long newVersionCode = resData.optLong("AGENT_VERSION_CODE") % 100000L;
            if (lastVersioCode < versionCode && newVersionCode >= versionCode) {
                final List resList = new ArrayList();
                resList.add(resData.optLong("RESOURCE_ID"));
                DeviceCommandRepository.getInstance().addSyncAgentSettingsCommandForAndroid(resList);
                try {
                    NotificationHandler.getInstance().SendNotification(resList, 2);
                }
                catch (final Exception ex) {
                    AgentUpgradeTaskHandler.LOGGER.log(Level.SEVERE, "Exception in processSyncSettings", ex);
                }
                this.isSettingsToBeProcessed = false;
            }
        }
    }
    
    private void processSyncDownloadSettings(final JSONObject resData, final int versionCode) {
        final Long lastVersionCode = resData.optLong("LAST_VERSION_CODE") % 100000L;
        final Long newVersionCode = resData.optLong("AGENT_VERSION_CODE") % 100000L;
        if (lastVersionCode < versionCode && newVersionCode >= versionCode) {
            final List resList = new ArrayList();
            resList.add(resData.optLong("RESOURCE_ID"));
            DeviceCommandRepository.getInstance().addSyncDownloadSettingsCommand(resList, 1);
            try {
                NotificationHandler.getInstance().SendNotification(resList, 2);
            }
            catch (final Exception ex) {
                AgentUpgradeTaskHandler.LOGGER.log(Level.SEVERE, "Exception in processSyncDownloadSettings", ex);
            }
        }
    }
    
    private void processCapabilitiesInfo(final JSONObject resData, final int versionCode) {
        final Long lastVersionCode = resData.optLong("LAST_VERSION_CODE") % 100000L;
        final Long newVersionCode = resData.optLong("AGENT_VERSION_CODE") % 100000L;
        if (lastVersionCode < versionCode && newVersionCode >= versionCode) {
            final List resList = new ArrayList();
            resList.add(resData.optLong("RESOURCE_ID"));
            DeviceCommandRepository.getInstance().addCapabilitiesInfoCommand(resList);
        }
    }
    
    private void processBlacklistActionForNewAgent(final JSONObject resData) {
        final Long lastVersioCode = resData.optLong("LAST_VERSION_CODE");
        final Long newVersionCode = resData.optLong("AGENT_VERSION_CODE");
        if (lastVersioCode < 11L && newVersionCode >= 11L) {
            final Properties props = new Properties();
            ((Hashtable<String, Long>)props).put("RESOURCE_ID", this.resourceID);
            ((Hashtable<String, Integer>)props).put("APP_STATUS", 2);
            ((Hashtable<String, Integer>)props).put("COMMAND_STATUS", -1);
            BlacklistWhitelistAppHandler.getInstance().updateBlacklistWhitelistAppCommandStatus(props);
        }
    }
    
    private void processGeoLocationActionForNewAgent(final JSONObject resData) {
        final Long lastVersioCode = resData.optLong("LAST_VERSION_CODE");
        final Long newVersionCode = resData.optLong("AGENT_VERSION_CODE");
        if (lastVersioCode < 11L && newVersionCode >= 11L) {
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(this.resourceID);
            try {
                final Integer trackingStatus = (Integer)DBUtil.getValueFromDB("LocationSettings", "CUSTOMER_ID", (Object)customerId, "TRACKING_STATUS");
                if (trackingStatus == 1) {
                    final List resourceList = new ArrayList();
                    resourceList.add(this.resourceID);
                    DeviceCommandRepository.getInstance().addSyncAgentSettingsCommandForAndroid(resourceList);
                }
            }
            catch (final Exception ex) {
                AgentUpgradeTaskHandler.LOGGER.log(Level.SEVERE, "Exception in processGeoLocationActionForNewAgent", ex);
            }
        }
    }
    
    private void processPrivacySettingsNewAgent(final JSONObject resData) {
        final Long lastVersioCode = resData.optLong("LAST_VERSION_CODE") % 100000L;
        final Long newVersionCode = resData.optLong("AGENT_VERSION_CODE") % 100000L;
        if (lastVersioCode < 314L && newVersionCode >= 314L) {
            final List resourceList = new ArrayList();
            resourceList.add(this.resourceID);
            DeviceCommandRepository.getInstance().addSyncPrivacySettingsCommand(resourceList, 1);
        }
    }
    
    private void processLanguagePackCommand(final JSONObject resData) {
        final Long lastVersioCode = resData.optLong("LAST_VERSION_CODE");
        final Long newVersionCode = resData.optLong("AGENT_VERSION_CODE");
        if (lastVersioCode < 28L && newVersionCode >= 28L) {
            final List resList = new ArrayList();
            resList.add(resData.optLong("RESOURCE_ID"));
            DeviceCommandRepository.getInstance().addLanguageLicenseCommand(resList, 1);
            try {
                NotificationHandler.getInstance().SendNotification(resList, 2);
            }
            catch (final Exception ex) {
                AgentUpgradeTaskHandler.LOGGER.log(Level.SEVERE, "Exception in processLanguagePackCommand", ex);
            }
        }
    }
    
    private void processAddAFWAccountCommand(final JSONObject data) {
        this.resourceID = data.optLong("RESOURCE_ID");
        final Long lastVersionCode = data.optLong("LAST_VERSION_CODE") % 100000L;
        final Long newVersionCode = data.optLong("AGENT_VERSION_CODE") % 100000L;
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(this.resourceID);
        try {
            if (GoogleForWorkSettings.isEMMTypeAFWConfigured(customerId)) {
                if (lastVersionCode < 253L && newVersionCode >= 253L) {
                    AgentUpgradeTaskHandler.LOGGER.log(Level.INFO, "Processing processAddAFWAccountCommand on upgrade 253{0}", data);
                    new GoogleManagedAccountHandler().checkAndAddAFWAccountForAll(this.resourceID, customerId, newVersionCode);
                    return;
                }
                if (lastVersionCode < 220L && newVersionCode >= 220L) {
                    AgentUpgradeTaskHandler.LOGGER.log(Level.INFO, "Processing processAddAFWAccountCommand on upgrade 220{0}", data);
                    if (ManagedDeviceHandler.getInstance().isDeviceOwner(this.resourceID) || ManagedDeviceHandler.getInstance().isProfileOwner(this.resourceID)) {
                        final String udid = (String)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)this.resourceID, "UDID");
                        new GoogleManagedAccountHandler().addAFWAccountAdditionCmd(this.resourceID, udid, customerId);
                    }
                }
                if (lastVersionCode < 233L & newVersionCode >= 233L) {
                    AgentUpgradeTaskHandler.LOGGER.log(Level.INFO, "Processing processAddAFWAccountCommand on upgrade {0} 233", data);
                    final String osVersion = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)this.resourceID, "OS_VERSION");
                    new GoogleManagedAccountHandler().checkAndAddAFWAccountForSamsung(this.resourceID, newVersionCode, osVersion);
                }
            }
        }
        catch (final Exception ex) {
            AgentUpgradeTaskHandler.LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    private void processRefreshTokenUpdate(final JSONObject resData, final int versionCode) {
        try {
            final Long lastVersioCode = resData.optLong("LAST_VERSION_CODE") % 100000L;
            final Long newVersionCode = resData.optLong("AGENT_VERSION_CODE") % 100000L;
            if (lastVersioCode < versionCode && newVersionCode >= versionCode) {
                final List resList = new ArrayList();
                resList.add(resData.optLong("RESOURCE_ID"));
                DeviceCommandRepository.getInstance().addRefreshTokenUpdateCommand(resList);
                try {
                    NotificationHandler.getInstance().SendNotification(resList, 2);
                }
                catch (final Exception ex) {
                    AgentUpgradeTaskHandler.LOGGER.log(Level.SEVERE, "Exception in processSyncSettings", ex);
                }
            }
        }
        catch (final Exception ex2) {
            AgentUpgradeTaskHandler.LOGGER.log(Level.SEVERE, null, ex2);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(AgentUpgradeTaskHandler.class.getName());
    }
}
