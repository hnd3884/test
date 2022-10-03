package com.me.mdm.server.updates.osupdates.ios;

import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.me.mdm.server.notification.PushNotificationHandler;
import com.adventnet.persistence.Row;
import com.dd.plist.NSArray;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.updates.osupdates.ResourceOSUpdateDataHandler;
import com.dd.plist.NSDictionary;
import com.me.mdm.server.updates.osupdates.OSUpdateConstants;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.PlistWrapper;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSUpdateStatusResponseHandler implements CommandResponseProcessor.SeqQueuedResponseProcessor
{
    private final Logger logger;
    private final String status_code = "STATUS_CODE";
    private String collectionId;
    
    public IOSUpdateStatusResponseHandler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.collectionId = null;
    }
    
    private JSONObject processResponse(final Long resourceID, final String responseStr, final String cmdUUID) {
        final JSONObject response = new JSONObject();
        try {
            final String cmdStatus = PlistWrapper.getInstance().getValueForKeyString("Status", responseStr);
            if (cmdStatus.contains("Error")) {
                this.logger.log(Level.SEVERE, "OSUpdateStatus command error response");
                response.put("STATUS_CODE", (Object)OSUpdateConstants.DeviceStatus.GENERAL_FAILED);
            }
            else if (cmdStatus.equalsIgnoreCase("Acknowledged")) {
                final NSArray updateResultsArray = PlistWrapper.getInstance().getArrayForKey("OSUpdateStatus", responseStr);
                if (updateResultsArray != null && updateResultsArray.count() > 0) {
                    final NSDictionary updateResultDictionary = (NSDictionary)updateResultsArray.objectAtIndex(0);
                    final String statusString = (updateResultDictionary.get((Object)"Status") == null) ? null : updateResultDictionary.get((Object)"Status").toString();
                    final String downloadPercent = (updateResultDictionary.get((Object)"DownloadPercentComplete") == null) ? null : updateResultDictionary.get((Object)"DownloadPercentComplete").toString();
                    final Boolean isDownloaded = Boolean.parseBoolean(updateResultDictionary.get((Object)"IsDownloaded").toString());
                    final int statusCode = this.getOSUpdateStatusConstant(statusString, isDownloaded);
                    final String productKey = (updateResultDictionary.get((Object)"ProductKey") == null) ? null : updateResultDictionary.get((Object)"ProductKey").toString();
                    boolean struckForLongerTime = false;
                    if (statusCode == OSUpdateConstants.DeviceStatus.DOWNLOADING) {
                        String remarks = "";
                        if (downloadPercent != null) {
                            remarks = " Download percent (0.0 to 1.0): ".concat(downloadPercent);
                        }
                        final Row deviceAvailableRow = new ResourceOSUpdateDataHandler().getDeviceAvailableRowFromProductKey(resourceID, productKey);
                        final String downloadPercentFromDB = (String)deviceAvailableRow.get("DOWNLOAD_PERCENT");
                        Long downloadUpdatedTime = -1L;
                        String downloadPercentage = "";
                        if (!downloadPercentFromDB.equalsIgnoreCase(downloadPercent)) {
                            downloadUpdatedTime = System.currentTimeMillis();
                            downloadPercentage = downloadPercent;
                        }
                        else {
                            final Long downloadPercentUpdateAt = (Long)deviceAvailableRow.get("DOWNLOAD_PERCENT_UPDATED_AT");
                            final Long threshold = 3600000L;
                            final Long currentMilliSec = System.currentTimeMillis();
                            if (downloadPercentUpdateAt < threshold + currentMilliSec) {
                                struckForLongerTime = true;
                            }
                        }
                        new ResourceOSUpdateDataHandler().updateResourceOSUpdateStatus(resourceID, statusCode, remarks, -1L, productKey, downloadPercentage, downloadUpdatedTime);
                    }
                    String collectionRemarks = "";
                    if (struckForLongerTime) {
                        collectionRemarks = "mdm.db.osupdate.download_take_more_time@@@<l>$(mdmUrl)/help/os_update_management/mdm_automate_os_updates.html?$(traceurl)&$(did)&pgSrc=delayedUpdates#iOS";
                    }
                    else if (statusCode == OSUpdateConstants.DeviceStatus.DOWNLOADING) {
                        collectionRemarks = "mdm.db.osupdate.update_downloading";
                    }
                    else if (statusCode == OSUpdateConstants.DeviceStatus.INSTALLING) {
                        collectionRemarks = "mdm.db.osupdate.update_downloading";
                    }
                    if (!MDMStringUtils.isEmpty(collectionRemarks)) {
                        MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, this.collectionId, 6, collectionRemarks);
                    }
                    response.put("STATUS_CODE", statusCode);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in processing osupdate status", ex);
            response.put("STATUS_CODE", (Object)OSUpdateConstants.DeviceStatus.GENERAL_FAILED);
        }
        return response;
    }
    
    private int getOSUpdateStatusConstant(final String status, final boolean isDownloaded) {
        if (status == null) {
            OSUpdateConstants.DeviceStatus.INSTALLING.intValue();
        }
        int code;
        if (isDownloaded) {
            code = OSUpdateConstants.DeviceStatus.DOWNLOADED;
            if (status.equals("Installing")) {
                code = OSUpdateConstants.DeviceStatus.INSTALLING;
            }
        }
        else {
            code = OSUpdateConstants.DeviceStatus.DOWNLOADING;
            if (status.equals("Idle")) {
                code = OSUpdateConstants.DeviceStatus.AVAILABLE;
            }
        }
        return code;
    }
    
    @Override
    public JSONObject processSeqQueuedCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        try {
            final String responseData = params.optString("strData");
            final JSONObject cmdParams = params.optJSONObject("PARAMS");
            final JSONObject initialParams = cmdParams.optJSONObject("initialParams");
            this.collectionId = String.valueOf(initialParams.optLong("COLLECTION_ID"));
            final JSONObject responseObject = this.processResponse(resourceID, responseData, commandUUID);
            final int statusCode = responseObject.optInt("STATUS_CODE");
            final JSONObject seqParams = new JSONObject();
            final JSONObject response = new JSONObject();
            this.logger.log(Level.INFO, "OSUpdate status code:{0}", new Object[] { statusCode });
            seqParams.put("osDownloaded", false);
            final boolean isAllowedToSkipUpdate = this.isAllowedToSkipUpdate(Long.parseLong(this.collectionId));
            final JSONObject notificationHandler = PushNotificationHandler.getInstance().getNotificationDetails(resourceID, 1);
            final String unlockToken = notificationHandler.optString("UNLOCK_TOKEN_ENCRYPTED");
            final int clearPasscodePrivacy = (int)new PrivacySettingsHandler().getPrivacySettingsJSON(resourceID).get("disable_clear_passcode");
            if (isAllowedToSkipUpdate && InventoryUtil.getInstance().isPasscodeEnableForResource(resourceID)) {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, this.collectionId, 5, "mdm.osupdate.remarks.passcodeProtected");
                response.put("action", 2);
            }
            else if (!isAllowedToSkipUpdate && MDMStringUtils.isEmpty(unlockToken)) {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, this.collectionId, 5, "mdm.osupdate.remarks.passcodeClearFailed@@@<l>$(mdmUrl)/kb/mdm-ios-13-update-impacts.html");
                response.put("action", 2);
            }
            else if (!isAllowedToSkipUpdate && clearPasscodePrivacy == 2) {
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, this.collectionId, 5, "mdm.osupdate.remarks.clearPasscodePrivacy@@@<l>$(mdmUrl)/mobile-device-management/kb/mdm-ios-13-update-impacts.html");
                response.put("action", 2);
            }
            else if (statusCode == OSUpdateConstants.DeviceStatus.DOWNLOADED) {
                seqParams.put("osDownloaded", true);
                response.put("action", 1);
            }
            else if (statusCode == OSUpdateConstants.DeviceStatus.GENERAL_FAILED || statusCode == OSUpdateConstants.DeviceStatus.DOWNLOAD_FAILED || statusCode == OSUpdateConstants.DeviceStatus.INSTALL_FAILED) {
                this.logger.log(Level.INFO, "OSUpdate status failure. So suspending sequential command");
                response.put("action", 2);
            }
            else {
                response.put("action", 1);
            }
            response.put("commandUUID", (Object)commandUUID);
            response.put("resourceID", (Object)resourceID);
            response.put("params", (Object)seqParams);
            SeqCmdRepository.getInstance().processSeqCommand(response);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while processing osupdate status ", e);
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return null;
    }
    
    private boolean isAllowedToSkipUpdate(final Long collectionID) {
        boolean isAllowedToSkip = true;
        try {
            final JSONObject policyDetails = OSUpdatePolicyHandler.getInstance().getOSUpdatePolicyJSON(collectionID);
            if (policyDetails.optJSONObject("DeploymentNotifTemplate") != null) {
                final Object isObj = policyDetails.getJSONObject("DeploymentNotifTemplate").opt("ALLOW_USERS_TO_SKIP".toLowerCase());
                if (isObj != null) {
                    isAllowedToSkip = Boolean.parseBoolean(isObj.toString());
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error during isAllowedToSkipUpdate() ", e);
        }
        this.logger.log(Level.FINE, "is Allowed to Skip the Clear Passcode: {0}", isAllowedToSkip);
        return isAllowedToSkip;
    }
}
