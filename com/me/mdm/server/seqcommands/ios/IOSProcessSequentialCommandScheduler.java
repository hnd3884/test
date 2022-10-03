package com.me.mdm.server.seqcommands.ios;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class IOSProcessSequentialCommandScheduler implements SchedulerExecutionInterface
{
    private static Logger logger;
    
    public void executeTask(final Properties properties) {
        Long resourceID = null;
        String commandUUID = null;
        try {
            final JSONObject params = new JSONObject((String)((Hashtable<K, String>)properties).get("params"));
            resourceID = params.optLong("resourceID");
            commandUUID = params.optString("commandUUID");
            IOSProcessSequentialCommandScheduler.logger.log(Level.WARNING, "IOS Seq command task commandUUID : {0}", commandUUID);
            if (commandUUID.contains("ManagedApplicationList")) {
                this.processManagedAppListCommand(params);
            }
            else if (commandUUID.contains("InstallApplication")) {
                this.processInstallAppCommand(params);
            }
            else if (commandUUID.contains("InstalledApplicationList")) {
                this.processInstallAppCommand(params);
            }
            else if (commandUUID.contains("SingleWebAppKioskFeedback")) {
                this.processWebAppKioskFeedback(params);
            }
        }
        catch (final Exception e) {
            IOSProcessSequentialCommandScheduler.logger.log(Level.SEVERE, "Exception in assign task sequential command is terminated", e);
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
    }
    
    private void processManagedAppListCommand(final JSONObject params) throws Exception {
        final Integer retryCount = params.optJSONObject("CurCmdParam").optInt("retryCount");
        final Long timeOffset = params.optJSONObject("CurCmdParam").optLong("timeout");
        final Long resourceID = params.optLong("resourceID");
        final String commandUUID = params.optString("commandUUID");
        final JSONObject initialParams = params.optJSONObject("initialParams");
        final Long collectionID = initialParams.optLong(IOSSeqCmdUtil.appCollection);
        final String installType = params.optJSONObject("CurCmdParam").optString("installType");
        final JSONObject responseJSON = new JSONObject();
        final JSONObject paramsHandler = new JSONObject();
        if (IOSSeqCmdUtil.getInstance().isCollectionInstallSuccessForResource(collectionID, resourceID)) {
            responseJSON.put("action", 1);
        }
        else if (retryCount >= 1) {
            responseJSON.put("action", 3);
            paramsHandler.put("retryCount", (Object)retryCount);
            paramsHandler.put("timeout", (Object)timeOffset);
        }
        else if (installType != null && installType.equalsIgnoreCase("kioskAppUpdate")) {
            responseJSON.put("action", 1);
            paramsHandler.put("retryCount", (Object)retryCount);
            final Long collectionId = SeqCmdUtils.getInstance().getBaseCollectionIDForResource(resourceID);
            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, String.valueOf(collectionId), 7, "mdm.apps.ios.kiosk.updateAppFailure");
        }
        else {
            responseJSON.put("action", 2);
            paramsHandler.put("retryCount", (Object)retryCount);
        }
        responseJSON.put("commandUUID", (Object)commandUUID);
        responseJSON.put("resourceID", (Object)resourceID);
        responseJSON.put("params", (Object)paramsHandler);
        SeqCmdRepository.getInstance().processSeqCommand(responseJSON);
    }
    
    private void processInstallAppCommand(final JSONObject params) throws Exception {
        final Long resourceID = params.optLong("resourceID");
        final String commandUUID = params.optString("commandUUID");
        final Integer retryCount = params.optJSONObject("CurCmdParam").optInt("retryCount");
        final Long timeOffset = params.optJSONObject("CurCmdParam").optLong("timeout");
        final JSONObject responseJSON = new JSONObject();
        final JSONObject paramsHandler = new JSONObject();
        paramsHandler.put("retryCount", (Object)retryCount);
        paramsHandler.put("timeout", (Object)timeOffset);
        responseJSON.put("action", 1);
        responseJSON.put("commandUUID", (Object)commandUUID);
        responseJSON.put("resourceID", (Object)resourceID);
        responseJSON.put("params", (Object)paramsHandler);
        SeqCmdRepository.getInstance().processSeqCommand(responseJSON);
    }
    
    private void processWebAppKioskFeedback(final JSONObject params) throws Exception {
        final Long resourceID = params.optLong("resourceID");
        final String commandUUID = params.optString("commandUUID");
        final Integer retryCount = params.optJSONObject("CurCmdParam").optInt("retryCount");
        final JSONObject responseJSON = new JSONObject();
        final JSONObject paramsHandler = new JSONObject();
        final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
        if (IOSSeqCmdUtil.getInstance().isCollectionInstallSuccessForResource(Long.valueOf(collectionId), resourceID)) {
            responseJSON.put("action", 1);
        }
        else if (retryCount >= 1) {
            responseJSON.put("action", 3);
            paramsHandler.put("retryCount", (Object)retryCount);
            paramsHandler.put("timeout", 30000);
        }
        else {
            responseJSON.put("action", 2);
            paramsHandler.put("retryCount", (Object)retryCount);
        }
        responseJSON.put("commandUUID", (Object)commandUUID);
        responseJSON.put("resourceID", (Object)resourceID);
        responseJSON.put("params", (Object)paramsHandler);
        SeqCmdRepository.getInstance().processSeqCommand(responseJSON);
    }
    
    static {
        IOSProcessSequentialCommandScheduler.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
}
