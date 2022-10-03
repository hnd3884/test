package com.adventnet.sym.server.mdm.apps.android;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashMap;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.apps.AppPermissionHandler;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.apps.android.apkextractor.ApkDetailsWrapper;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AdvancedAaptApkExtractorImpl extends AndroidAPKExtractor
{
    Logger logger;
    
    public AdvancedAaptApkExtractorImpl() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public JSONObject getAndroidAppsDetails(final String apkPath) throws JSONException {
        final ApkDetailsWrapper apkWrapper = new ApkDetailsWrapper();
        JSONObject apkDetailsJSON = null;
        try {
            apkDetailsJSON = apkWrapper.getApkDetails(apkPath);
            if (apkDetailsJSON.has("supported_screens")) {
                apkDetailsJSON.put("SUPPORTED_DEVICES", this.getSupportedScreenConstant(apkDetailsJSON.getJSONArray("supported_screens")));
            }
            if (apkDetailsJSON.has("permissions") && apkDetailsJSON.getJSONObject("permissions").has("dangerous")) {
                apkDetailsJSON.put("permissions_group", (Object)this.transformPermissionToPermissionGroup(apkDetailsJSON.getJSONObject("permissions").getJSONArray("dangerous")));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while fetching apk details", e);
        }
        return apkDetailsJSON;
    }
    
    private int getSupportedScreenConstant(final JSONArray screens) {
        final String screensStr = screens.toString();
        final boolean isTablet = screensStr.contains("Tablet");
        final boolean isPhone = screensStr.contains("Phone");
        return (isPhone && isTablet) ? 1 : (isPhone ? 2 : (isTablet ? 3 : 0));
    }
    
    private JSONObject transformPermissionToPermissionGroup(final JSONArray permissions) throws Exception {
        final JSONObject permissionGroup = new JSONObject();
        final HashMap permissionMap = new AppPermissionHandler().getPermissionNameToGroupMap();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppPermissionGroups"));
        selectQuery.addSelectColumn(Column.getColumn("AppPermissionGroups", "APP_PERMISSION_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppPermissionGroups", "APP_PERMISSION_GROUP_NAME"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            for (int i = 0; i < permissions.length(); ++i) {
                if (permissionMap.containsKey(String.valueOf(permissions.get(i)))) {
                    final Long permissionGrpId = permissionMap.get(String.valueOf(permissions.get(i)));
                    final Row row = dataObject.getRow("AppPermissionGroups", new Criteria(new Column("AppPermissionGroups", "APP_PERMISSION_GROUP_ID"), (Object)permissionGrpId, 0));
                    if (row != null) {
                        final String permissionGroupName = (String)row.get("APP_PERMISSION_GROUP_NAME");
                        JSONArray arr = permissionGroup.optJSONArray(permissionGroupName);
                        if (arr == null) {
                            arr = new JSONArray();
                        }
                        arr.put((Object)String.valueOf(permissions.get(i)));
                        permissionGroup.put(permissionGroupName, (Object)arr);
                    }
                }
            }
        }
        return permissionGroup;
    }
    
    @Override
    protected JSONObject getAPKProperties(final JSONObject requiredProperties) throws JSONException {
        return null;
    }
    
    @Override
    protected String getPropertyValue(final String propertyName) {
        return null;
    }
}
