package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Item;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.List;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WinDesktopAppPrivacyCommand extends WinMobileInstalledAppListCommand
{
    private Logger logger;
    
    public WinDesktopAppPrivacyCommand() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        super.processRequest(responseSyncML, jsonObject);
        try {
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
                    final List MSIList = (List)jsonObject.get("MSIList");
                    for (final String id : MSIList) {
                        final Item winMsiItem = this.createTargetItemTagElement("./Vendor/MSFT/Win32AppInventory/Win32InstalledProgram/" + id);
                        getRequestCommand.addRequestItem(winMsiItem);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in generating command", e);
        }
    }
    
    @Override
    public JSONObject processResponse(final SyncMLMessage requestSyncML) {
        return new WinDesktopInstalledAppListCommand().processResponse(requestSyncML);
    }
}
