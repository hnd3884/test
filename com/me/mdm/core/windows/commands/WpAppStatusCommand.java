package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Location;
import org.json.JSONException;
import java.util.Iterator;
import com.me.mdm.framework.syncml.responsecmds.ResultsResponseCommand;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.List;
import java.util.ArrayList;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WpAppStatusCommand
{
    public static WpAppStatusCommand wpAppStatusCommand;
    Logger logger;
    
    public WpAppStatusCommand() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject cmdParams) {
        final Boolean isMSI = cmdParams.optBoolean("isMSI", (boolean)Boolean.FALSE);
        if (!isMSI) {
            this.processModernAppRequest(responseSyncML, cmdParams);
        }
        else {
            this.processMSIAppRequest(responseSyncML, cmdParams);
        }
    }
    
    private void processModernAppRequest(final SyncMLMessage responseSyncML, final JSONObject cmdParams) {
        try {
            final String packageFamilyName = String.valueOf(cmdParams.get("PackageFamilyName"));
            final Long resID = cmdParams.getLong("ResourceID");
            final boolean isDesktop = cmdParams.getBoolean("IsDesktop");
            final GetRequestCommand getappStatus = new GetRequestCommand();
            getappStatus.setRequestCmdId("WinAppInstallStatusQuery");
            String uri = "/Vendor/MSFT/EnterpriseModernAppManagement/AppInstallation/" + packageFamilyName + "?list=StructData";
            if (!isDesktop) {
                uri = "./User" + uri;
            }
            else {
                uri = "." + uri;
            }
            final Item item = this.createTargetItemTagElement(uri);
            final ArrayList items = new ArrayList();
            items.add(item);
            getappStatus.setRequestItems(items);
            responseSyncML.getSyncBody().addRequestCmd(getappStatus);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in WpStatusCommand : {0}", exp);
        }
    }
    
    private void processMSIAppRequest(final SyncMLMessage responseSyncML, final JSONObject cmdParams) {
        try {
            final String packageFamilyName = String.valueOf(cmdParams.get("PackageFamilyName"));
            final GetRequestCommand getappStatus = new GetRequestCommand();
            getappStatus.setRequestCmdId("WinAppInstallStatusQuery");
            final String uri = "./Device/Vendor/MSFT/EnterpriseDesktopAppManagement/MSI/%7B" + packageFamilyName + "%7D/";
            final ArrayList items = new ArrayList();
            Item item = this.createTargetItemTagElement(uri + "Status");
            items.add(item);
            item = this.createTargetItemTagElement(uri + "LastError");
            items.add(item);
            item = this.createTargetItemTagElement(uri + "LastErrorDesc");
            items.add(item);
            getappStatus.setRequestItems(items);
            responseSyncML.getSyncBody().addRequestCmd(getappStatus);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in WpStatusCommand : {0}", exp);
        }
    }
    
    public JSONObject processResponse(final SyncMLMessage requestSyncML) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        final List<SyncMLResponseCommand> responseCmds = requestSyncML.getSyncBody().getResponseCmds();
        for (final SyncMLResponseCommand responseCmd : responseCmds) {
            if (responseCmd instanceof ResultsResponseCommand) {
                final List<Item> responseItems = responseCmd.getResponseItems();
                for (final Item responseItem : responseItems) {
                    final String sourceUri = responseItem.getSource().getLocUri();
                    if (sourceUri.toLowerCase().contains("/status")) {
                        jsonObject.put("status", Integer.parseInt(responseItem.getData().toString()));
                    }
                    else if (sourceUri.toLowerCase().contains("/lasterrordesc")) {
                        jsonObject.put("lastErrorDescription", (Object)responseItem.getData().toString());
                    }
                    else {
                        if (!sourceUri.toLowerCase().contains("/lasterror")) {
                            continue;
                        }
                        jsonObject.put("lastError", (Object)responseItem.getData().toString());
                    }
                }
            }
        }
        return jsonObject;
    }
    
    Item createTargetItemTagElement(final String locationUri) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        return item;
    }
    
    static {
        WpAppStatusCommand.wpAppStatusCommand = null;
    }
}
