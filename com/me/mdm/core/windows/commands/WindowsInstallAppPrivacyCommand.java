package com.me.mdm.core.windows.commands;

import java.util.Iterator;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import java.util.List;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WindowsInstallAppPrivacyCommand extends WinMobileInstalledAppListCommand
{
    private Logger logger;
    
    public WindowsInstallAppPrivacyCommand() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        try {
            final SequenceRequestCommand sequenceRequestCommand = new SequenceRequestCommand();
            sequenceRequestCommand.setRequestCmdId(String.valueOf(jsonObject.get("COMMAND_UUID")));
            final List storeList = (List)jsonObject.get("storeApps");
            final List nonStoreList = (List)jsonObject.get("nonStoreApps");
            this.addAppInventoryQueryCommands(storeList, jsonObject, sequenceRequestCommand);
            if (String.valueOf(jsonObject.get("COMMAND_UUID")).equalsIgnoreCase("InstalledApplicationList")) {
                final JSONObject nonStoreAppsJson = new JSONObject();
                nonStoreAppsJson.put("COMMAND_UUID", (Object)String.valueOf(jsonObject.get("COMMAND_UUID")));
                nonStoreAppsJson.put("APP_INSTALLED_SOURCE", (Object)"nonStore");
                this.addAppInventoryQueryCommands(nonStoreList, nonStoreAppsJson, sequenceRequestCommand);
            }
            responseSyncML.getSyncBody().addRequestCmd(sequenceRequestCommand);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in creating Windows 10 App Inventory query", exp);
        }
    }
    
    private SequenceRequestCommand addAppInventoryQueryCommands(final List applist, final JSONObject jsonObject, final SequenceRequestCommand sequenceRequestCommand) throws Exception {
        for (final String pfn : applist) {
            jsonObject.put("PACKAGE_FAMILY_NAME_FILTER", (Object)pfn);
            final ReplaceRequestCommand appsQueryFilterReplaceRequestCommand = new ReplaceRequestCommand();
            appsQueryFilterReplaceRequestCommand.setRequestCmdId(String.valueOf(jsonObject.get("COMMAND_UUID")));
            final String appsQueryString = this.getInventoryQueryString(jsonObject);
            appsQueryFilterReplaceRequestCommand.addRequestItem(this.createCommandItemTagElement(this.baseLocationURI + "AppInventoryQuery", appsQueryString, "xml"));
            final GetRequestCommand appsQueryGetCommand = new GetRequestCommand();
            appsQueryGetCommand.setRequestCmdId(String.valueOf(jsonObject.get("COMMAND_UUID")));
            appsQueryGetCommand.addRequestItem(this.createCommandItemTagElement(this.baseLocationURI + "AppInventoryResults", null));
            sequenceRequestCommand.addRequestCmd(appsQueryFilterReplaceRequestCommand);
            sequenceRequestCommand.addRequestCmd(appsQueryGetCommand);
        }
        return sequenceRequestCommand;
    }
}
