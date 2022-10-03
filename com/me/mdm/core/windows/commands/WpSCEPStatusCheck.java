package com.me.mdm.core.windows.commands;

import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import com.me.mdm.framework.syncml.responsecmds.ResultsResponseCommand;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;
import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import org.json.JSONArray;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WpSCEPStatusCheck
{
    Logger logger;
    
    public WpSCEPStatusCheck() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        try {
            final JSONArray scepConfigNames = jsonObject.optJSONArray("scepConfigNames");
            final Boolean isWin10OrAbove = jsonObject.optBoolean("isWindows10OrAbove", (boolean)Boolean.FALSE);
            final String collectionId = jsonObject.optString("COLLECTION_ID", "");
            final GetRequestCommand getRequestCommand = new GetRequestCommand();
            getRequestCommand.setRequestCmdId("ScepStatusCheck;Collection=" + collectionId);
            String baseLocationUri = "./Vendor/MSFT/CertificateStore/My/SCEP/";
            if (isWin10OrAbove) {
                baseLocationUri = "./Vendor/MSFT/ClientCertificateInstall/SCEP/";
            }
            for (int index = 0; index < scepConfigNames.length(); ++index) {
                final String scepConfigName = String.valueOf(scepConfigNames.get(index));
                final String locationUri = baseLocationUri + scepConfigName + "/";
                getRequestCommand.addRequestItem(this.createCommandItemTagElement(locationUri + "Status", null));
                getRequestCommand.addRequestItem(this.createCommandItemTagElement(locationUri + "ErrorCode", null));
                getRequestCommand.addRequestItem(this.createCommandItemTagElement(locationUri + "CertThumbPrint", null));
            }
            responseSyncML.getSyncBody().addRequestCmd(getRequestCommand);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in WpSCEPStatusCheck.processRequestForDEPToken {0}", exp);
        }
    }
    
    private Item createCommandItemTagElement(final String sLocationURI, final String sValue) {
        final Item commandItem = new Item();
        commandItem.setTarget(new Location(sLocationURI));
        if (sValue != null) {
            commandItem.setData(sValue);
        }
        return commandItem;
    }
    
    public JSONObject processResponse(final SyncMLMessage requestSyncML) {
        final JSONObject responseJsonValues = new JSONObject();
        try {
            final List<SyncMLResponseCommand> responseCmds = requestSyncML.getSyncBody().getResponseCmds();
            for (final SyncMLResponseCommand responseCmd : responseCmds) {
                if (responseCmd instanceof ResultsResponseCommand) {
                    final List<Item> responseItems = responseCmd.getResponseItems();
                    for (final Item responseItem : responseItems) {
                        final String sourceUri = responseItem.getSource().getLocUri();
                        final String[] sourceUriSplit = sourceUri.split("/");
                        final String subJsonKey = sourceUriSplit[sourceUriSplit.length - 1];
                        final String scepConfigName = sourceUriSplit[sourceUriSplit.length - 2];
                        JSONObject subJsonObject = null;
                        if (responseJsonValues.has(scepConfigName)) {
                            subJsonObject = responseJsonValues.getJSONObject(scepConfigName);
                        }
                        else {
                            subJsonObject = new JSONObject();
                        }
                        subJsonObject.put(subJsonKey, (Object)responseItem.getData().toString());
                        responseJsonValues.put(scepConfigName, (Object)subJsonObject);
                    }
                }
            }
        }
        catch (final JSONException exp) {
            this.logger.log(Level.SEVERE, "Exception occured while parsing SCEP_STATUS_CHECK command response {0}", (Throwable)exp);
        }
        return responseJsonValues;
    }
}
