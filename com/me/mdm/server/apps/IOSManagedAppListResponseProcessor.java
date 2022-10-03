package com.me.mdm.server.apps;

import java.util.List;
import org.json.JSONArray;
import com.me.mdm.server.profiles.ios.IOSPerAppVPNHandler;
import java.util.Collections;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.api.MdmInvDataProcessor;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.me.mdm.server.seqcommands.SeqCmdDBUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import java.util.ArrayList;
import com.dd.plist.NSDictionary;
import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import java.util.logging.Level;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.adventnet.sym.server.mdm.apps.ManagedAppDataHandler;
import com.adventnet.sym.server.mdm.PlistWrapper;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSManagedAppListResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor, CommandResponseProcessor.ImmediateSeqResponseProcessor, CommandResponseProcessor.SeqQueuedResponseProcessor
{
    private static Logger logger;
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        final JSONObject seqResponse = new JSONObject();
        try {
            final JSONObject response = new JSONObject();
            final JSONObject seqParams = new JSONObject();
            final String responseData = params.optString("strData");
            final NSDictionary nsDict = PlistWrapper.getInstance().getDictForKey("ManagedApplicationList", responseData);
            final ManagedAppDataHandler appDataHandler = new ManagedAppDataHandler();
            final ArrayList appGroupList = appDataHandler.processIOSManagedAppDict(nsDict);
            if (!appGroupList.isEmpty()) {
                final JSONObject appResponse = appGroupList.get(0);
                final Integer status = appResponse.optInt("AppStatus");
                if (status == 1) {
                    final JSONObject paramObject = params.optJSONObject("currentSeqParams");
                    Integer retryCount = paramObject.optInt("retryCount");
                    final Long timeout = paramObject.optLong("timeout");
                    if (retryCount != null && timeout != null && retryCount != 0 && timeout != 0L) {
                        seqParams.put("timeout", 2L * timeout);
                        seqParams.put("retryCount", (Object)(--retryCount));
                    }
                    response.put("action", 4);
                }
                else if (status == 2) {
                    response.put("action", 1);
                }
                else if (status == 0) {
                    response.put("action", 2);
                    final String remarks = appResponse.optString("Remarks");
                    seqParams.put("Remarks", (Object)remarks);
                }
            }
            else {
                response.put("action", 2);
            }
            response.put("resourceID", (Object)resourceID);
            response.put("commandUUID", (Object)commandUUID);
            response.put("params", (Object)seqParams);
            response.put("isNotify", params.optBoolean("isNotify", (boolean)Boolean.FALSE));
            seqResponse.put("isNeedToAddQueue", true);
            SeqCmdRepository.getInstance().processSeqCommand(response);
        }
        catch (final Exception e) {
            IOSManagedAppListResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing immediate seq response for managed app list for resource:" + String.valueOf(n));
            IOSSeqCmdUtil.getInstance().removeSeqCommandForResource(resourceID, commandUUID);
        }
        return seqResponse;
    }
    
    @Override
    public JSONObject processSeqQueuedCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        try {
            this.processManagedAppsList(params);
            final JSONObject baseParams = SeqCmdUtils.getInstance().getBaseParamsForResource(resourceID);
            final SeqCmdDBUtil baseObject = new SeqCmdDBUtil();
            final JSONObject currentParams = baseObject.getParams(resourceID);
            params.put("baseSeqParams", (Object)baseParams);
            params.put("currentSeqParams", (Object)currentParams);
            params.put("isNotify", (Object)Boolean.TRUE);
        }
        catch (final Exception e) {
            IOSManagedAppListResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing seq response for managed app list for resource:" + String.valueOf(n));
        }
        return this.processImmediateSeqCommand(params);
    }
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        final JSONObject response = new JSONObject();
        final Long resourceID = params.optLong("resourceId");
        try {
            this.processManagedAppsList(params);
            new ManagedAppDataHandler().bringUnmanagedAppAsManaged(resourceID);
        }
        catch (final Exception e) {
            IOSManagedAppListResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing seq response for managed app list for resource:" + String.valueOf(n));
        }
        return response;
    }
    
    private void processManagedAppsList(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        try {
            final Long customerId = params.optLong("customerId");
            final String strCommandUuid = params.optString("strCommandUuid");
            final String responseData = params.optString("strData");
            final NSDictionary nsDict = PlistWrapper.getInstance().getDictForKey("ManagedApplicationList", responseData);
            final ManagedAppDataHandler managedAppsHandler = new ManagedAppDataHandler();
            boolean needToSync = true;
            if (strCommandUuid.contains("Collection")) {
                needToSync = false;
            }
            final JSONObject managedApps = new JSONObject();
            managedApps.put(ManagedAppDataHandler.resourceId, (Object)resourceID);
            managedApps.put(ManagedAppDataHandler.managedAppList, (Object)nsDict);
            managedApps.put(ManagedAppDataHandler.customerId, (Object)customerId);
            managedApps.put(ManagedAppDataHandler.needToSync, needToSync);
            final JSONArray appDetaislList = managedAppsHandler.processIOSManagedAppsList(managedApps);
            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatusForAppIds(appDetaislList, resourceID);
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowMultipleAppConfiguration")) {
                final List installedAppIDList = MdmInvDataProcessor.getInstance().getAppIDFromResourceID(resourceID);
                final PostAppInstallationListener postAppInstallationListener = new PostAppInstallationListener();
                final List installedAppGroupIDList = postAppInstallationListener.getAppGroupIDfromAppID(installedAppIDList);
                postAppInstallationListener.pushAppConfigurationProfile(installedAppGroupIDList, resourceID);
                NotificationHandler.getInstance().SendNotification(Collections.singletonList(resourceID));
            }
            new IOSPerAppVPNHandler().checkAndAddPerAppVpnConfigurationToResource(resourceID, customerId);
        }
        catch (final Exception ex) {
            IOSManagedAppListResponseProcessor.logger.log(Level.SEVERE, ex, () -> "Exception in processIOSManagedAppsList for managed app list for resource:" + String.valueOf(n));
        }
    }
    
    static {
        IOSManagedAppListResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
