package com.me.mdm.server.apps;

import com.me.mdm.server.acp.IOSAppCatalogHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.apps.handler.AppsAutoDeployment;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.List;
import com.me.mdm.server.ios.error.IOSErrorStatusHandler;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class MacOSInstallEnterpriseAppResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    private static Logger logger;
    
    private int getStatusID(final String status) {
        switch (status) {
            case "Acknowledged": {
                return 6;
            }
            default: {
                return 7;
            }
        }
    }
    
    private String getRemarks(final int status, final String strData) {
        try {
            if (status != 6) {
                final IOSErrorStatusHandler errorHandler = new IOSErrorStatusHandler();
                final JSONObject errorJSON = errorHandler.getIOSErrors(null, (List<Long>)null, strData);
                String remark = errorJSON.optString("EnglishRemarks");
                if (remark == null) {
                    remark = errorJSON.optString("LocalizedRemarks");
                }
                return remark;
            }
            return "dc.db.mdm.collection.Successfully_installed_the_app";
        }
        catch (final Exception e) {
            MacOSInstallEnterpriseAppResponseProcessor.logger.log(Level.SEVERE, "Error in getting remarks for install Enterprise Application", e);
            return null;
        }
    }
    
    private Long getCollectionIDFromCommandUUID(String commandUUID) {
        try {
            if (commandUUID.contains("agentID") || commandUUID.contains("InstallAgentID=")) {
                final String startString = "InstallAgentID=";
                final String configStartString = "Install_AgentConfig?agentID=";
                final Boolean isInstallApplication = commandUUID.contains("InstallAgentID=");
                commandUUID = commandUUID.replaceAll("InstallEnterpriseApplication;InstallAgentID=[0-9]*;Collection=", "");
                commandUUID = commandUUID.replace(configStartString, "");
                commandUUID = commandUUID.replace(startString, "");
                if (isInstallApplication) {
                    return Long.parseLong(commandUUID);
                }
                AppsAutoDeployment.getInstance().getCollectionIDFromAgentID(Integer.parseInt(commandUUID));
            }
            else {
                commandUUID = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
            }
            return Long.parseLong(commandUUID);
        }
        catch (final Exception e) {
            return -1L;
        }
    }
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        final String strData = params.optString("strData");
        final String strStatus = params.optString("strStatus");
        final Long customerID = JSONUtil.optLongForUVH(params, "customerId", Long.valueOf(-1L));
        final Long collectionID = this.getCollectionIDFromCommandUUID(commandUUID);
        final JSONObject seqResponse = new JSONObject();
        final int statusID = this.getStatusID(strStatus);
        try {
            final String remarks = this.getRemarks(statusID, strData);
            MDMUtil.getInstance().addOrUpdateCollnToResources(resourceID, collectionID, statusID, remarks);
            if (AppsAutoDeployment.getInstance().getAgentIDFromCollectionID(collectionID) != -1) {
                new IOSAppCatalogHandler().scheduleFetchAgentInstallStatus(resourceID, 180000L);
            }
        }
        catch (final Exception e) {
            MacOSInstallEnterpriseAppResponseProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing immediate seq response for managed app list for resource:" + String.valueOf(n));
        }
        return seqResponse;
    }
    
    static {
        MacOSInstallEnterpriseAppResponseProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
