package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.responsecmds.ResultsResponseCommand;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WinGetEnrollmentTypeQuery
{
    private Logger logger;
    
    public WinGetEnrollmentTypeQuery() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject cmdParams) {
        final GetRequestCommand getRequestCommand = new GetRequestCommand();
        getRequestCommand.setRequestCmdId("EnrollmentTypeQuery");
        final String baseURI = "./Vendor/MSFT/DMClient/Provider/MEMDM/EnrollmentType";
        getRequestCommand.addRequestItem(this.createCommandItemTagElement(baseURI, null));
        responseSyncML.getSyncBody().addRequestCmd(getRequestCommand);
        responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
    }
    
    public JSONObject processResponse(final SyncMLMessage requestSyncML) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final List<SyncMLResponseCommand> responseCmds = requestSyncML.getSyncBody().getResponseCmds();
            for (final SyncMLResponseCommand resultCommand : responseCmds) {
                if (resultCommand instanceof ResultsResponseCommand) {
                    final ArrayList itemList = resultCommand.getResponseItems();
                    for (final Object itemList2 : itemList) {
                        final Item item = (Item)itemList2;
                        final String locUri = item.getSource().getLocUri();
                        if (locUri.endsWith("EnrollmentType")) {
                            jsonObject.put("EnrollmentType", (Object)item.getData().toString());
                        }
                    }
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred during processing of parseResetPasscodeSyncML", exp);
        }
        return jsonObject;
    }
    
    private Item createCommandItemTagElement(final String sLocationURI, final String sValue) {
        final Item commandItem = new Item();
        commandItem.setTarget(new Location(sLocationURI));
        if (sValue != null) {
            commandItem.setData(sValue);
        }
        return commandItem;
    }
}
