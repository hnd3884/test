package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.ios.payload.ManagedSettingItem;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.dd.plist.NSDictionary;
import java.util.List;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2iOSWallpaperPayload implements DO2Settings
{
    protected static Logger logger;
    public static String wallpaperName;
    public static String homeScreenWallpaperName;
    public static String lockscreenWallpaperName;
    
    @Override
    public List<NSDictionary> createSettingCommand(final DataObject dataObject, final JSONObject params) {
        final List<NSDictionary> settingList = new ArrayList<NSDictionary>();
        Iterator wallpaperIterator = null;
        try {
            if (!dataObject.isEmpty()) {
                wallpaperIterator = dataObject.getRows("MDMWallpaperPolicy");
                while (wallpaperIterator.hasNext()) {
                    final Row wallpaperPayloadRow = wallpaperIterator.next();
                    String fileName = (String)wallpaperPayloadRow.get("BELOW_HDPI_WALLPAPER_PATH");
                    final Integer whereToApply = (Integer)wallpaperPayloadRow.get("SET_WALLPAPER_POSITION");
                    final String webdir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
                    fileName = fileName.replace("/", File.separator);
                    if (!MDMStringUtils.isEmpty(fileName) && whereToApply == 4) {
                        String filePath = webdir + fileName + File.separator + DO2iOSWallpaperPayload.lockscreenWallpaperName;
                        settingList.add(this.createWallpaperSetting(filePath, 1).getPayloadDict());
                        filePath = webdir + fileName + File.separator + DO2iOSWallpaperPayload.homeScreenWallpaperName;
                        settingList.add(this.createWallpaperSetting(filePath, 2).getPayloadDict());
                    }
                    else {
                        if (MDMStringUtils.isEmpty(fileName)) {
                            continue;
                        }
                        final String filePath = webdir + fileName + File.separator + DO2iOSWallpaperPayload.wallpaperName;
                        settingList.add(this.createWallpaperSetting(filePath, whereToApply).getPayloadDict());
                    }
                }
            }
        }
        catch (final Exception ex) {
            DO2iOSWallpaperPayload.logger.log(Level.SEVERE, "Exception while creating D02Wallpapaer payload", ex);
        }
        return settingList;
    }
    
    private ManagedSettingItem createWallpaperSetting(final String filePath, final Integer whereToApply) {
        final ManagedSettingItem settingsPayload = new ManagedSettingItem("Wallpaper");
        settingsPayload.setImage(filePath);
        settingsPayload.setWhere(whereToApply);
        return settingsPayload;
    }
    
    static {
        DO2iOSWallpaperPayload.logger = Logger.getLogger("MDMConfigLogger");
        DO2iOSWallpaperPayload.wallpaperName = "default.ioswallpaper";
        DO2iOSWallpaperPayload.homeScreenWallpaperName = "home.ioswallpaper";
        DO2iOSWallpaperPayload.lockscreenWallpaperName = "lock.ioswallpaper";
    }
}
