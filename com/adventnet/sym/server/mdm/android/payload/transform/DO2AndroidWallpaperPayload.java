package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.android.payload.AndroidWallpaperPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2AndroidWallpaperPayload implements DO2AndroidPayload
{
    public Logger logger;
    
    public DO2AndroidWallpaperPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidWallpaperPayload androidWallpaperPayload = null;
        try {
            if (dataObject != null) {
                final Iterator iterator = dataObject.getRows("MDMWallpaperPolicy");
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String belowHDPIWallPath = (String)payloadRow.get("BELOW_HDPI_WALLPAPER_PATH");
                    final String aboveHDPIWallPath = (String)payloadRow.get("ABOVE_HDPI_WALLPAPER_PATH");
                    final boolean allowWallpaperChange = (boolean)payloadRow.get("ALLOW_WALLPAPER_CHANGE");
                    androidWallpaperPayload = new AndroidWallpaperPayload("1.0", "com.mdm.mobiledevice.wallpaper", "Wallpaper Policy");
                    androidWallpaperPayload.setWallpaperURL_800((belowHDPIWallPath == null) ? "" : belowHDPIWallPath);
                    androidWallpaperPayload.setWallpaperURL_1920((aboveHDPIWallPath == null) ? "" : aboveHDPIWallPath);
                    androidWallpaperPayload.setAllowWallpaperChange(allowWallpaperChange);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while create Wallpaper payload", ex);
        }
        return androidWallpaperPayload;
    }
}
