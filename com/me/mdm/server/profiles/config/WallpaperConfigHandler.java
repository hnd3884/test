package com.me.mdm.server.profiles.config;

import java.io.File;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.ios.payload.transform.DO2iOSWallpaperPayload;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;

public class WallpaperConfigHandler extends DefaultConfigHandler
{
    @Override
    public JSONObject apiJSONToServerJSON(final String configName, final JSONObject apiJSON) throws APIHTTPException {
        final JSONObject convertedJSON = super.apiJSONToServerJSON(configName, apiJSON);
        try {
            final Long payloadId = convertedJSON.optLong("CONFIG_DATA_ITEM_ID");
            final Boolean belowHdpiModified = convertedJSON.optBoolean("IS_BELOW_HDPI_WALL_MODIFIED");
            final Boolean belowHdpiLockModified = convertedJSON.optBoolean("IS_BELOW_HDPI_LOCK_WALL_MODIFIED");
            final Boolean aboveHdpiModified = convertedJSON.optBoolean("IS_ABOVE_HDPI_WALL_MODIFIED");
            if (payloadId != 0L && (!belowHdpiLockModified || !belowHdpiModified || !aboveHdpiModified)) {
                final DataObject getPayloadData = this.getPayloadData(payloadId, "MDMWallpaperPolicy");
                if (payloadId != null && !getPayloadData.isEmpty()) {
                    this.convertWallpaperImageSource(getPayloadData, convertedJSON, configName);
                }
            }
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return convertedJSON;
    }
    
    @Override
    protected JSONArray DOToAPIJSON(final DataObject dataObject, final String configName, final String tableName) throws APIHTTPException {
        try {
            final JSONArray result = new JSONArray();
            if (dataObject.containsTable(tableName)) {
                final Iterator<Row> rows = dataObject.getRows(tableName);
                while (rows.hasNext()) {
                    final JSONObject config = new JSONObject();
                    final Row row = rows.next();
                    String columnName = null;
                    Object columnValue = null;
                    JSONObject property = null;
                    final int type = (int)row.get("SET_WALLPAPER_POSITION");
                    final List columns = row.getColumns();
                    for (int i = 0; i < columns.size(); ++i) {
                        columnName = columns.get(i);
                        property = this.getDetailsForColName(configName, columnName);
                        columnValue = row.get(columnName);
                        if (columnName.equals("BELOW_HDPI_WALLPAPER_PATH") || columnName.equals("ABOVE_HDPI_WALLPAPER_PATH")) {
                            if (columnValue != null) {
                                final JSONObject param = new JSONObject();
                                param.put("columnName", (Object)columnName);
                                param.put("columnValue", columnValue);
                                param.put("type", type);
                                param.put("currentConfig", (Object)configName);
                                this.processColumnValue(param, config);
                            }
                        }
                        else if (property != null && property.has("alias") && columnValue != null) {
                            if (property.has("type") && String.valueOf(property.get("type")).equals("File")) {
                                config.put(String.valueOf(property.get("alias")), this.constructFileUrl(columnValue));
                            }
                            else {
                                config.put(String.valueOf(property.get("alias")), columnValue);
                            }
                        }
                    }
                    result.put((Object)config);
                }
            }
            return result;
        }
        catch (final JSONException | DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in DOToServerJSON", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void processColumnValue(final JSONObject param, final JSONObject configJSON) throws JSONException {
        final String columnName = param.optString("columnName");
        final int type = param.optInt("type");
        final String currentConfig = param.optString("currentConfig");
        Object columnValue = param.opt("columnValue");
        final String lockColumnName = columnName.equals("BELOW_HDPI_WALLPAPER_PATH") ? "BELOW_HDPI_LOCK_WALLPAPER_PATH" : null;
        final JSONObject columnObject = this.getDetailsForColName(currentConfig, columnName);
        final String columnValueAlias = String.valueOf(columnObject.get("alias"));
        final JSONObject lockColumnObject = this.getDetailsForColName(currentConfig, lockColumnName);
        final String lockColumnAlias = String.valueOf(lockColumnObject.get("alias"));
        if (type == 1) {
            if (currentConfig.equalsIgnoreCase("ioswallpaperpolicy")) {
                columnValue = columnValue + "/" + DO2iOSWallpaperPayload.wallpaperName;
            }
            columnValue = this.constructFileUrl(columnValue);
            configJSON.put(lockColumnAlias, columnValue);
        }
        else if (type == 2) {
            if (currentConfig.equalsIgnoreCase("ioswallpaperpolicy")) {
                columnValue = columnValue + "/" + DO2iOSWallpaperPayload.wallpaperName;
            }
            columnValue = this.constructFileUrl(columnValue);
            configJSON.put(columnValueAlias, columnValue);
        }
        else if (type == 3) {
            if (currentConfig.equalsIgnoreCase("ioswallpaperpolicy")) {
                columnValue = columnValue + "/" + DO2iOSWallpaperPayload.wallpaperName;
            }
            columnValue = this.constructFileUrl(columnValue);
            configJSON.put(columnValueAlias, columnValue);
            if (currentConfig.equalsIgnoreCase("ioswallpaperpolicy")) {
                configJSON.put(lockColumnAlias, columnValue);
            }
        }
        else if (type == 4) {
            Object homeColumnValue = columnValue + "/" + DO2iOSWallpaperPayload.homeScreenWallpaperName;
            Object lockColumnValue = columnValue + "/" + DO2iOSWallpaperPayload.lockscreenWallpaperName;
            homeColumnValue = this.constructFileUrl(homeColumnValue);
            lockColumnValue = this.constructFileUrl(lockColumnValue);
            configJSON.put(columnValueAlias, homeColumnValue);
            configJSON.put(lockColumnAlias, lockColumnValue);
        }
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        try {
            super.validateServerJSON(serverJSON);
            boolean status = false;
            final String hdpiSource = serverJSON.optString("BELOW_HDPI_WALLPAPER", "");
            final String hdpiLockSource = serverJSON.optString("BELOW_HDPI_LOCK_WALLPAPER", "");
            final String abovehdpiSource = serverJSON.optString("ABOVE_HDPI_LOCK_WALLPAPER", "");
            if (!MDMStringUtils.isEmpty(hdpiSource)) {
                status = (status || this.isImageSizeGreater(hdpiSource));
            }
            if (!MDMStringUtils.isEmpty(hdpiLockSource)) {
                status = (status || this.isImageSizeGreater(hdpiLockSource));
            }
            if (!MDMStringUtils.isEmpty(abovehdpiSource)) {
                status = (status || this.isImageSizeGreater(abovehdpiSource));
            }
            if (status) {
                throw new APIHTTPException("PAY0001", new Object[0]);
            }
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while validating the wallpaper server JSON", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private DataObject getPayloadData(final Long payloadId, final String tableName) throws APIHTTPException {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table(tableName));
            selectQuery.setCriteria(new Criteria(new Column(tableName, "CONFIG_DATA_ITEM_ID"), (Object)payloadId, 0));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            return MDMUtil.getPersistence().get(selectQuery);
        }
        catch (final DataAccessException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void convertWallpaperSource(final String actualSource, final JSONObject convertedJSON, final boolean isModified, final String keyName) throws APIHTTPException {
        try {
            final String source = convertedJSON.optString(keyName);
            if (!isModified && MDMStringUtils.isEmpty(source) && !MDMStringUtils.isEmpty(actualSource)) {
                convertedJSON.put(keyName, (Object)actualSource.replace("/", File.separator));
            }
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void convertWallpaperImageSource(final DataObject dataObject, final JSONObject convertedJSON, final String currentConfig) {
        try {
            final Row wallpaperPolicy = dataObject.getRow("MDMWallpaperPolicy");
            final String wallpaperPath = (String)wallpaperPolicy.get("BELOW_HDPI_WALLPAPER_PATH");
            final String aboveHdpiWallpaperPath = (String)wallpaperPolicy.get("ABOVE_HDPI_WALLPAPER_PATH");
            final Integer wallpaperType = (Integer)wallpaperPolicy.get("SET_WALLPAPER_POSITION");
            this.convertWallpaperBasedOnType(wallpaperPath, wallpaperType, convertedJSON, "BELOW_HDPI_WALLPAPER_PATH", currentConfig);
            this.convertWallpaperBasedOnType(aboveHdpiWallpaperPath, wallpaperType, convertedJSON, "ABOVE_HDPI_WALLPAPER_PATH", currentConfig);
        }
        catch (final Exception ex) {}
    }
    
    private void convertWallpaperBasedOnType(String actualSource, final int wallpaperType, final JSONObject convertedJSON, final String columnName, final String currentConfig) {
        final String lockColumnName = columnName.equals("BELOW_HDPI_WALLPAPER_PATH") ? "BELOW_HDPI_LOCK_WALLPAPER_PATH" : null;
        final Boolean belowHdpiModified = convertedJSON.optBoolean(columnName);
        Boolean belowHdpiLockModified = false;
        if (lockColumnName != null) {
            belowHdpiLockModified = convertedJSON.optBoolean(lockColumnName);
        }
        if (wallpaperType == 1) {
            if (currentConfig.equalsIgnoreCase("ioswallpaperpolicy")) {
                actualSource = actualSource + File.separator + DO2iOSWallpaperPayload.wallpaperName;
            }
            this.convertWallpaperSource(actualSource, convertedJSON, belowHdpiLockModified, columnName);
        }
        else if (wallpaperType == 2) {
            if (currentConfig.equalsIgnoreCase("ioswallpaperpolicy")) {
                actualSource = actualSource + File.separator + DO2iOSWallpaperPayload.wallpaperName;
            }
            this.convertWallpaperSource(actualSource, convertedJSON, belowHdpiModified, columnName);
        }
        else if (wallpaperType == 3) {
            if (currentConfig.equalsIgnoreCase("ioswallpaperpolicy")) {
                actualSource = actualSource + "/" + DO2iOSWallpaperPayload.wallpaperName;
            }
            this.convertWallpaperSource(actualSource, convertedJSON, belowHdpiModified, columnName);
            if (currentConfig.equalsIgnoreCase("ioswallpaperpolicy") && !MDMStringUtils.isEmpty(lockColumnName)) {
                this.convertWallpaperSource(actualSource, convertedJSON, belowHdpiLockModified, lockColumnName);
            }
        }
        else if (wallpaperType == 4) {
            final Object homeColumnValue = actualSource + "/" + DO2iOSWallpaperPayload.homeScreenWallpaperName;
            final Object lockColumnValue = actualSource + "/" + DO2iOSWallpaperPayload.lockscreenWallpaperName;
            this.convertWallpaperSource((String)homeColumnValue, convertedJSON, belowHdpiModified, columnName);
            this.convertWallpaperSource((String)lockColumnValue, convertedJSON, belowHdpiLockModified, lockColumnName);
        }
    }
}
