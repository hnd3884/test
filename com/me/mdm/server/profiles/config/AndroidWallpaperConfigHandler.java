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

public class AndroidWallpaperConfigHandler extends DefaultConfigHandler
{
    @Override
    public boolean deletePayloadFile(final DataObject dataObject, final Long configDataId) {
        try {
            this.logger.log(Level.INFO, "remove the android wallpaper payload for configDataId {0}", configDataId);
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
            this.logger.log(Level.SEVERE, "Exception while deleting the android wallpaper payload file", ex);
            return false;
        }
    }
}
