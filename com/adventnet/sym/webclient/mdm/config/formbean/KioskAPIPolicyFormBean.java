package com.adventnet.sym.webclient.mdm.config.formbean;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.io.File;
import java.util.List;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.mdm.server.payload.PayloadException;
import org.json.JSONArray;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;

public class KioskAPIPolicyFormBean extends DefaultKioskFormBean
{
    public Logger logger;
    
    public KioskAPIPolicyFormBean() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException, PayloadException {
        final int executionOrder = dynaActionForm.length;
        try {
            for (int k = 0; k < executionOrder; ++k) {
                final JSONObject dynaJSON = dynaActionForm[k];
                final String tableName = (String)dynaJSON.get("TABLE_NAME");
                final String subTableName = (String)dynaJSON.get("ANDROID_KIOSK_POLICY_APPS");
                String backGroudsubTableName = null;
                String batteryOptimizedAppsSubTableName = null;
                if (dynaJSON.has("BACKGROUND_APPS")) {
                    backGroudsubTableName = (String)dynaJSON.get("ANDROID_KIOSK_POLICY_BACKGROUND_APPS");
                }
                if (dynaJSON.has("ANDROID_KIOSK_BATTERY_OPTIMIZED_APPS")) {
                    batteryOptimizedAppsSubTableName = (String)dynaJSON.get("ANDROID_KIOSK_BATTERY_OPTIMIZED_APPS");
                }
                Object configDataItemID = dynaJSON.optLong("CONFIG_DATA_ITEM_ID");
                if (tableName != null) {
                    final Row configData = dataObject.getRow("ConfigData");
                    final Object configDataID = configData.get("CONFIG_DATA_ID");
                    this.isAdded = false;
                    Row payloadRow;
                    if (configDataItemID == null || configDataItemID.equals(0L)) {
                        this.insertConfigDataItem(dynaJSON, dataObject, k);
                        final Row dataItemRow = dataObject.getRow("ConfigDataItem");
                        configDataItemID = dataItemRow.get("CONFIG_DATA_ITEM_ID");
                        payloadRow = new Row(tableName);
                        payloadRow.set("CONFIG_DATA_ITEM_ID", configDataItemID);
                        this.isAdded = true;
                    }
                    else {
                        final Iterator configDataItemExtnRow = dataObject.getRows("MdConfigDataItemExtn");
                        if (!configDataItemExtnRow.hasNext()) {
                            this.insertConfigDataItemExtn(dynaJSON, dataObject);
                        }
                        payloadRow = dataObject.getRow(tableName);
                        dataObject.deleteRows(subTableName, (Criteria)null);
                        dataObject.deleteRows(backGroudsubTableName, (Criteria)null);
                        dataObject.deleteRows(batteryOptimizedAppsSubTableName, (Criteria)null);
                    }
                    final List columns = payloadRow.getColumns();
                    for (final Object column : columns) {
                        final String columnName = (String)column;
                        final Object columnValue = dynaJSON.opt(columnName);
                        if (columnName != null) {
                            if (columnName.equals("WALLPAPER")) {
                                String wallpaperSrc = dynaJSON.optString("WALLPAPER", "");
                                if (wallpaperSrc != null && !wallpaperSrc.equals("")) {
                                    final Row row = dataObject.getRow("CfgDataToCollection", new Criteria(Column.getColumn("CfgDataToCollection", "CONFIG_DATA_ID"), configDataID, 0));
                                    final Long collectionID = (Long)row.get("COLLECTION_ID");
                                    final String destPath = this.getProfileKioskFolderPath(collectionID);
                                    wallpaperSrc = this.uploadWallpaper(wallpaperSrc, destPath, collectionID);
                                    payloadRow.set(columnName, (Object)wallpaperSrc);
                                }
                                else {
                                    final boolean isWallpaperRemove = dynaJSON.optBoolean("IS_WALLPAPER_REMOVED", false);
                                    if (!isWallpaperRemove) {
                                        continue;
                                    }
                                    payloadRow.set(columnName, (Object)null);
                                }
                            }
                            else if (columnName.equals("DEFAULT_KIOSK_APP")) {
                                if (dynaJSON.optString("DEFAULT_KIOSK_APP", "").equals("")) {
                                    continue;
                                }
                                final JSONArray defaultAppDataArray = new JSONArray(dynaJSON.optString("DEFAULT_KIOSK_APP", "[]"));
                                if (defaultAppDataArray.length() <= 0) {
                                    continue;
                                }
                                final JSONObject defaultAppData = defaultAppDataArray.optJSONObject(0);
                                if (defaultAppData == null) {
                                    continue;
                                }
                                payloadRow.set(columnName, defaultAppData.opt("APP_GROUP_ID"));
                            }
                            else {
                                if (columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                    continue;
                                }
                                payloadRow.set(columnName, columnValue);
                            }
                        }
                    }
                    final Row payloadSubRow = new Row(subTableName);
                    final JSONArray appData = new JSONArray(dynaJSON.optString("ALLOWED_APPS"));
                    for (int i = 0; i < appData.length(); ++i) {
                        final Row subRow = new Row(subTableName);
                        final List subColumns = payloadSubRow.getColumns();
                        for (final Object subColumn : subColumns) {
                            final String columnName = (String)subColumn;
                            if (columnName != null && columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                subRow.set(columnName, configDataItemID);
                            }
                            else {
                                subRow.set(columnName, (Object)Long.valueOf(appData.getJSONObject(i).optString("APP_GROUP_ID", "-1")));
                            }
                        }
                        dataObject.addRow(subRow);
                    }
                    if (dynaJSON.has("BACKGROUND_APPS")) {
                        final Row backGroundAppSubRow = new Row(backGroudsubTableName);
                        final JSONArray backgroundAppData = new JSONArray(dynaJSON.optString("BACKGROUND_APPS"));
                        for (int j = 0; j < backgroundAppData.length(); ++j) {
                            final Row subRow2 = new Row(backGroudsubTableName);
                            final List subColumns2 = backGroundAppSubRow.getColumns();
                            for (final Object subColumn2 : subColumns2) {
                                final String columnName = (String)subColumn2;
                                if (columnName != null && columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                    subRow2.set(columnName, configDataItemID);
                                }
                                else {
                                    subRow2.set(columnName, (Object)Long.valueOf(backgroundAppData.getJSONObject(j).optString("APP_GROUP_ID", "-1")));
                                }
                            }
                            dataObject.addRow(subRow2);
                        }
                    }
                    if (dynaJSON.optBoolean("ALLOW_CUSTOM_SETTINGS")) {
                        Boolean isCustomSettingsAdded = false;
                        Row customSettingsRow = dataObject.getRow("KioskCustomSettings");
                        if (customSettingsRow == null) {
                            isCustomSettingsAdded = true;
                            customSettingsRow = new Row("KioskCustomSettings");
                            customSettingsRow.set("CONFIG_DATA_ITEM_ID", configDataItemID);
                        }
                        final JSONObject customSettings = dynaJSON.optJSONObject("KIOSK_CUSTOM_SETTINGS");
                        final List subColumns3 = customSettingsRow.getColumns();
                        for (final Object subColumn3 : subColumns3) {
                            final String columnName = (String)subColumn3;
                            if (columnName.equals("ALLOW_WIFI_NETWORK_CONFIGURATION")) {
                                boolean isAddWifiNetwork = false;
                                if (customSettings.optBoolean("ALLOW_WIFI")) {
                                    isAddWifiNetwork = customSettings.optBoolean("ALLOW_WIFI_NETWORK_CONFIGURATION");
                                }
                                customSettingsRow.set(columnName, (Object)isAddWifiNetwork);
                            }
                            else if (columnName.equals("HOTSPOT_SETTINGS_TIMEOUT") || columnName.equals("APN_SETTINGS_TIMEOUT") || columnName.equals("BLUETOOTH_SETTINGS_TIMEOUT")) {
                                customSettingsRow.set(columnName, customSettings.get("SETTINGS_TIMEOUT"));
                            }
                            else {
                                if (columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                    continue;
                                }
                                customSettingsRow.set(columnName, customSettings.get(columnName));
                            }
                        }
                        if (customSettings.has("BATTERY_OPTIMIZED_APPS")) {
                            final Row batteryOptimizedAppsSubRow = new Row(batteryOptimizedAppsSubTableName);
                            final JSONArray batteryOptimizedAppsData = new JSONArray(customSettings.optString("BATTERY_OPTIMIZED_APPS"));
                            for (int l = 0; l < batteryOptimizedAppsData.length(); ++l) {
                                final Row subRow3 = new Row(batteryOptimizedAppsSubTableName);
                                final List batteryOptimizedAppsSubRowColumns = batteryOptimizedAppsSubRow.getColumns();
                                for (final Object subColumn4 : batteryOptimizedAppsSubRowColumns) {
                                    final String columnName = (String)subColumn4;
                                    if (columnName != null && columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                        subRow3.set(columnName, configDataItemID);
                                    }
                                    else {
                                        subRow3.set(columnName, (Object)Long.valueOf(batteryOptimizedAppsData.getJSONObject(l).optString("APP_GROUP_ID", "-1")));
                                    }
                                }
                                dataObject.addRow(subRow3);
                            }
                        }
                        if (isCustomSettingsAdded) {
                            dataObject.addRow(customSettingsRow);
                        }
                        else {
                            dataObject.updateRow(customSettingsRow);
                        }
                    }
                    if (dynaJSON.optString("CONFIG_NAME").equalsIgnoreCase("ANDROID_KIOSK_POLICY") && !dynaJSON.optBoolean("ENABLE_DEFAULT_APP")) {
                        payloadRow.set("DEFAULT_KIOSK_APP", (Object)null);
                        payloadRow.set("DEFAULT_APP_LAUNCH_TIMEOUT", (Object)(-1));
                    }
                    if (this.isAdded) {
                        dataObject.addRow(payloadRow);
                    }
                    else {
                        dataObject.updateRow(payloadRow);
                    }
                    this.addScreenLayout(dataObject, dynaJSON, multipleConfigForm);
                    this.addWebClipsRel(dataObject, dynaJSON, configDataItemID);
                }
            }
        }
        catch (final PayloadException e) {
            throw e;
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while saving profile", exp);
            throw new SyMException(1002, exp.getCause());
        }
    }
    
    private String getKioskImageDBPath(final Long collectionID) {
        return File.separator + "mdm" + File.separator + "kiosk" + File.separator + collectionID;
    }
    
    private String getProfileKioskFolderPath(final Long collectionID) throws Exception {
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String kioskWallPaperImagePath = webappsDir + File.separator + "mdm" + File.separator + "kiosk" + File.separator + collectionID;
        return kioskWallPaperImagePath;
    }
    
    private String uploadWallpaper(final String src, final String dest, final Long collectionID) {
        final File file = new File(src);
        final String fileName = file.getName();
        final File directoryPath = new File(dest);
        if (!directoryPath.exists()) {
            directoryPath.mkdir();
        }
        final String completeFilePath = directoryPath + File.separator + fileName;
        try {
            ApiFactoryProvider.getFileAccessAPI().copyFile(src, completeFilePath);
            ApiFactoryProvider.getFileAccessAPI().deleteDirectory(file.getParent());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in uploadWallpaper", ex);
        }
        String path = this.getKioskImageDBPath(collectionID) + File.separator + fileName;
        path = path.replaceAll("\\\\", "/");
        return path;
    }
}
