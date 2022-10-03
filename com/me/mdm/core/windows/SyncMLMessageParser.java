package com.me.mdm.core.windows;

import com.me.mdm.framework.syncml.responsecmds.StatusResponseCommand;
import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;
import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.responsecmds.ResultsResponseCommand;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;
import com.me.mdm.framework.syncml.core.SyncHeaderMessage;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class SyncMLMessageParser
{
    Logger logger;
    private static SyncMLMessageParser syncMlParser;
    
    public SyncMLMessageParser() {
        this.logger = Logger.getLogger("SyncMLMessageParser");
    }
    
    public static SyncMLMessageParser getInstance() {
        if (SyncMLMessageParser.syncMlParser == null) {
            SyncMLMessageParser.syncMlParser = new SyncMLMessageParser();
        }
        return SyncMLMessageParser.syncMlParser;
    }
    
    public JSONObject parseSyncMLMessageHeader(final SyncMLMessage requestSyncML) {
        final JSONObject jsonObject = new JSONObject();
        try {
            String sDeviceUDID = null;
            final SyncHeaderMessage deviceSyncHeader = requestSyncML.getSyncHeader();
            final String deviceUDIDStr = deviceSyncHeader.getSource().getLocUri();
            if (deviceUDIDStr.contains("urn:uuid:")) {
                final String[] ret = deviceUDIDStr.split("urn:uuid:");
                sDeviceUDID = ret[1];
            }
            else {
                sDeviceUDID = deviceUDIDStr;
            }
            jsonObject.put("UDID", (Object)sDeviceUDID);
            final String sTarget = deviceSyncHeader.getTarget().getLocUri();
            jsonObject.put("TARGET", (Object)sTarget);
            final JSONObject tempJsonObject = new JSONObject();
            final int index = sTarget.indexOf(63);
            final String queryParameters = sTarget.substring(index + 1);
            String[] pairs = queryParameters.split("##");
            if (!queryParameters.contains("##")) {
                pairs = queryParameters.split("&");
            }
            for (final String pair : pairs) {
                final int idx = pair.indexOf("=");
                tempJsonObject.put(pair.substring(0, idx), (Object)pair.substring(idx + 1));
            }
            jsonObject.put("CUSTOMER_ID", (Object)String.valueOf(tempJsonObject.get("cid")));
            jsonObject.put("ENROLLMENT_REQUEST_ID", (Object)String.valueOf(tempJsonObject.get("erid")));
            jsonObject.put("MANAGED_USER_ID", (Object)String.valueOf(tempJsonObject.get("muid")));
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, " Exception in getWpManagedDeviceDetails ", exp);
        }
        return jsonObject;
    }
    
    public JSONObject parseInstalledApplicationList(final SyncMLMessage requestSyncML) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final List<SyncMLResponseCommand> responseCmds = requestSyncML.getSyncBody().getResponseCmds();
            for (int i = 0; i < responseCmds.size(); ++i) {
                final SyncMLResponseCommand resultCommand = responseCmds.get(i);
                if (resultCommand instanceof ResultsResponseCommand) {
                    final ArrayList itemList = resultCommand.getResponseItems();
                    for (int j = 0; j < itemList.size(); ++j) {
                        final Item item = itemList.get(j);
                        String productUri = item.getSource().getLocUri();
                        productUri = URLDecoder.decode(productUri, "utf-8");
                        if (productUri.indexOf(123) != -1 && productUri.indexOf(125) != -1) {
                            final String productID = productUri.substring(productUri.indexOf(123) + 1, productUri.indexOf(125));
                            if (item.getMeta() != null && item.getMeta().getFormat().equalsIgnoreCase("node")) {
                                final JSONObject details = new JSONObject();
                                details.put("Identifier", (Object)productID);
                                jsonObject.put(productID, (Object)details);
                            }
                            else {
                                final JSONObject details = jsonObject.getJSONObject(productID);
                                if (item.getSource().getLocUri().endsWith("Version")) {
                                    details.put("Version", (Object)item.getData().toString());
                                }
                                else if (item.getSource().getLocUri().endsWith("Title")) {
                                    details.put("Name", (Object)item.getData().toString());
                                }
                                else if (item.getSource().getLocUri().endsWith("InstallDate")) {
                                    details.put("InstallDate", (Object)item.getData().toString());
                                }
                                else if (item.getSource().getLocUri().endsWith("Publisher")) {
                                    details.put("Publisher", (Object)item.getData().toString());
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred during processing of parseInstalledApplicationList", exp);
        }
        return jsonObject;
    }
    
    public JSONArray parseSyncMLAlertRequest(final SyncMLMessage requestSyncML) {
        final JSONArray alerts = new JSONArray();
        try {
            final List requestCmds = requestSyncML.getSyncBody().getRequestCmds();
            for (int i = 0; i < requestCmds.size(); ++i) {
                final SyncMLRequestCommand reqCommand = requestCmds.get(i);
                final String syncMLCmdName = reqCommand.getSyncMLCommandName();
                final List requestItems = reqCommand.getRequestItems();
                if (syncMLCmdName.equalsIgnoreCase("Alert") && requestItems != null) {
                    for (int j = 0; j < requestItems.size(); ++j) {
                        final Item itemData = requestItems.get(j);
                        final Meta metaData = itemData.getMeta();
                        final String itemMetaType = metaData.getType();
                        final String itemDataValue = (String)itemData.getData();
                        final JSONObject jsonObject = new JSONObject();
                        jsonObject.put("AlertItemMetaType", (Object)itemMetaType);
                        jsonObject.put("AlertItemDataValue", (Object)itemDataValue);
                        alerts.put((Object)jsonObject);
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error while parsing request Alerts : ", ex);
        }
        return alerts;
    }
    
    public JSONObject parseResetPasscodeSyncML(final SyncMLMessage requestSyncML) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final List<SyncMLResponseCommand> responseCmds = requestSyncML.getSyncBody().getResponseCmds();
            for (final SyncMLResponseCommand resultCommand : responseCmds) {
                if (resultCommand instanceof ResultsResponseCommand) {
                    final ArrayList itemList = resultCommand.getResponseItems();
                    for (final Object itemList2 : itemList) {
                        final Item item = (Item)itemList2;
                        if (item.getSource().getLocUri().endsWith("NewPINValue")) {
                            jsonObject.put("PASSCODE", (Object)item.getData().toString());
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
    
    public JSONObject parseCommandStatusMessage(final SyncMLMessage requestSyncML) {
        final JSONObject jsonObject = new JSONObject();
        final JSONObject allStatusMap = new JSONObject();
        try {
            jsonObject.put("statusMap", (Object)allStatusMap);
            final List<SyncMLResponseCommand> responseCmds = requestSyncML.getSyncBody().getResponseCmds();
            for (int i = 0; i < responseCmds.size(); ++i) {
                final SyncMLResponseCommand statusCommand = responseCmds.get(i);
                String statusKey = statusCommand.getTargetRef();
                if (statusKey == null || statusKey.trim().equals("")) {
                    statusKey = statusCommand.getCmdRef() + ";" + statusCommand.getCmd();
                }
                else {
                    final int index = StringUtils.lastOrdinalIndexOf((CharSequence)statusKey, (CharSequence)"/", 2);
                    statusKey = statusKey.substring(index + 1);
                }
                if (statusCommand instanceof StatusResponseCommand && statusCommand.getCmd().equalsIgnoreCase("Sequence")) {
                    jsonObject.put("CommandUUID", (Object)statusCommand.getCmdRef());
                }
                if (statusCommand instanceof StatusResponseCommand && statusCommand.getCmd().equalsIgnoreCase("Atomic")) {
                    jsonObject.put("CommandUUID", (Object)statusCommand.getCmdRef());
                    jsonObject.put("Status", statusCommand.getData());
                    jsonObject.getJSONObject("statusMap").put(statusKey, statusCommand.getData());
                }
                else if (statusCommand instanceof StatusResponseCommand && statusCommand.getCmd().equalsIgnoreCase("Get")) {
                    jsonObject.put("CommandUUID", (Object)statusCommand.getCmdRef());
                    jsonObject.put("Status", statusCommand.getData());
                    jsonObject.getJSONObject("statusMap").put(statusKey, statusCommand.getData());
                }
                else if (statusCommand instanceof ResultsResponseCommand) {
                    final String commandReference = statusCommand.getCmdRef();
                    if (commandReference != null) {
                        jsonObject.put("CommandUUID", (Object)statusCommand.getCmdRef());
                    }
                }
                else if (statusCommand instanceof StatusResponseCommand) {
                    final String errorCodeStr = (String)statusCommand.getData();
                    if (errorCodeStr != null) {
                        final int errorCode = Integer.parseInt(errorCodeStr.trim());
                        if (errorCode != 200 && errorCode != 216) {
                            String errorTarget = statusCommand.getTargetRef();
                            if (errorTarget != null) {
                                final int index2 = errorTarget.lastIndexOf(47);
                                if (index2 != -1) {
                                    errorTarget = errorTarget.substring(index2 + 1);
                                    jsonObject.put("ErrorCode", errorCode);
                                    jsonObject.put("Remarks", (Object)(errorCode + " : " + errorTarget));
                                }
                            }
                            else {
                                jsonObject.put("ErrorCode", errorCode);
                                jsonObject.put("Remarks", (Object)(errorCode + " : " + statusCommand.getCmd()));
                            }
                        }
                        jsonObject.getJSONObject("statusMap").put(statusKey, statusCommand.getData());
                    }
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred during processing of parseCommandStatusMessage", exp);
        }
        return jsonObject;
    }
    
    public JSONObject parseRemoteLocationSyncML(final SyncMLMessage requestSyncML) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final List<SyncMLResponseCommand> responseCmds = requestSyncML.getSyncBody().getResponseCmds();
            for (final SyncMLResponseCommand resultCommand : responseCmds) {
                if (resultCommand instanceof ResultsResponseCommand) {
                    final ArrayList itemList = resultCommand.getResponseItems();
                    for (final Object itemList2 : itemList) {
                        final Item item = (Item)itemList2;
                        final String locUri = item.getSource().getLocUri();
                        if (locUri.endsWith("Latitude")) {
                            jsonObject.put("Latitude", (Object)item.getData().toString());
                        }
                        else if (locUri.endsWith("Longitude")) {
                            jsonObject.put("Longitude", (Object)item.getData().toString());
                        }
                        else if (locUri.endsWith("Altitude")) {
                            jsonObject.put("ALTITUDE", (Object)item.getData().toString());
                        }
                        else if (locUri.endsWith("AltitudeAccuracy")) {
                            jsonObject.put("ALTITUDE_ACCURACY", (Object)item.getData().toString());
                        }
                        else if (locUri.endsWith("Accuracy")) {
                            jsonObject.put("LOCATION_ACCURACY", (Object)item.getData().toString());
                        }
                        else {
                            if (!locUri.endsWith("Age")) {
                                continue;
                            }
                            jsonObject.put("AGE", (Object)item.getData().toString());
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
    
    static {
        SyncMLMessageParser.syncMlParser = null;
    }
}
