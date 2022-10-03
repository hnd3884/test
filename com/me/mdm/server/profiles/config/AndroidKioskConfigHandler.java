package com.me.mdm.server.profiles.config;

import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Iterator;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;
import java.util.logging.Level;
import org.json.JSONObject;

public class AndroidKioskConfigHandler extends DefaultKioskConfigHandler
{
    @Override
    public JSONObject apiJSONToServerJSON(final String configName, final JSONObject apiJSON) throws APIHTTPException {
        this.logger.log(Level.INFO, "Started converting API JSON to Server JSON...");
        try {
            final JSONObject result = super.apiJSONToServerJSON(configName, apiJSON);
            final List<String> appIdentifiers = new ArrayList<String>();
            final JSONArray kioskApps = result.getJSONArray("ALLOWED_APPS");
            for (int index = 0; index < kioskApps.length(); ++index) {
                final JSONObject kioskApp = kioskApps.getJSONObject(index);
                if (kioskApp.has("IDENTIFIER") && !kioskApp.has("APP_GROUP_ID")) {
                    appIdentifiers.add(kioskApp.getString("IDENTIFIER"));
                }
            }
            final DataObject dataObject = MDMUtil.getPersistence().get("MdAppGroupDetails", new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)appIdentifiers.toArray(), 8));
            if (!appIdentifiers.isEmpty() && dataObject.isEmpty()) {
                throw new APIHTTPException("COM0015", new Object[] { "No App Found for he identifier : " + appIdentifiers.toString() });
            }
            for (int index2 = 0; index2 < kioskApps.length() && !dataObject.isEmpty(); ++index2) {
                final JSONObject kioskApp2 = kioskApps.getJSONObject(index2);
                if (kioskApp2.has("IDENTIFIER")) {
                    final Long appGroupId = (Long)dataObject.getValue("MdAppGroupDetails", "APP_GROUP_ID", new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)kioskApp2.getString("IDENTIFIER"), 0));
                    if (appGroupId < 0L) {
                        throw new APIHTTPException("COM0015", new Object[] { "No App Found for he identifier : " + kioskApp2.get("IDENTIFIER") });
                    }
                    kioskApp2.put("APP_GROUP_ID", (Object)appGroupId);
                }
            }
            final int kioskMode = result.getInt("KIOSK_MODE");
            if (kioskMode == 0) {
                result.put("LAUNCHER_TYPE", 2);
            }
            return result;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in apiJSONToServerJSON", e);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
    }
    
    @Override
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        JSONArray result = null;
        try {
            if (!dataObject.isEmpty()) {
                result = super.DOToAPIJSON(dataObject, configName, tableName);
                final JSONArray templateConfigProperties = ProfileConfigurationUtil.getInstance().getPayloadConfigurationProperties(configName);
                Iterator iterator = dataObject.getRows("MdAppGroupDetails");
                final List<Long> appGroupIDList = new ArrayList<Long>();
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    appGroupIDList.add((Long)row.get("APP_GROUP_ID"));
                }
                final Iterator<Row> backGroundAppsRows = dataObject.getRows("MDBACKGROUNDAPPGROUPDETAILS");
                final List<Long> backgroundAppGroupIDList = new ArrayList<Long>();
                while (backGroundAppsRows.hasNext()) {
                    final Row row2 = backGroundAppsRows.next();
                    backgroundAppGroupIDList.add((Long)row2.get("APP_GROUP_ID"));
                }
                final JSONObject configObject = result.getJSONObject(0);
                final Long defaultAppGroupID = configObject.optLong("default_kiosk_app");
                final JSONObject appDetailsJsonObj = new MDMAppMgmtHandler().getAppInformation(appGroupIDList);
                final JSONArray appDetailsJsonArray = new JSONArray();
                final JSONArray defaultAppJsonArray = new JSONArray();
                iterator = appDetailsJsonObj.keys();
                while (iterator.hasNext()) {
                    final String key = iterator.next();
                    final JSONObject appDetails = appDetailsJsonObj.getJSONObject(key);
                    final JSONObject appDetailsLowerCase = new JSONObject();
                    final Iterator appDetailItr = appDetails.keys();
                    while (appDetailItr.hasNext()) {
                        final String appDetailsKey = appDetailItr.next();
                        if (appDetailsKey.equals("APP_GROUP_ID")) {
                            appDetailsLowerCase.put("app_id", appDetails.get(appDetailsKey));
                        }
                        else if (appDetailsKey.equals("DISPLAY_IMAGE_LOC")) {
                            final Object value = appDetails.get(appDetailsKey);
                            if (MDMStringUtils.isEmpty(String.valueOf(value))) {
                                continue;
                            }
                            appDetailsLowerCase.put(appDetailsKey.toLowerCase(), this.constructFileUrl(value));
                        }
                        else {
                            appDetailsLowerCase.put(appDetailsKey.toLowerCase(), appDetails.get(appDetailsKey));
                        }
                    }
                    if (appDetailsLowerCase.get("app_id").equals(defaultAppGroupID)) {
                        defaultAppJsonArray.put((Object)appDetailsLowerCase);
                    }
                    appDetailsJsonArray.put((Object)appDetailsLowerCase);
                }
                configObject.put("allowed_apps", (Object)appDetailsJsonArray);
                configObject.put("default_kiosk_app", (Object)defaultAppJsonArray);
                final JSONObject backgroundAppDetailsJsonObj = new MDMAppMgmtHandler().getAppInformation(backgroundAppGroupIDList);
                final JSONArray backGroundAppDetailsJsonArray = new JSONArray();
                iterator = backgroundAppDetailsJsonObj.keys();
                while (iterator.hasNext()) {
                    final String key2 = iterator.next();
                    final JSONObject appDetails2 = backgroundAppDetailsJsonObj.getJSONObject(key2);
                    final JSONObject appDetailsLowerCase2 = new JSONObject();
                    final Iterator appDetailItr2 = appDetails2.keys();
                    while (appDetailItr2.hasNext()) {
                        final String appDetailsKey2 = appDetailItr2.next();
                        if (appDetailsKey2.equals("APP_GROUP_ID")) {
                            appDetailsLowerCase2.put("app_id", appDetails2.get(appDetailsKey2));
                        }
                        else if (appDetailsKey2.equals("DISPLAY_IMAGE_LOC")) {
                            final Object value2 = appDetails2.get(appDetailsKey2);
                            if (MDMStringUtils.isEmpty(String.valueOf(value2))) {
                                continue;
                            }
                            appDetailsLowerCase2.put(appDetailsKey2.toLowerCase(), this.constructFileUrl(value2));
                        }
                        else {
                            appDetailsLowerCase2.put(appDetailsKey2.toLowerCase(), appDetails2.get(appDetailsKey2));
                        }
                    }
                    backGroundAppDetailsJsonArray.put((Object)appDetailsLowerCase2);
                }
                configObject.put("background_apps", (Object)backGroundAppDetailsJsonArray);
                final Iterator subIterator = dataObject.getRows("KioskCustomSettings");
                final JSONObject subConfig = new JSONObject();
                while (subIterator.hasNext()) {
                    String columnName = null;
                    Object columnValue = null;
                    final Row subPayloadRow = subIterator.next();
                    final List columns = subPayloadRow.getColumns();
                    for (int i = 0; i < columns.size(); ++i) {
                        columnName = columns.get(i);
                        columnValue = subPayloadRow.get(columnName);
                        if (columnName != null && columnValue != null && !columnName.equals("CONFIG_DATA_ITEM_ID")) {
                            subConfig.put(columnName.toLowerCase(), columnValue);
                        }
                    }
                    final Iterator<Row> batteryOptimizedAppsRows = dataObject.getRows("MDBATTERYOPTIMIZEDAPPS");
                    final List<Long> batteryOptimizedAppList = new ArrayList<Long>();
                    while (batteryOptimizedAppsRows.hasNext()) {
                        final Row row3 = batteryOptimizedAppsRows.next();
                        batteryOptimizedAppList.add((Long)row3.get("APP_GROUP_ID"));
                    }
                    final JSONObject batteryOptimizedAppsObj = new MDMAppMgmtHandler().getAppInformation(batteryOptimizedAppList);
                    final JSONArray batteryOptimizedAppsJsonArray = new JSONArray();
                    iterator = batteryOptimizedAppsObj.keys();
                    while (iterator.hasNext()) {
                        final String key3 = iterator.next();
                        final JSONObject appDetails3 = batteryOptimizedAppsObj.getJSONObject(key3);
                        final JSONObject appDetailsLowerCase3 = new JSONObject();
                        final Iterator appDetailItr3 = appDetails3.keys();
                        while (appDetailItr3.hasNext()) {
                            final String appDetailsKey3 = appDetailItr3.next();
                            if (appDetailsKey3.equals("APP_GROUP_ID")) {
                                appDetailsLowerCase3.put("app_id", appDetails3.get(appDetailsKey3));
                            }
                            else if (appDetailsKey3.equals("DISPLAY_IMAGE_LOC")) {
                                final Object value3 = appDetails3.get(appDetailsKey3);
                                if (MDMStringUtils.isEmpty(String.valueOf(value3))) {
                                    continue;
                                }
                                appDetailsLowerCase3.put(appDetailsKey3.toLowerCase(), this.constructFileUrl(value3));
                            }
                            else {
                                appDetailsLowerCase3.put(appDetailsKey3.toLowerCase(), appDetails3.get(appDetailsKey3));
                            }
                        }
                        batteryOptimizedAppsJsonArray.put((Object)appDetailsLowerCase3);
                    }
                    subConfig.put("battery_optimized_apps", (Object)batteryOptimizedAppsJsonArray);
                }
                configObject.put("kiosk_custom_settings", (Object)subConfig);
                this.addWebClipsRel(configObject, dataObject, templateConfigProperties);
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.WARNING, "Exception occured at converting do to apiJSON in Chrome Kiosk Config", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        super.validateServerJSON(serverJSON);
        boolean resetScreenLayoutJSON = true;
        boolean resetWebclipJSON = true;
        final int kioskMode = serverJSON.getInt("KIOSK_MODE");
        if (kioskMode == 3) {
            if (!serverJSON.has("WebClipPolicies")) {
                throw new APIHTTPException("COM0005", new Object[] { "webclip_policies_ids" });
            }
            if (serverJSON.has("ALLOWED_APPS") && serverJSON.getJSONArray("ALLOWED_APPS").length() > 0) {
                serverJSON.put("ALLOWED_APPS", (Object)new JSONArray());
            }
            resetWebclipJSON = false;
        }
        else if (kioskMode == 0) {
            if (!serverJSON.has("ALLOWED_APPS") || serverJSON.getJSONArray("ALLOWED_APPS").length() > 1) {
                throw new APIHTTPException("COM0005", new Object[] { "ALLOWED_APPS" });
            }
            if (serverJSON.has("WebClipPolicies") && serverJSON.getJSONArray("WebClipPolicies").length() > 0) {
                serverJSON.put("WebClipPolicies", (Object)new JSONArray());
            }
        }
        else if (kioskMode == 1) {
            if (!serverJSON.has("ALLOWED_APPS") || serverJSON.getJSONArray("ALLOWED_APPS").length() < 0) {
                throw new APIHTTPException("COM0005", new Object[] { "ALLOWED_APPS" });
            }
            resetScreenLayoutJSON = false;
            resetWebclipJSON = false;
        }
        if (resetScreenLayoutJSON) {
            serverJSON.put("ScreenLayout", (Object)new JSONObject());
            serverJSON.put("ScreenLayoutSettings", (Object)new JSONObject());
        }
        if (resetWebclipJSON) {
            serverJSON.put("WebClipPolicies", (Object)new JSONArray());
        }
    }
    
    @Override
    public boolean deletePayloadFile(final DataObject dataObject, final Long configDataId) {
        try {
            this.logger.log(Level.INFO, "remove the Android kiosk wallpaper payload for configDataId {0}", configDataId);
            final Iterator<Row> configDataItemRows = dataObject.getRows("ConfigDataItem", new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ID"), (Object)configDataId, 0));
            final List<String> configDataItemIds = DBUtil.getColumnValuesAsList((Iterator)configDataItemRows, "CONFIG_DATA_ITEM_ID");
            final DataObject androidKiosksObject = MDMUtil.getPersistence().get("AndroidKioskPolicy", new Criteria(Column.getColumn("AndroidKioskPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemIds.toArray(), 8));
            final Iterator<Row> androidKioskRows = androidKiosksObject.getRows("AndroidKioskPolicy");
            final List<String> androidWallpaperFiles = DBUtil.getColumnValuesAsList((Iterator)androidKioskRows, "WALLPAPER");
            for (final String androidWallpaper : androidWallpaperFiles) {
                ProfileUtil.getInstance().deleteProfileFile(androidWallpaper);
            }
            return true;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while deleting the Android kiosk wallpaper payload file", ex);
            return false;
        }
    }
}
