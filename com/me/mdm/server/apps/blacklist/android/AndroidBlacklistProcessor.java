package com.me.mdm.server.apps.blacklist.android;

import java.util.List;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.android.AndroidInventory;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.apps.blacklist.BlacklistQueryUtils;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.mdm.server.apps.blacklist.BaseBlacklistProcessor;

public class AndroidBlacklistProcessor extends BaseBlacklistProcessor
{
    public static final String BLACKLIST_ACTION = "Action";
    public static final String BLACKLIST_APPS = "BlacklistApps";
    public static final String WHITELIST_APPS = "WhitelistApps";
    public static final String APP_LIST = "AppList";
    
    public AndroidBlacklistProcessor() {
        this.platformType = 2;
    }
    
    @Override
    public Object processBlackListRequest(final HashMap params) throws Exception {
        final JSONObject blacklistWhitelistData = new JSONObject();
        final JSONObject jsonObject = (JSONObject)super.processBlackListRequest(params);
        final JSONObject blacklist = new JSONObject();
        final JSONArray blacklistArray = jsonObject.getJSONArray("BlacklistApps");
        final JSONArray blacklistIdentifierArray = new JSONArray();
        final JSONArray whitelistIdentifierArray = new JSONArray();
        for (int i = 0; i < blacklistArray.length(); ++i) {
            final JSONObject appJSON = blacklistArray.getJSONObject(i);
            final Integer status = (Integer)appJSON.get("STATUS");
            if (status == 1) {
                blacklistIdentifierArray.put(appJSON.get("IDENTIFIER"));
            }
        }
        final JSONObject whitelist = new JSONObject();
        final JSONArray whitelistArray = jsonObject.getJSONArray("WhitelistApps");
        for (int j = 0; j < whitelistArray.length(); ++j) {
            final JSONObject appJSON2 = whitelistArray.getJSONObject(j);
            final Integer status2 = (Integer)appJSON2.get("STATUS");
            if (status2 == 6) {
                whitelistIdentifierArray.put(appJSON2.get("IDENTIFIER"));
            }
        }
        blacklist.put("Action", 3);
        blacklist.put("AppList", (Object)blacklistIdentifierArray);
        whitelist.put("AppList", (Object)whitelistIdentifierArray);
        blacklistWhitelistData.put("BlacklistApps", (Object)blacklist);
        blacklistWhitelistData.put("WhitelistApps", (Object)whitelist);
        return blacklistWhitelistData;
    }
    
    @Override
    public JSONObject processResponse(final Object param) throws Exception {
        final JSONObject response = (JSONObject)param;
        final JSONObject status = response.getJSONObject("ResponseData");
        status.put("RESOURCE_ID", response.get("RESOURCE_ID"));
        if (ManagedDeviceHandler.getInstance().isProfileOwner((Long)response.get("RESOURCE_ID"))) {
            status.put("scope", 1);
        }
        else {
            status.put("scope", response.get("scope"));
        }
        final JSONObject jsonObject = super.processResponse(status);
        final Long resourceId = status.getLong("RESOURCE_ID");
        final JSONObject blackListApps = status.getJSONObject("BlacklistApps");
        final JSONObject whiteListApps = status.getJSONObject("WhitelistApps");
        final List blackSuccessList = JSONUtil.convertJSONArrayToList(blackListApps.getJSONArray("SuccessList"));
        final List whiteSuccessList = JSONUtil.convertJSONArrayToList(whiteListApps.getJSONArray("SuccessList"));
        if (!blackSuccessList.isEmpty()) {
            this.logger.log(Level.INFO, "Apps that are to be deleted : {0} for resource id {1}", new Object[] { blackListApps.toString(), resourceId });
            BlacklistQueryUtils.getInstance().deleteInstalledAppFromIdentifier(resourceId, blackSuccessList);
        }
        if (!whiteSuccessList.isEmpty()) {
            DeviceCommandRepository.getInstance().addDeviceScanCommand(new DeviceDetails(resourceId), null);
            AndroidInventory.getInstance().initSystemAppCommand(resourceId);
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            NotificationHandler.getInstance().SendNotification(resourceList, 2);
        }
        return jsonObject;
    }
}
