package com.me.mdm.server.apps.blacklist.ios;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import java.util.List;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.dd.plist.NSArray;
import com.adventnet.sym.server.mdm.ios.payload.ConfigurationPayload;
import com.adventnet.sym.server.mdm.ios.payload.RestrictionsPayload;
import java.util.Collection;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.mdm.server.apps.blacklist.BaseBlacklistProcessor;

public class IOSBlacklistAppProcessor extends BaseBlacklistProcessor
{
    public IOSBlacklistAppProcessor() {
        this.platformType = 1;
    }
    
    @Override
    public Object processBlackListRequest(final HashMap params) throws Exception {
        final JSONObject jsonObject = (JSONObject)super.processBlackListRequest(params);
        String strQuery = null;
        final JSONArray appsList = (JSONArray)jsonObject.get("BlacklistApps");
        final List blacklist = new ArrayList();
        for (int i = 0; i < appsList.length(); ++i) {
            blacklist.add(appsList.getJSONObject(i).get("IDENTIFIER"));
        }
        final List nonBlasklistableItems = new ArrayList();
        nonBlasklistableItems.add("com.apple.TVSettings");
        nonBlasklistableItems.add("com.apple.Settings");
        blacklist.removeAll(nonBlasklistableItems);
        final boolean isAdd = blacklist.size() > 0;
        final String collectionName = "MDMBlacklistApps";
        final String payloadIdentifier = "com.mdm." + collectionName;
        final RestrictionsPayload blacklistPayload = new RestrictionsPayload(1, "MDM", "com.mdm.mobiledevice.blacklistedApp", "Blacklist Apps Policy");
        blacklistPayload.setBlacklistedAppBundleIDs(blacklist);
        final ConfigurationPayload cfgPayload = new ConfigurationPayload(1, "MDM", payloadIdentifier, collectionName);
        final NSArray nsarray = new NSArray(1);
        nsarray.setValue(0, (Object)blacklistPayload.getPayloadDict());
        cfgPayload.setPayloadRemovalDisallowed(2);
        cfgPayload.setPayloadContent(nsarray);
        final String payloadDictXml = cfgPayload.getPayloadDict().toXMLPropertyList();
        final String commandRequestType = isAdd ? "InstallProfile" : "RemoveProfile";
        final String commandType = (params.get("COMMAND_TYPE") != null) ? params.get("COMMAND_TYPE") : "BlacklistAppInDevice";
        final IOSCommandPayload commandPayload = PayloadHandler.getInstance().createCommandPayload(commandRequestType);
        commandPayload.setCommandUUID(commandType, false);
        if (!isAdd) {
            commandPayload.getCommandDict().put("Identifier", (Object)payloadIdentifier);
        }
        commandPayload.setPayload(payloadDictXml.getBytes());
        strQuery = commandPayload.getPayloadDict().toXMLPropertyList();
        return strQuery;
    }
    
    @Override
    public JSONObject processResponse(final Object param) throws Exception {
        final JSONObject jsonObject = (JSONObject)param;
        final Long resourceId = jsonObject.getLong("RESOURCE_ID");
        final Boolean success = (Boolean)jsonObject.get("success");
        final Boolean profileNotAvailable = jsonObject.optBoolean("profileNotAvailable", false);
        final JSONObject appJson = this.getInProgressStatusForResource(resourceId);
        final JSONObject updateParam = new JSONObject();
        final JSONObject blacklistJson = new JSONObject();
        final JSONObject whiteListJson = new JSONObject();
        if (success) {
            blacklistJson.put("SuccessList", appJson.get("BlacklistApps"));
            blacklistJson.put("FailureList", (Object)new JSONArray());
            whiteListJson.put("SuccessList", appJson.get("WhitelistApps"));
            whiteListJson.put("FailureList", (Object)new JSONArray());
        }
        else if (profileNotAvailable) {
            whiteListJson.put("SuccessList", appJson.get("WhitelistApps"));
            whiteListJson.put("FailureList", (Object)new JSONArray());
            blacklistJson.put("FailureList", appJson.get("BlacklistApps"));
            blacklistJson.put("SuccessList", (Object)new JSONArray());
        }
        else {
            blacklistJson.put("FailureList", appJson.get("BlacklistApps"));
            blacklistJson.put("SuccessList", (Object)new JSONArray());
            whiteListJson.put("FailureList", appJson.get("WhitelistApps"));
            whiteListJson.put("SuccessList", (Object)new JSONArray());
        }
        updateParam.put("BlacklistApps", (Object)blacklistJson);
        updateParam.put("WhitelistApps", (Object)whiteListJson);
        updateParam.put("RESOURCE_ID", (Object)resourceId);
        return super.processResponse(updateParam);
    }
}
