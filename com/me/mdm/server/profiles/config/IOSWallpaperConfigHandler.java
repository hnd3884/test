package com.me.mdm.server.profiles.config;

import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONObject;

public class IOSWallpaperConfigHandler extends WallpaperConfigHandler
{
    @Override
    public void validateServerJSON(final JSONObject serverJSON) throws APIHTTPException {
        super.validateServerJSON(serverJSON);
        try {
            final Integer wallpaperPosition = serverJSON.getInt("SET_WALLPAPER_POSITION");
            final String hdpiSource = serverJSON.optString("BELOW_HDPI_WALLPAPER", "");
            final String hdpiLockSource = serverJSON.optString("BELOW_HDPI_LOCK_WALLPAPER", "");
            final String hdpiSourcePath = serverJSON.optString("BELOW_HDPI_WALLPAPER_PATH", "");
            final String hdpiLockSourcePath = serverJSON.optString("BELOW_HDPI_LOCK_WALLPAPER_PATH", "");
            boolean homeScreenConfigured = true;
            boolean lockScreenConfigured = true;
            if (MDMStringUtils.isEmpty(hdpiSource) && MDMStringUtils.isEmpty(hdpiSourcePath)) {
                homeScreenConfigured = false;
            }
            if (MDMStringUtils.isEmpty(hdpiLockSource) && MDMStringUtils.isEmpty(hdpiLockSourcePath)) {
                lockScreenConfigured = false;
            }
            switch (wallpaperPosition) {
                case 1: {
                    if (!lockScreenConfigured) {
                        throw new APIHTTPException("COM0005", new Object[] { "BELOW_HDPI_LOCK_WALLPAPER" });
                    }
                    break;
                }
                case 2: {
                    if (!homeScreenConfigured) {
                        throw new APIHTTPException("COM0005", new Object[] { "BELOW_HDPI_WALLPAPER" });
                    }
                    break;
                }
                case 3: {
                    if (!homeScreenConfigured) {
                        throw new APIHTTPException("COM0005", new Object[] { "BELOW_HDPI_WALLPAPER" });
                    }
                    break;
                }
                case 4: {
                    if (!homeScreenConfigured) {
                        throw new APIHTTPException("COM0005", new Object[] { "BELOW_HDPI_WALLPAPER" });
                    }
                    if (!lockScreenConfigured) {
                        throw new APIHTTPException("COM0005", new Object[] { "BELOW_HDPI_LOCK_WALLPAPER" });
                    }
                    break;
                }
            }
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
    }
    
    @Override
    public boolean deletePayloadFile(final DataObject dataObject, final Long configDataId) {
        try {
            this.logger.log(Level.INFO, "remove the IOS wallpaper payload for configDataId {0}", configDataId);
            final Iterator<Row> configDataItemRows = dataObject.getRows("ConfigDataItem", new Criteria(Column.getColumn("ConfigDataItem", "CONFIG_DATA_ID"), (Object)configDataId, 0));
            final List<String> configDataItemIds = DBUtil.getColumnValuesAsList((Iterator)configDataItemRows, "CONFIG_DATA_ITEM_ID");
            final DataObject wallpapersObject = MDMUtil.getPersistence().get("MDMWallpaperPolicy", new Criteria(Column.getColumn("MDMWallpaperPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemIds.toArray(), 8));
            Iterator<Row> wallpaperRows = wallpapersObject.getRows("MDMWallpaperPolicy");
            final List<String> aboveWallpaperFiles = DBUtil.getColumnValuesAsList((Iterator)wallpaperRows, "ABOVE_HDPI_WALLPAPER_PATH");
            for (final String aboveWallpaperFile : aboveWallpaperFiles) {
                ProfileUtil.getInstance().deleteProfileFile(aboveWallpaperFile);
            }
            wallpaperRows = wallpapersObject.getRows("MDMWallpaperPolicy");
            final List<String> belowWallpaperFiles = DBUtil.getColumnValuesAsList((Iterator)wallpaperRows, "BELOW_HDPI_WALLPAPER_PATH");
            for (final String belowWallpaperFile : belowWallpaperFiles) {
                ProfileUtil.getInstance().deleteProfileFile(belowWallpaperFile);
            }
            return true;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while deleting the ios wallpaper payload file", ex);
            return false;
        }
    }
}
