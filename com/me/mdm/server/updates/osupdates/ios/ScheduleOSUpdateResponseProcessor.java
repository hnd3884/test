package com.me.mdm.server.updates.osupdates.ios;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import java.util.Properties;
import com.dd.plist.NSArray;
import com.me.mdm.server.updates.osupdates.OSUpdateConstants;
import com.dd.plist.NSDictionary;
import com.me.mdm.server.updates.osupdates.ResourceOSUpdateDataHandler;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScheduleOSUpdateResponseProcessor
{
    private final Logger logger;
    private int deviceUpdateStatus;
    private boolean cmdError;
    public static String updateremark;
    String collectionId;
    
    public ScheduleOSUpdateResponseProcessor() {
        this.logger = Logger.getLogger("MDMLogger");
        this.deviceUpdateStatus = -1;
        this.cmdError = false;
        this.collectionId = null;
    }
    
    public void processResponse(final Long resourceID, final String responseStr, final String cmdUUID) {
        this.logger.log(Level.INFO, "ScheduleOSUpdateResProcessor: processResponse() non-seq.. ");
        this.collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(cmdUUID);
        this.processResponse(resourceID, responseStr);
    }
    
    public void processResponseForSeqCmd(final Long resourceID, final String responseStr, final String cmdUUID, final JSONObject cmdParams) {
        try {
            this.logger.log(Level.INFO, "ScheduleOSUpdateResProcessor: processResponseForSeqCmd().. ");
            this.collectionId = String.valueOf(cmdParams.getLong("COLLECTION_ID"));
            this.processResponse(resourceID, responseStr);
            final JSONObject resJson = new JSONObject();
            resJson.put("action", 1);
            resJson.put("resourceID", (Object)resourceID);
            resJson.put("commandUUID", (Object)cmdUUID);
            resJson.put("params", (Object)new JSONObject());
            SeqCmdRepository.getInstance().processSeqCommand(resJson);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error.....", e);
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, cmdUUID);
        }
    }
    
    public void processResponse(final Long resourceID, final String responseStr) {
        try {
            final String cmdStatus = PlistWrapper.getInstance().getValueForKeyString("Status", responseStr);
            if (cmdStatus.contains("Error")) {
                this.cmdError = true;
                if (this.processNotApplicableDevice(resourceID, this.collectionId)) {
                    return;
                }
                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, this.collectionId, 7, "mdm.osupdate.policy.failed");
            }
            else if (cmdStatus.equalsIgnoreCase("Acknowledged")) {
                int collnStatus = 6;
                String remarks = ScheduleOSUpdateResponseProcessor.updateremark;
                remarks = remarks.concat("None");
                String deviceUpdateRemarks = null;
                Long deviceUpdateStartAt = -1L;
                String productKey = "";
                boolean updateCollection = true;
                final ResourceOSUpdateDataHandler resourceOSUpdateDataHandler = new ResourceOSUpdateDataHandler();
                try {
                    final NSArray updateResultsArray = PlistWrapper.getInstance().getArrayForKey("UpdateResults", responseStr);
                    if (updateResultsArray != null && updateResultsArray.count() > 0) {
                        final NSDictionary updateResultDictionary = (NSDictionary)updateResultsArray.objectAtIndex(0);
                        final String installAction = updateResultDictionary.get((Object)"InstallAction").toString();
                        final String statusString = updateResultDictionary.get((Object)"Status").toString();
                        productKey = ((updateResultDictionary.get((Object)"ProductKey") == null) ? null : updateResultDictionary.get((Object)"ProductKey").toString());
                        if (installAction.equalsIgnoreCase("Error")) {
                            collnStatus = 16;
                            this.deviceUpdateStatus = OSUpdateConstants.DeviceStatus.GENERAL_FAILED;
                            final NSArray errorChain = (NSArray)updateResultDictionary.get((Object)"ErrorChain");
                            final NSDictionary errorDict = (NSDictionary)errorChain.lastObject();
                            remarks = ((errorDict.get((Object)"USEnglishDescription") == null) ? null : errorDict.get((Object)"USEnglishDescription").toString());
                            if (remarks != null) {
                                remarks = ((errorDict.get((Object)"LocalizedDescription") == null) ? null : errorDict.get((Object)"LocalizedDescription").toString());
                            }
                            if (remarks == null) {
                                remarks = statusString;
                            }
                            deviceUpdateRemarks = remarks;
                        }
                        else {
                            collnStatus = 6;
                            deviceUpdateRemarks = statusString;
                            this.deviceUpdateStatus = this.getOSUpdateStatusConstant(statusString);
                            deviceUpdateStartAt = resourceOSUpdateDataHandler.getResourceUpdateStartedAtTime(resourceID, productKey);
                            if (deviceUpdateStartAt != null && deviceUpdateStartAt == -1L) {
                                deviceUpdateStartAt = System.currentTimeMillis();
                            }
                            else if (statusString.equalsIgnoreCase("Downloading")) {
                                updateCollection = false;
                            }
                            if (statusString.equalsIgnoreCase("Downloading")) {
                                remarks = "mdm.db.osupdate.update_downloading";
                            }
                            else if (statusString.equalsIgnoreCase("Installing")) {
                                remarks = "mdm.db.osupdate.update_installing";
                            }
                            else {
                                remarks = ScheduleOSUpdateResponseProcessor.updateremark;
                                remarks = remarks.concat(statusString);
                            }
                        }
                    }
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, e, () -> "Exception occurred while processing Attempt OS update response: " + n);
                }
                if (updateCollection) {
                    MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, this.collectionId, collnStatus, remarks);
                }
                resourceOSUpdateDataHandler.updateResourceOSUpdateStatus(resourceID, this.deviceUpdateStatus, deviceUpdateRemarks, deviceUpdateStartAt, productKey, "", -1L);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception occurred while processing Attempt OS update response " + n2);
        }
    }
    
    public void processAttemptUpdateResponseForSeqCmd(final Long resourceID, final String responseStr, final String cmdUUID, final JSONObject cmdParams) {
        try {
            this.logger.log(Level.INFO, "ScheduleOSUpdateResProcessor: processAttemptUpdateResponseForSeqCmd().. {0}", new Object[] { resourceID });
            this.collectionId = String.valueOf(cmdParams.getLong("COLLECTION_ID"));
            this.processResponse(resourceID, responseStr);
            final JSONObject resJson = new JSONObject();
            final JSONObject seqParams = new JSONObject();
            if (this.cmdError) {
                resJson.put("action", 2);
                seqParams.put("forceRestrict", true);
            }
            else if (this.deviceUpdateStatus == OSUpdateConstants.DeviceStatus.INSTALLING) {
                this.scheduleCheckInstallStatus(resourceID, cmdUUID, cmdParams);
                resJson.put("action", 1);
            }
            else if (this.deviceUpdateStatus == OSUpdateConstants.DeviceStatus.DOWNLOADING) {
                this.scheduleCheckDownloadStatus(resourceID, cmdUUID, cmdParams);
                resJson.put("action", 1);
            }
            else {
                this.scheduleFailureRetries(resourceID, cmdUUID, cmdParams);
                resJson.put("action", 2);
                seqParams.put("forceRestrict", true);
            }
            resJson.put("resourceID", (Object)resourceID);
            resJson.put("commandUUID", (Object)cmdUUID);
            resJson.put("params", (Object)seqParams);
            SeqCmdRepository.getInstance().processSeqCommand(resJson);
        }
        catch (final Exception e) {
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, cmdUUID);
            this.logger.log(Level.SEVERE, "error.....", e);
        }
    }
    
    private void scheduleCheckInstallStatus(final Long resourceID, final String cmdUUID, final JSONObject cmdParams) {
        try {
            int retries = (cmdParams.opt("install_retries") == null) ? 0 : Integer.parseInt(cmdParams.opt("install_retries").toString());
            if (retries >= UpdateRetryHandler.getMaxInstallStatusRetries()) {
                return;
            }
            ++retries;
            cmdParams.put("install_retries", retries);
            this.scheduleRetry(resourceID, cmdParams, UpdateRetryHandler.getInstallStatusRetryPeriod());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error  ", e);
        }
    }
    
    private void scheduleCheckDownloadStatus(final Long resourceID, final String cmdUUID, final JSONObject cmdParams) {
        try {
            int retries = (cmdParams.opt("download_retries") == null) ? 0 : Integer.parseInt(cmdParams.opt("download_retries").toString());
            if (retries >= UpdateRetryHandler.getMaxDownloadStatusRetries()) {
                return;
            }
            ++retries;
            Long delayMillis = UpdateRetryHandler.getDownloadStatusRetryPeriod();
            if (retries > UpdateRetryHandler.getDownloadStatusExpBackoffRetryThreshold()) {
                delayMillis = 2L * delayMillis * (retries - UpdateRetryHandler.getDownloadStatusExpBackoffRetryThreshold());
            }
            cmdParams.put("download_retries", retries);
            this.scheduleRetry(resourceID, cmdParams, delayMillis);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error  ", e);
        }
    }
    
    private void scheduleFailureRetries(final Long resourceID, final String cmdUUID, final JSONObject cmdParams) {
        try {
            int retries = (cmdParams.opt("failure_retries") == null) ? 0 : Integer.parseInt(cmdParams.opt("failure_retries").toString());
            if (retries >= UpdateRetryHandler.getMaxFailureRetries()) {
                return;
            }
            ++retries;
            cmdParams.put("failure_retries", retries);
            this.scheduleRetry(resourceID, cmdParams, UpdateRetryHandler.getFailureRetryPeriod());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error  ", e);
        }
    }
    
    private void scheduleRetry(final Long resourceID, final JSONObject cmdParams, final Long delayMillis) {
        try {
            final Properties props = new Properties();
            ((Hashtable<String, String>)props).put("download_retries", cmdParams.optString("download_retries", "0"));
            ((Hashtable<String, String>)props).put("install_retries", cmdParams.optString("install_retries", "0"));
            ((Hashtable<String, String>)props).put("failure_retries", cmdParams.optString("failure_retries", "0"));
            ((Hashtable<String, String>)props).put("res", resourceID.toString());
            final ArrayList collectionIdsList = new ArrayList();
            collectionIdsList.add(this.collectionId);
            final Long cmdId = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionIdsList, "ScheduleOSUpdate").get(0);
            ((Hashtable<String, String>)props).put("cmd_id", cmdId.toString());
            this.logger.log(Level.INFO, "OSUpdate scheduleRetry().. scheduling next retry.. in delay: {0} details.. {1}", new Object[] { delayMillis, props.toString() });
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "IOSUpdateRetryTask");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis() + delayMillis);
            taskInfoMap.put("poolName", "mdmPool");
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.server.updates.osupdates.ios.UpdateRetryHandler", taskInfoMap, props);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error  ", e);
        }
    }
    
    private boolean processNotApplicableDevice(final Long resourceID, final String collectionId) throws Exception {
        if (ManagedDeviceHandler.getInstance().isSupervisedAndEqualOrAboveVersion(resourceID, "10.0")) {
            return false;
        }
        MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 8, "mdm.osupdate.remarks.notapplicableios");
        return true;
    }
    
    private int getOSUpdateStatusConstant(final String installResponse) {
        if (installResponse.equals("Downloading")) {
            return OSUpdateConstants.DeviceStatus.DOWNLOADING;
        }
        if (installResponse.equals("Installing") || installResponse.equals("Idle")) {
            return OSUpdateConstants.DeviceStatus.INSTALLING;
        }
        return OSUpdateConstants.DeviceStatus.GENERAL_FAILED;
    }
    
    static {
        ScheduleOSUpdateResponseProcessor.updateremark = "Current status on device: ";
    }
}
