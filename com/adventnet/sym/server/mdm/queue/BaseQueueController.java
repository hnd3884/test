package com.adventnet.sym.server.mdm.queue;

import com.me.mdm.agent.handlers.DeviceCommandResponse;
import com.me.mdm.agent.handlers.DeviceMessageRequest;
import com.adventnet.sym.server.mdm.PlistWrapper;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.HashMap;
import java.util.logging.Logger;

public class BaseQueueController
{
    protected static Logger mdmLogger;
    protected static Logger queueLogger;
    
    public QueueName getQueueName(String deviceCommand, final String strCommandUuid) {
        if (strCommandUuid.equalsIgnoreCase("DefaultWebClipsPayload")) {
            return QueueName.ENROLLMENT_DATA;
        }
        if (deviceCommand.contains("ResponseType")) {
            final HashMap hashMap = this.convertStringToHash(deviceCommand);
            deviceCommand = hashMap.get("ResponseType");
        }
        final String s = deviceCommand;
        switch (s) {
            case "EraseDevice":
            case "CorporateWipe":
            case "Enrollment":
            case "RemoveDevice":
            case "RemoveOldAgent":
            case "DeviceConfigured":
            case "AccountConfiguration":
            case "SetBootstrapToken":
            case "SharedDeviceConfiguration": {
                return QueueName.ENROLLMENT_DATA;
            }
            case "TokenUpdate":
            case "ReregisterNotificationToken": {
                return QueueName.ENROLLMENT_DATA_ID;
            }
            case "GetKnoxAvailability":
            case "ClearPasscode":
            case "ResetPasscode":
            case "DeviceRing":
            case "PlayLostModeSound":
            case "ResumeKioskCommand":
            case "PauseKioskCommand":
            case "ActivateKnoxLicense":
            case "DeactivateKnoxLicense":
            case "CreateContainer":
            case "RemoveContainer":
            case "ContainerLock":
            case "ContainerUnlock":
            case "ClearContainerPasscode":
            case "DeactivateKnox":
            case "ActivateKnox":
            case "LocationConfiguration":
            case "EnableLostMode":
            case "DisableLostMode":
            case "RemoteSession":
            case "UnlockUserAccount":
            case "GetLocation":
            case "DeviceLock":
            case "MacFirmwareSetPasscode":
            case "MacFirmwareClearPasscode":
            case "MacFirmwareVerifyPassword":
            case "MacFirmwarePostSecurityInfo":
            case "MacFirmwarePreSecurityInfo":
            case "FileVaultUserLoginSecurityInfo":
            case "MigrateUrl": {
                return QueueName.SECURITY_DATA;
            }
            case "RestartDevice":
            case "ShutDownDevice":
            case "LostModeDeviceLocation":
            case "BATTERY_CONFIGURATION": {
                return QueueName.PASSIVE_EVENTS;
            }
            case "AddAFWAccount":
            case "DeviceAlerts":
            case "ManagedUserLoginUpdate":
            case "SyncDeviceDetails": {
                return QueueName.ACTIVE_EVENTS;
            }
            case "DeviceInformation":
            case "SecurityInfo":
            case "InstalledApplicationList":
            case "FetchAppleAgentDetails":
            case "ManagedAppsOnly":
            case "ManagedApplicationList":
            case "CertificateList":
            case "Restrictions":
            case "UserList":
            case "ProfileList":
            case "AndroidInvScan":
            case "AndroidInvScanContainer":
            case "AssetScan":
            case "AssetScanContainer":
            case "PreloadedAppsInfo":
            case "PreloadedContainerAppsInfo":
            case "PersonalAppsInfo":
            case "DeviceInfo":
            case "DeviceName":
            case "IOSRemoveDeviceNameRestriction":
            case "ProvisioningProfileList": {
                return QueueName.ASSET_DATA;
            }
            case "SyncAgentSettings":
            case "AgentUpgrade":
            case "RegistrationStatusUpdate":
            case "InstallProfile;Collection=UpgradeMobileConfig4":
            case "InstallProfile;Collection=UpgradeMobileConfig5": {
                return QueueName.AGENT_UPGRADE;
            }
            case "InstallProfile":
            case "KioskDefaultRestriction":
            case "RemoveProfile":
            case "DefaultMDMKioskProfile":
            case "DefaultMDMRemoveKioskProfile":
            case "RemoveKioskDefaultRestriction":
            case "RemoveUserInstalledProfile":
            case "InstallManagedSettings":
            case "LockScreenMessages":
            case "DisablePasscode":
            case "RemoveDisablePasscode":
            case "ClearPasscodeForPasscodeRestriction":
            case "RestrictPasscode":
            case "RemoveRestrictedPasscode":
            case "ClearPasscodeRestriction":
            case "SingletonRestriction":
            case "RemoveSingletonRestriction":
            case "DeviceCompliance":
            case "SingleWebAppKioskAppConfiguration":
            case "SingleWebAppKioskFeedback":
            case "RemoveSingleWebAppKioskAppConfiguration":
            case "RemoveSingleWebAppKioskFeedback": {
                return QueueName.PROFILE_COLLECTION_STATUS;
            }
            case "InstallApplication":
            case "RemoveApplication":
            case "ApplicationConfiguration":
            case "MDMDefaultApplicationConfiguration":
            case "DefaultAppCatalogWebClips":
            case "DefaultRemoveAppCatalogWebClips":
            case "InstallApplicationConfiguration":
            case "RemoveApplicationConfiguration":
            case "InstallEnterpriseApplication":
            case "ApplyRedemptionCode":
            case "InviteToProgram":
            case "ManageApplication":
            case "MDMDefaultApplicationConfigMigrate":
            case "DefaultAppCatalogWebClipsMigrate": {
                return QueueName.APP_COLLECTION_STATUS;
            }
            case "BlacklistWhitelistApp":
            case "BlacklistWhitelistAppContainer":
            case "BlacklistAppInDevice":
            case "BlacklistAppInContainer":
            case "RemoveBlacklistAppInDevice":
            case "RemoveBlacklistAppInContainer": {
                return QueueName.BLACKLIST_COLLECTION_STATUS;
            }
            case "OSUpdateStatus":
            case "ScheduleOSUpdate":
            case "AttemptOSUpdate":
            case "OsUpdatePolicy":
            case "RemoveOsUpdatePolicy":
            case "RestrictOSUpdates":
            case "RemoveRestrictOSUpdates":
            case "AvailableOSUpdates": {
                return QueueName.OSUPDATE_COLLECTION_STATUS;
            }
            default: {
                return QueueName.OTHERS;
            }
        }
    }
    
