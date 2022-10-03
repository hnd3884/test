package com.me.mdm.server.apps.blacklist.windows;

import java.util.Arrays;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.apps.blacklist.BlacklistQueryUtils;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.mdm.server.apps.blacklist.BaseBlacklistProcessor;

public class WindowsBlacklistProcessor extends BaseBlacklistProcessor
{
    public WindowsBlacklistProcessor() {
        this.platformType = 3;
    }
    
    @Override
    public Object processBlackListRequest(final HashMap params) throws Exception {
        final JSONObject jsonObject = (JSONObject)super.processBlackListRequest(params);
        final JSONArray appsList = (JSONArray)jsonObject.get("BlacklistApps");
        final List msiList = new ArrayList();
        final List appxList = new ArrayList();
        for (int i = 0; i < appsList.length(); ++i) {
            final JSONObject curApp = (JSONObject)appsList.get(i);
            String appIdentifier = (String)curApp.get("IDENTIFIER");
            final Boolean isModern = (Boolean)curApp.get("IS_MODERN_APP");
            final JSONObject blackListAppData = new JSONObject();
            if (appIdentifier != null) {
                appIdentifier = appIdentifier.split("_")[0];
            }
            blackListAppData.put("productName", (Object)appIdentifier);
            blackListAppData.put("binaryName", (Object)"*");
            blackListAppData.put("publisherName", (Object)"*");
            if (!isModern) {
                msiList.add(blackListAppData);
            }
            else {
                appxList.add(blackListAppData);
            }
        }
        final HashMap appxHashMap = new HashMap();
        final HashMap msiHashMap = new HashMap();
        final HashMap map = new HashMap();
        appxHashMap.put("enabled", appxList);
        map.put("appx", appxHashMap);
        msiHashMap.put("enabled", msiList);
        map.put("msi", appxHashMap);
        return map;
    }
    
    @Override
    public JSONObject processResponse(final Object param) throws Exception {
        final JSONObject jsonObject = (JSONObject)param;
        final Long resourceId = jsonObject.getLong("RESOURCE_ID");
        final Boolean success = (Boolean)jsonObject.get("success");
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
        else {
            blacklistJson.put("FailureList", appJson.get("BlacklistApps"));
            blacklistJson.put("SuccessList", (Object)new JSONArray());
            whiteListJson.put("FailureList", appJson.get("WhitelistApps"));
            whiteListJson.put("SuccessList", (Object)new JSONArray());
        }
        updateParam.put("BlacklistApps", (Object)blacklistJson);
        updateParam.put("WhitelistApps", (Object)whiteListJson);
        updateParam.put("RESOURCE_ID", (Object)resourceId);
        final JSONObject response = super.processResponse(updateParam);
        if (success) {
            final JSONArray blackListApps = appJson.getJSONArray("BlacklistApps");
            final List blackSuccessList = JSONUtil.convertJSONArrayToList(blackListApps);
            if (!blackSuccessList.isEmpty()) {
                this.logger.log(Level.INFO, "Apps that are to be deleted : {0} for resource id {1}", new Object[] { blackListApps.toString(), resourceId });
                BlacklistQueryUtils.getInstance().deleteInstalledAppFromIdentifier(resourceId, blackSuccessList);
            }
            final JSONArray whiteListApps = appJson.getJSONArray("WhitelistApps");
            if (whiteListApps.length() > 0) {
                final String osVersion = DBUtil.getValueFromDB("MdOSDetailsTemp", "RESOURCE_ID", (Object)resourceId, "OS_VERSION").toString();
                if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.0f)) {
                    DeviceCommandRepository.getInstance().addWindowsCommand(Arrays.asList(resourceId), "PreloadedAppsInfo");
                }
                DeviceCommandRepository.getInstance().addWindowsCommand(Arrays.asList(resourceId), "InstalledApplicationList");
            }
        }
        return response;
    }
}
