package com.me.mdm.core.windows.commands;

import java.util.ArrayList;
import java.util.List;
import com.me.mdm.framework.syncml.responsecmds.ResultsResponseCommand;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;
import org.json.JSONException;
import java.util.logging.Level;
import org.apache.commons.lang3.StringUtils;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.Iterator;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WinDesktopInstalledAppListCommand extends WinMobileInstalledAppListCommand
{
    private Logger logger;
    
    public WinDesktopInstalledAppListCommand() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        super.processRequest(responseSyncML, jsonObject);
        final Boolean isWinMSIAppsEnabled = jsonObject.optBoolean("isWinMSIAppsEnabled", (boolean)Boolean.FALSE);
        if (isWinMSIAppsEnabled) {
            final SequenceRequestCommand sequenceRequestCommand = responseSyncML.getSyncBody().getRequestCmds().get(0);
            GetRequestCommand getRequestCommand = null;
            for (final SyncMLRequestCommand syncMLRequestCommand : sequenceRequestCommand.getRequestCmds()) {
                if (syncMLRequestCommand instanceof GetRequestCommand) {
                    getRequestCommand = (GetRequestCommand)syncMLRequestCommand;
                }
            }
            if (getRequestCommand != null) {
                final Item winMsiItem = this.createTargetItemTagElement("./Vendor/MSFT/Win32AppInventory/Win32InstalledProgram?list=StructData");
                getRequestCommand.addRequestItem(winMsiItem);
            }
        }
    }
    
    @Override
    public JSONObject processResponse(final SyncMLMessage requestSyncML) {
        final JSONObject appListJson = super.processResponse(requestSyncML);
        try {
            final JSONObject processedAppList = this.getWinMSIAppList(requestSyncML);
            final Iterator iter = processedAppList.keys();
            while (iter.hasNext()) {
                final String uniqueID = iter.next();
                final JSONObject installedProgramJSON = processedAppList.optJSONObject(uniqueID);
                String msiProductCode = installedProgramJSON.optString("MsiProductCode", "");
                msiProductCode = StringUtils.strip(msiProductCode, "{}");
                if (!msiProductCode.trim().equalsIgnoreCase("")) {
                    final JSONObject appDetailsJSON = new JSONObject();
                    appDetailsJSON.put("Identifier", (Object)msiProductCode);
                    appDetailsJSON.put("Name", (Object)String.valueOf(installedProgramJSON.get("Name")));
                    appDetailsJSON.put("Version", (Object)String.valueOf(installedProgramJSON.get("Version")));
                    appDetailsJSON.put("Publisher", installedProgramJSON.get("Publisher"));
                    appDetailsJSON.put("IsModernApp", (Object)Boolean.FALSE);
                    appListJson.put(msiProductCode, (Object)appDetailsJSON);
                }
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception while processing MSI JSON response", (Throwable)e);
        }
        return appListJson;
    }
    
    private JSONObject getWinMSIAppList(final SyncMLMessage requestSyncML) throws JSONException {
        final JSONObject msiJSON = new JSONObject();
        final List<SyncMLResponseCommand> responseCmds = requestSyncML.getSyncBody().getResponseCmds();
        for (final SyncMLResponseCommand responseCommand : responseCmds) {
            if (responseCommand instanceof ResultsResponseCommand) {
                final ArrayList<Item> itemList = responseCommand.getResponseItems();
                for (final Item item : itemList) {
                    final String locUri = item.getSource().getLocUri();
                    if (locUri.contains("/Win32InstalledProgram") && (locUri.contains("Name") || locUri.contains("Publisher") || locUri.contains("Version") || locUri.contains("MsiProductCode"))) {
                        final String uniqueID = locUri.substring(locUri.lastIndexOf("/Win32InstalledProgram/") + "/Win32InstalledProgram/".length(), locUri.lastIndexOf("/"));
                        final String propertyKey = locUri.substring(locUri.lastIndexOf("/") + 1);
                        if (uniqueID == null || propertyKey == null) {
                            continue;
                        }
                        final String data = item.getData().toString();
                        JSONObject installedProgramJSON = msiJSON.optJSONObject(uniqueID);
                        if (installedProgramJSON == null) {
                            installedProgramJSON = new JSONObject();
                        }
                        installedProgramJSON.put(propertyKey, (Object)data);
                        msiJSON.put(uniqueID, (Object)installedProgramJSON);
                    }
                }
            }
        }
        return msiJSON;
    }
}
