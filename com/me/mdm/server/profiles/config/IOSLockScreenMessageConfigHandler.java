package com.me.mdm.server.profiles.config;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.List;
import java.util.Iterator;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.ios.payload.transform.DO2LockScreenPayload;
import java.io.File;
import org.json.JSONObject;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class IOSLockScreenMessageConfigHandler extends DefaultConfigHandler
{
    @Override
    public JSONArray DOToAPIJSON(final DataObject dataObject, final String configName) throws APIHTTPException {
        try {
            final JSONArray response = new JSONArray();
            final JSONArray cfgItems = super.DOToAPIJSON(dataObject, configName, "LockScreenToCfgDataItem");
            final JSONArray configs = super.DOToAPIJSON(dataObject, configName, "LockScreenConfiguration");
            for (int i = 0; i < cfgItems.length(); ++i) {
                final JSONObject result = JSONUtil.mergeJSONObjects(cfgItems.getJSONObject(i), configs.getJSONObject(i));
                result.put("messages", (Object)this.getMessageDetails(dataObject));
                response.put((Object)result);
            }
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "exception in IOSLockScreenMessageConfigHandler", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    protected Object transformTableValueToApiValue(final DataObject dataObject, final String columnName, Object columnValue, final String tableName, final String configName) throws APIHTTPException {
        try {
            if (columnName.equals("WALLPAPER_PATH")) {
                final Row row = dataObject.getFirstRow("LockScreenConfiguration");
                final Integer wallpaperType = (Integer)row.get("WALLPAPER_TYPE");
                if (wallpaperType == 2) {
                    final String tempPath = columnValue + File.separator + DO2LockScreenPayload.lockScreenImageName;
                    columnValue = tempPath.replaceAll("\\\\", "/");
                    columnValue = this.constructFileUrl(columnValue);
                }
                return columnValue;
            }
            return columnValue;
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception occurred in getTransformedValue", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONArray getMessageDetails(final DataObject dataObject) {
        final JSONArray messageArray = new JSONArray();
        try {
            final SortColumn sortColumn = new SortColumn(new Column("LockScreenMessages", "ORDER"), true);
            dataObject.sortRows("LockScreenMessages", new SortColumn[] { sortColumn });
            final Iterator messageIterator = dataObject.getRows("LockScreenMessages");
            while (messageIterator.hasNext()) {
                final Row messageRow = messageIterator.next();
                final JSONObject messgaeObject = new JSONObject();
                final List columnList = messageRow.getColumns();
                for (int i = 0; i < columnList.size(); ++i) {
                    final String columnName = columnList.get(i);
                    final Object columnValue = messageRow.get(columnName);
                    if (!columnName.equals("MESSAGE_ID")) {
                        messgaeObject.put(columnName.toLowerCase(), columnValue);
                    }
                }
                messageArray.put((Object)messgaeObject);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting message details in lockscreen", e);
        }
        return messageArray;
    }
    
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        try {
            super.validateServerJSON(serverJSON);
            final String webClips = serverJSON.optString("WALLPAPER", "");
            if (!MDMStringUtils.isEmpty(webClips)) {
                if (this.isFileSizeGreater(webClips, 20971520L)) {
                    throw new APIHTTPException("PAY0001", new Object[0]);
                }
                ImageIO.read(new File(webClips));
            }
        }
        catch (final IIOException e) {
            this.logger.log(Level.SEVERE, "Invalid jpeg file", e);
            throw new APIHTTPException("PAY0014", new Object[0]);
        }
        catch (final APIHTTPException e2) {
            throw e2;
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Exception while validating the lock screen wallpaper server JSON", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public boolean deletePayloadFile(final DataObject dataObject, final Long configDataId) {
        try {
            this.logger.log(Level.INFO, "remove the lock screen wallpaper payload for configDataId {0}", configDataId);
            final Iterator<Row> configDataItemRows = dataObject.getRows("ConfigDataItem", new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ID"), (Object)configDataId, 0));
            final List<String> configDataItemIds = DBUtil.getColumnValuesAsList((Iterator)configDataItemRows, "CONFIG_DATA_ITEM_ID");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("LockScreenToCfgDataItem"));
            selectQuery.addJoin(new Join("LockScreenToCfgDataItem", "LockScreenConfiguration", new String[] { "LOCK_SCREEN_CONFIGURATION_ID" }, new String[] { "LOCK_SCREEN_CONFIGURATION_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("LockScreenConfiguration", "WALLPAPER_PATH"));
            selectQuery.addSelectColumn(Column.getColumn("LockScreenConfiguration", "LOCK_SCREEN_CONFIGURATION_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("LockScreenToCfgDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemIds.toArray(), 8);
            selectQuery.setCriteria(criteria);
            final DataObject lockScreenWallpaperObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator<Row> lockScreenRows = lockScreenWallpaperObject.getRows("LockScreenConfiguration");
            final List<String> lockScreenWallpaperFiles = DBUtil.getColumnValuesAsList((Iterator)lockScreenRows, "WALLPAPER_PATH");
            for (final String lockScreenWallpaper : lockScreenWallpaperFiles) {
                ProfileUtil.getInstance().deleteProfileFile(lockScreenWallpaper);
            }
            return true;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while deleting the lock screen wallpaper payload file", ex);
            return false;
        }
    }
}
