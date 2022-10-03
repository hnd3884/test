package com.me.mdm.server.profiles;

import com.me.mdm.server.profiles.ios.IOSPasscodeSingletonRestrictionHandler;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import com.adventnet.sym.server.mdm.apps.ios.AppleAppLicenseMgmtHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.apps.ManagedAppDataHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONException;
import java.util.List;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.profiles.kiosk.IOSKioskProfileDataHandler;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import java.util.logging.Level;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSInstallProfileResponseProcessor implements CommandResponseProcessor.ImmediateSeqResponseProcessor, CommandResponseProcessor.SeqQueuedResponseProcessor, CommandResponseProcessor.QueuedResponseProcessor
{
    private static Logger logger;
    public static String status;
    public static String remarks;
    public static String errorcode;
    private static HashMap<String, String> iOSRestrictionSupervisedMap;
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        final JSONObject seqResponse = new JSONObject();
        try {
            final JSONObject response = new JSONObject();
            final String status = params.optString("strStatus");
            final JSONObject seqParams = new JSONObject();
            if (status.equalsIgnoreCase("Acknowledged")) {
                response.put("action", 1);
            }
            else {
                response.put("action", 2);
                seqResponse.put("isNeedToAddQueue", true);
            }
            if (!status.equalsIgnoreCase("NotNow")) {
                seqParams.put("isNeedToRemove", true);
                response.put("resourceID", (Object)resourceID);
                response.put("commandUUID", (Object)commandUUID);
                response.put("params", (Object)seqParams);
                response.put("isNotify", params.optBoolean("isNotify", (boolean)Boolean.FALSE));
                SeqCmdRepository.getInstance().processSeqCommand(response);
            }
            else {
                seqResponse.put("isNeedToAddQueue", true);
            }
        }
        catch (final Exception e) {
            IOSInstallProfileResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing install profile immediate seq processing for resource:" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return seqResponse;
    }
    
    @Override
    public JSONObject processSeqQueuedCommand(final JSONObject params) {
        params.put("isNotify", (Object)Boolean.TRUE);
        return this.processImmediateSeqCommand(params);
    }
    
    public void processSucceededProfileCommand(final Long collectionID, final Long resourceId, final Long customerId) {
        String remarks = "dc.db.mdm.collection.Successfully_applied_policy";
        final int status = 6;
        Integer errorCode = null;
        try {
            final List configIds = MDMConfigUtil.getConfigIds(collectionID);
            if (configIds.contains(183)) {
                final JSONObject params = new JSONObject();
                params.put("collectionId", (Object)collectionID);
                params.put("resourceId", (Object)resourceId);
                params.put("customerId", (Object)customerId);
                remarks = this.processKioskResponse(params);
                if (remarks.equals("dc.db.mdm.collection.Successfully_applied_policy")) {
                    final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
                    final JSONObject kioskObject = kioskHandler.isProfileApplicableForIOSKioskAutomation(collectionID, customerId);
                    final Integer kioskMode = kioskObject.getInt("KIOSK_MODE");
                    final Long appGroupId = kioskObject.optLong("APP_GROUP_ID");
                    final Long appCollectionId = MDMUtil.getInstance().getProdCollectionIdFromAppGroupId(appGroupId);
                    final JSONObject updateAvailableJSON = kioskHandler.isAnyUpdateAvailableAppForResource(appCollectionId, resourceId);
                    if (updateAvailableJSON != null && kioskMode == 1) {
                        final JSONObject object = new JSONObject();
                        final JSONObject historyObject = updateAvailableJSON.optJSONObject("ResourceToProfileHistory");
                        final String columnName = "ASSOCIATED_BY".toLowerCase();
                        final Long userId = historyObject.optLong(columnName);
                        object.put("profileCollectionId", (Object)collectionID);
                        object.put("collectionId", (Object)appCollectionId);
                        object.put("resourceId", (Object)resourceId);
                        object.put("customerId", (Object)customerId);
                        object.put("UserId", (Object)userId);
                        final IOSKioskProfileDataHandler handler = new IOSKioskProfileDataHandler();
                        handler.addKioskAppUpdateSeqCmd(object);
                    }
                }
            }
            final boolean isSupervised = InventoryUtil.getInstance().isSupervisedDevice(resourceId);
            if (!isSupervised) {
                final ProfileResponseRemark responseRemark = this.processUnsupervisedRemark(configIds, 1, collectionID);
                final String responseRemarks = responseRemark.getFinalizedRemark();
                if (!MDMStringUtils.isEmpty(responseRemarks)) {
                    remarks = responseRemarks;
                }
                errorCode = responseRemark.errorCode;
            }
            final MDMCollectionStatusUpdate collnUpdater = MDMCollectionStatusUpdate.getInstance();
            if (errorCode != null && errorCode != 0) {
                collnUpdater.updateCollnToResErrorCode(resourceId, collectionID, errorCode);
            }
            collnUpdater.updateMdmConfigStatus(resourceId, String.valueOf(collectionID), status, remarks);
            final JSONObject listenerParams = new JSONObject();
            listenerParams.put("collectionId", (Object)collectionID);
            listenerParams.put("resourceId", (Object)resourceId);
            listenerParams.put("handler", 1);
            listenerParams.put("additionalParams", (Object)new JSONObject());
            listenerParams.put("platformType", 1);
            listenerParams.put("customerId", (Object)customerId);
            MDMProfileResponseListenerHandler.getInstance().invokeProfileListener(listenerParams);
        }
        catch (final Exception e) {
            IOSInstallProfileResponseProcessor.logger.log(Level.SEVERE, "Exception in processing profile com    mand", e);
        }
    }
    
    public JSONObject processFailureProfileCommand(final Long collectionID, final Long resourceId, final Long customerId) {
        try {
            final JSONObject listenerParams = new JSONObject();
            listenerParams.put("collectionId", (Object)collectionID);
            listenerParams.put("resourceId", (Object)resourceId);
            listenerParams.put("handler", 2);
            listenerParams.put("additionalParams", (Object)new JSONObject());
            listenerParams.put("platformType", 1);
            listenerParams.put("customerId", (Object)customerId);
            MDMProfileResponseListenerHandler.getInstance().invokeProfileListener(listenerParams);
        }
        catch (final JSONException e) {
            IOSInstallProfileResponseProcessor.logger.log(Level.SEVERE, "Exception in process failure profile command", (Throwable)e);
        }
        return null;
    }
    
    private String getHelpURL(final String remarks) {
        final HashMap pageSource = this.getPageSource();
        String helpUrl = null;
        if (remarks.contains("mdm.profile.ios.kiosk.automateApp") || remarks.contains("mdm.profile.ios.kiosk.appnotAvailable") || remarks.contains("mdm.profile.ios.kiosk.userBasedApps") || remarks.contains("mdm.profile.ios.kiosk.noappUserBased")) {
            helpUrl = "/help/profile_management/ios/mdm_app_lock.html";
        }
        return "@@@<a target='_blank' style='color: rgb(0, 0, 204);' href=\"$(mdmUrl)" + helpUrl + "?$(traceurl)&$(did)&pgSrc=" + pageSource.get(remarks) + "" + "#app_automation" + "\">@@@</a>";
    }
    
    public String processKioskResponse(final JSONObject params) throws Exception {
        final Long collectionId = params.optLong("collectionId");
        final Long resourceId = params.optLong("resourceId");
        final boolean isAppFailed = params.optBoolean("isAppFailed");
        final Long customerId = params.optLong("customerId");
        final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
        final String remarks = "dc.db.mdm.collection.Successfully_applied_policy";
        final JSONObject profileDetails = kioskHandler.isProfileApplicableForIOSKioskAutomation(collectionId, customerId);
        final Integer kioskType = profileDetails.optInt("KIOSK_MODE");
        if (kioskType == 1) {
            final Long appGroupID = profileDetails.optLong("APP_GROUP_ID");
            if (appGroupID != 0L) {
                final DataObject appObject = MDMUtil.getPersistenceLite().get("MdAppGroupDetails", new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID, 0));
                final Row appRow = appObject.getRow("MdAppGroupDetails");
                final String appName = (String)appRow.get("GROUP_DISPLAY_NAME");
                final boolean isVppApp = new ManagedAppDataHandler().isAppPurchasedFromPortal(appGroupID);
                if (isAppFailed) {
                    return "mdm.profile.ios.kiosk.appnotAvailable@@@" + appName + this.getHelpURL("mdm.profile.ios.kiosk.appnotAvailable") + this.getInstalledAppView(resourceId);
                }
                final String bundleIdentifier = (String)appRow.get("IDENTIFIER");
                final DataObject systemAppObject = AppsUtil.getInstance().getIOSSystemApps(new Criteria(new Column("IOSSystemApps", "IDENTIFIER"), (Object)bundleIdentifier, 0));
                final Integer packageType = AppsUtil.getInstance().getAppPackageType(appGroupID);
                if (!packageType.equals(2) && !isVppApp && systemAppObject.isEmpty()) {
                    return "mdm.profile.ios.kiosk.automateApp" + this.getHelpURL("mdm.profile.ios.kiosk.automateApp");
                }
            }
        }
        return remarks;
    }
    
    public String getAppViewURL(final Long collectionId, final int platformType) {
        try {
            final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionId);
            final Long packageId = AppsUtil.getInstance().getPackageId(appGroupId);
            final String helpUrl = "#/uems/mdm/manage/appRepo/apps/details?appId=" + packageId + "&appGroupId=" + appGroupId;
            return "@@@<a target='_blank' href=" + helpUrl + ">@@@</a>";
        }
        catch (final Exception e) {
            IOSInstallProfileResponseProcessor.logger.log(Level.SEVERE, "Exception in getting App view URl", e);
            return null;
        }
    }
    
    private HashMap getPageSource() {
        final HashMap pageSource = new HashMap();
        pageSource.put("mdm.profile.ios.kiosk.automateApp", "automateKiosk");
        pageSource.put("mdm.profile.ios.kiosk.appnotAvailable", "noKioskApp");
        pageSource.put("mdm.profile.ios.kiosk.userBasedApps", "userBased");
        pageSource.put("mdm.profile.ios.kiosk.appFailed", "kioskAppFailed");
        pageSource.put("mdm.profile.ios.kiosk.noappUserBased", "noAppUserBased");
        return pageSource;
    }
    
    private Integer getAppType(final Long appGroupId) {
        final HashMap appDetails = new AppleAppLicenseMgmtHandler().getAppLicenseDetails(appGroupId, null);
        if (appDetails != null) {
            final Integer appType = appDetails.get("appAssignmentType");
            return appType;
        }
        return 0;
    }
    
    private String getInstalledAppView(final Long resourceId) {
        final String helpUrl = "#/uems/mdm/inventory/devicesList/" + resourceId + "/installedApps";
        return "@@@<a ignorequickload=\"true\" target='_blank' style='color: rgb(0, 0, 204);' href=" + helpUrl + ">@@@</a>";
    }
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        try {
            final String commandStatus = params.optString("strStatus");
            final String commandUUID = params.optString("strCommandUuid");
            final String strData = params.optString("strData");
            final Long resourceID = params.optLong("resourceId");
            final Long customerId = params.optLong("customerId");
            final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            if (commandStatus.contains("Acknowledged")) {
                this.processSucceededProfileCommand(Long.parseLong(collectionId), resourceID, customerId);
            }
            else if (commandStatus.contains("Error")) {
                final IOSErrorStatusHandler errorStatusHandler = new IOSErrorStatusHandler();
                final JSONObject errorHash = errorStatusHandler.getIOSErrors(commandUUID, strData, commandStatus);
                final String remarks = errorHash.optString("EnglishRemarks");
                final Integer errorCode = Integer.parseInt(errorHash.optString("ErrorCode"));
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 7, remarks);
                if (IOSErrorStatusHandler.IOS_PROFILE_ERROR_CODE_KB_LIST.contains(errorCode)) {
                    MDMCollectionStatusUpdate.getInstance().updateCollnToResErrorCode(resourceID, Long.parseLong(collectionId), errorCode);
                }
                this.processFailureProfileCommand(Long.parseLong(collectionId), resourceID, customerId);
            }
        }
        catch (final Exception ex) {
            IOSInstallProfileResponseProcessor.logger.log(Level.SEVERE, "Exception while processing install profile command");
        }
        return null;
    }
    
    private ProfileResponseRemark processUnsupervisedRemark(final List configIds, final int platformType, final Long collectionId) {
        final ProfileResponseRemark remark = ProfileResponseRemark.getInstance(platformType);
        if (configIds.contains(173) && new RestrictionProfileHandler().isRestrictionConfigured(collectionId, IOSInstallProfileResponseProcessor.iOSRestrictionSupervisedMap, "RestrictionsPolicy")) {
            remark.appendRemark("mdm.profile.ios.restriction");
        }
        if (configIds.contains(172) && new RestrictionProfileHandler().isRestrictionConfigured(collectionId, IOSPasscodeSingletonRestrictionHandler.PASSCODE_RESTRICTION, "PasscodePolicy")) {
            remark.appendRemark("dc.mdm.enroll.passcode");
        }
        if (configIds.contains(518)) {
            remark.appendRemark("dc.conf.dispConf.wallpaper");
        }
        if (configIds.contains(183)) {
            remark.appendRemark("dc.mdm.profile.android.kiosk");
        }
        if (configIds.contains(522)) {
            remark.appendRemark("mdm.profile.assetTagging");
        }
        if (configIds.contains(529)) {
            remark.appendRemark("mdm.profile.ios.accessibility_settings");
        }
        return remark;
    }
    
    static {
        IOSInstallProfileResponseProcessor.logger = Logger.getLogger("MDMLogger");
        IOSInstallProfileResponseProcessor.status = "status";
        IOSInstallProfileResponseProcessor.remarks = "remarks";
        IOSInstallProfileResponseProcessor.errorcode = "errorcode";
        IOSInstallProfileResponseProcessor.iOSRestrictionSupervisedMap = new HashMap<String, String>() {
            {
                this.put("ALLOW_FACE_TIME", "false");
                this.put("ALLOW_SPOTLIGHT_RESULT", "false");
                this.put("ALLOW_IMESSAGE", "false");
                this.put("ALLOW_AIRDROP", "false");
                this.put("FORCE_ASSIST_PROFANITY_FILTER", "true");
                this.put("ALLOW_ASSISTANT_USER_CONTENT", "false");
                this.put("ALLOW_MODIFI_DEVICE_NAME", "false");
                this.put("ALLOW_AIRPRINT", "false");
                this.put("ALLOW_AIRPRINT_CREDENTIAL_STORAGE", "false");
                this.put("FORCE_AIRPRINT_TLS", "true");
                this.put("ALLOW_AIRPRINT_IBEACON_DISCOVERY", "false");
                this.put("FORCE_AIRDROP_UNMANAGED", "true");
                this.put("ALLOW_ERASE_CONTENT_SETTINGS", "false");
                this.put("ALLOW_MODIFY_TOUCH_ID", "false");
                this.put("ALLOW_PROFILE_INSTALLATION", "false");
                this.put("ALLOW_ACCOUNT_MODIFICATION", "false");
                this.put("ALLOW_HOST_PAIRING", "false");
                this.put("ALLOW_PAIRED_WATCH", "false");
                this.put("ALLOW_APP_INSTALLATION", "false");
                this.put("ALLOW_APP_REMOVAL", "false");
                this.put("ALLOW_MANAGED_APP_TRUST", "false");
                this.put("ALLOW_AUTO_APP_DOWNLOAD", "false");
                this.put("ALLOW_GAME_CENTER", "false");
                this.put("ALLOW_MULTIPLAYER_GAMING", "false");
                this.put("ALLOW_ADD_GAME_CENTER_FRIEND", "false");
                this.put("ALLOW_ITUNES", "false");
                this.put("ALLOW_PODCASTS", "false");
                this.put("ALLOW_NEWS", "false");
                this.put("ALLOW_MUSIC_SERVICE", "false");
                this.put("ALLOW_RADIO_SERVICE", "false");
                this.put("ALLOW_USE_OF_IBOOKSTORE", "false");
                this.put("ALLOW_IBOOKSTORE_EROTICA_MEDIA", "false");
                this.put("ALLOW_SAFARI", "false");
                this.put("ALLOW_APP_CELLULAR_DATA", "false");
                this.put("ALLOW_BLUETOOTH_MODIFICATION", "false");
                this.put("FORCE_WIFI_WHITELISTING", "true");
                this.put("ALLOW_VPN_CREATION", "false");
                this.put("ALLOW_CLOUD_DOCUMENT_SYNC", "false");
                this.put("ALLOW_FIND_MY_FRIENDS_MOD", "false");
                this.put("ALLOW_DIAG_SUB_MODIFICATION", "false");
                this.put("ALLOW_EXPLICIT_CONTENT", "false");
                this.put("ALLOW_DICTIONARY_LOOKUP", "false");
                this.put("ALLOW_PREDICTIVE_KEYBOARD", "false");
                this.put("ALLOW_AUTO_CORRECTION", "false");
                this.put("ALLOW_SPELLCHECK", "false");
                this.put("ALLOW_KEYBOARD_SHORTCUT", "false");
                this.put("ALLOW_DICTATION", "false");
                this.put("FORCE_CLASSROOM_AUTO_JOIN", "true");
                this.put("FORCE_CLASSROOM_APPDEVICELOCK", "true");
                this.put("ALLOW_CLASSROOM_REMOTEVIEW", "false");
                this.put("FORCE_CLASSROOM_REMOTEVIEW", "true");
                this.put("REQUEST_TO_LEAVE_CLASSROOM", "true");
                this.put("ALLOW_PROXIMITY_FOR_NEWDEVICE", "false");
                this.put("AUTHENTICATE_BEFORE_AUTOFILL", "true");
                this.put("FORCE_DATE_TIME", "true");
                this.put("ALLOW_PASSWORD_AUTOFILL", "false");
                this.put("ALLOW_PASSWORD_PROXIMITY", "false");
                this.put("ALLOW_PASSWORD_SHARING", "false");
                this.put("ALLOW_UNMANAGED_READ_MANAGED_CONTACT", "true");
                this.put("ALLOW_USB_RESTRICTION_MODE", "false");
                this.put("AIRPLAY_INCOMING_REQUEST", "true");
                this.put("ALLOW_DEVICE_SLEEP", "true");
                this.put("ALLOW_REMOTE_APP_PAIRING", "true");
                this.put("ALLOW_APP_CLIPS", "true");
                this.put("ALLOW_CELLULAR_PLAN_MODIFICATION", "true");
                this.put("ALLOW_FILE_NETWORK_DRIVE_ACCESS", "true");
            }
        };
    }
}
