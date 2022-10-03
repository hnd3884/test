package com.adventnet.sym.server.mdm.ios.payload.transform;

import com.adventnet.sym.server.mdm.ios.payload.ManagedSettingItem;
import com.dd.plist.NSData;
import java.util.logging.Level;
import com.me.mdm.server.profiles.LockScreenDataHandler;
import com.me.mdm.server.profiles.ios.IOSLockScreenHandler;
import java.util.ArrayList;
import com.dd.plist.NSDictionary;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import java.util.List;

public class DO2LockScreenPayload implements DO2Settings
{
    public static String lockScreenImageName;
    private static final List PORTRAIT_ONLY_MODEL_TYPE;
    private static final Logger LOGGER;
    
    @Override
    public List<NSDictionary> createSettingCommand(final DataObject dataObject, final JSONObject params) {
        final List<NSDictionary> settingList = new ArrayList<NSDictionary>();
        try {
            if (!dataObject.isEmpty()) {
                final IOSLockScreenHandler iosLockScreenHandler = new IOSLockScreenHandler();
                final JSONObject lockscreenResolution = iosLockScreenHandler.deviceResolution(params.optLong("ResourceId"));
                final LockScreenDataHandler dataHandler = new LockScreenDataHandler();
                final JSONObject imageObject = new JSONObject();
                imageObject.put("dataObject", (Object)dataObject);
                imageObject.put("ResourceId", params.opt("ResourceId"));
                imageObject.put("strUDID", params.opt("strUDID"));
                final Integer wallpaperType = (Integer)dataObject.getFirstValue("LockScreenConfiguration", "ORIENTATION");
                final String screenSize = lockscreenResolution.optString("SCREEN_SIZE");
                Integer widthResolution = 0;
                Integer heightResolution = 0;
                if (!screenSize.equals("")) {
                    widthResolution = Integer.parseInt(screenSize.split("x")[0]);
                    heightResolution = Integer.parseInt(screenSize.split("x")[1]);
                }
                final Integer modelType = lockscreenResolution.optInt("MODEL_TYPE");
                widthResolution = ((widthResolution != 0) ? widthResolution : 1080);
                heightResolution = ((heightResolution != 0) ? heightResolution : 1920);
                imageObject.put("HeightResolution", (Object)heightResolution);
                imageObject.put("WidthResolution", (Object)widthResolution);
                if (wallpaperType == 2 && !DO2LockScreenPayload.PORTRAIT_ONLY_MODEL_TYPE.contains(modelType)) {
                    imageObject.put("HeightResolution", (Object)widthResolution);
                    imageObject.put("WidthResolution", (Object)heightResolution);
                }
                final NSData finalImageData = dataHandler.writeTextToImage(imageObject);
                settingList.add(this.createWallpaperSetting(finalImageData, 1).getPayloadDict());
            }
        }
        catch (final Exception e) {
            DO2LockScreenPayload.LOGGER.log(Level.SEVERE, "Exception while getting Lockscreen payload", e);
        }
        return settingList;
    }
    
    private ManagedSettingItem createWallpaperSetting(final NSData data, final Integer whereToApply) {
        final ManagedSettingItem settingsPayload = new ManagedSettingItem("Wallpaper");
        settingsPayload.setImage(data);
        settingsPayload.setWhere(whereToApply);
        return settingsPayload;
    }
    
    static {
        DO2LockScreenPayload.lockScreenImageName = "defaultLockscreen.ioswallpaper";
        PORTRAIT_ONLY_MODEL_TYPE = new ArrayList() {
            {
                this.add(1);
            }
        };
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
