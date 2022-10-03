package com.adventnet.sym.webclient.mdm.config.formbean;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.io.File;
import org.json.JSONArray;
import java.util.Iterator;
import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.webclient.formbean.MDMDefaultFormBean;

public class KioskPolicyFormBean extends MDMDefaultFormBean
{
    public Logger logger;
    
    public KioskPolicyFormBean() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void dynaFormToDO(final JSONObject multipleConfigForm, final JSONObject[] dynaActionForm, final DataObject dataObject) throws SyMException {
        final int executionOrder = dynaActionForm.length;
        try {
            for (int k = 0; k < executionOrder; ++k) {
                final JSONObject dynaJSON = dynaActionForm[k];
                final String tableName = (String)dynaJSON.get("TABLE_NAME");
                final String subTableName = (String)dynaJSON.get("ANDROID_KIOSK_POLICY_APPS");
                Object configDataItemID = dynaJSON.optLong("CONFIG_DATA_ITEM_ID");
                if (tableName != null) {
                    final Row configData = dataObject.getRow("ConfigData");
                    final Object configDataID = configData.get("CONFIG_DATA_ID");
                    this.isAdded = false;
                    Row payloadRow;
                    if (configDataItemID == null || configDataItemID.equals(0L)) {
                        final Row dataItemRow = new Row("ConfigDataItem");
                        dataItemRow.set("CONFIG_DATA_ID", configDataID);
                        dataItemRow.set("EXECUTION_ORDER", (Object)k);
                        dataObject.addRow(dataItemRow);
                        configDataItemID = dataItemRow.get("CONFIG_DATA_ITEM_ID");
                        dynaJSON.put("CONFIG_DATA_ITEM_ID", configDataItemID);
                        super.insertConfigDataItemExtn(dynaJSON, dataObject);
                        payloadRow = new Row(tableName);
                        payloadRow.set("CONFIG_DATA_ITEM_ID", configDataItemID);
                        this.isAdded = true;
                    }
                    else {
                        payloadRow = dataObject.getRow(tableName);
                        dataObject.deleteRows(subTableName, (Criteria)null);
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
                                final JSONObject defaultAppData = new JSONObject(dynaJSON.optString("DEFAULT_KIOSK_APP", "{}"));
                                if (defaultAppData.length() <= 0) {
                                    continue;
                                }
                                final Iterator iterator = defaultAppData.keys();
                                while (iterator.hasNext()) {
                                    payloadRow.set(columnName, iterator.next());
                                }
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
                    final JSONObject appData = new JSONObject(dynaJSON.optString("ALLOWED_APPS"));
                    final JSONArray appGroupIDArr = appData.names();
                    for (int i = 0; i < appGroupIDArr.length(); ++i) {
                        final Row subRow = new Row(subTableName);
                        final List subColumns = payloadSubRow.getColumns();
                        for (final Object subColumn : subColumns) {
                            final String columnName = (String)subColumn;
                            if (columnName != null && columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                subRow.set(columnName, configDataItemID);
                            }
                            else {
                                subRow.set(columnName, (Object)Long.valueOf(appGroupIDArr.optString(i, "-1")));
                            }
                        }
                        dataObject.addRow(subRow);
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
                        final List subColumns2 = customSettingsRow.getColumns();
                        for (final Object subColumn2 : subColumns2) {
                            final String columnName = (String)subColumn2;
                            if (!columnName.equals("CONFIG_DATA_ITEM_ID")) {
                                customSettingsRow.set(columnName, customSettings.get(columnName));
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
                }
            }
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
            ex.printStackTrace();
        }
        String path = this.getKioskImageDBPath(collectionID) + File.separator + fileName;
        path = path.replaceAll("\\\\", "/");
        return path;
    }
}