    public QueueName getCommandQueueName(final String deviceCommand) {
        return this.getQueueName(deviceCommand, "");
    }
    
    protected HashMap<?, ?> convertStringToHash(final String queueData) {
        try {
            final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(queueData));
            return hmap;
        }
        catch (final JSONException ex) {
            BaseQueueController.mdmLogger.log(Level.SEVERE, "Exception while processing android command", (Throwable)ex);
            return null;
        }
    }
    
    protected HashMap getHashFromPlist(final String queueData) {
        return PlistWrapper.getInstance().getHashFromPlist(queueData);
    }
    
    protected HashMap getHashFromJson(final String queueData) {
        try {
            return JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(queueData));
        }
        catch (final Exception exp) {
            BaseQueueController.mdmLogger.log(Level.INFO, "IOSQueueController : Cannot get Hash from JSON ", exp);
            return null;
        }
    }
    
    protected DeviceMessageRequest getDeviceMessageFromQueueData(final String queueData) {
        try {
            return new DeviceMessageRequest(new JSONObject(queueData));
        }
        catch (final Exception exp) {
            BaseQueueController.mdmLogger.log(Level.SEVERE, "Cannot convert the queue data to DeviceMessageRequest", exp);
            return null;
        }
    }
    
    protected DeviceCommandResponse getDeviceCommandFromQueueData(final String queueData) {
        try {
            return new DeviceCommandResponse(new JSONObject(queueData));
        }
        catch (final Exception exp) {
            BaseQueueController.mdmLogger.log(Level.SEVERE, "Cannot convert the queue data to DeviceCommandResponse", exp);
            return null;
        }
    }
    
    static {
        BaseQueueController.mdmLogger = Logger.getLogger("MDMLogger");
        BaseQueueController.queueLogger = Logger.getLogger("MDMQueueBriefLogger");
    }
}
