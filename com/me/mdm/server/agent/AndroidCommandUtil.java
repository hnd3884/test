package com.me.mdm.server.agent;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;

public class AndroidCommandUtil
{
    public static Logger logger;
    private static AndroidCommandUtil androidCommandUtil;
    
    public static AndroidCommandUtil getInstance() {
        if (AndroidCommandUtil.androidCommandUtil == null) {
            AndroidCommandUtil.androidCommandUtil = new AndroidCommandUtil();
        }
        return AndroidCommandUtil.androidCommandUtil;
    }
    
    public JSONObject addAbsoluteUrlInAndroidInstallCommand(final DeviceCommand deviceCommand, final JSONObject commandJson) {
        try {
            final String command = deviceCommand.commandType;
            if (command.equals("InstallApplication") && commandJson.has("Command") && commandJson.getJSONObject("Command").has("RequestData") && !commandJson.getJSONObject("Command").getJSONObject("RequestData").has("AbsoluteUrl")) {
                final Long collectionId = (Long)commandJson.getJSONObject("Command").getJSONObject("RequestData").get("CollectionID");
                final String absoluteUrl = AppsUtil.getInstance().getAbsoluteUrlFromCollectionID(collectionId);
                commandJson.getJSONObject("Command").getJSONObject("RequestData").put("AbsoluteUrl", (Object)absoluteUrl);
            }
        }
        catch (final Exception e) {
            AndroidCommandUtil.logger.log(Level.INFO, "Exception while adding absolute url to Android Install command ", e);
        }
        return commandJson;
    }
    
    static {
        AndroidCommandUtil.logger = Logger.getLogger("MDMConfigLogger");
        AndroidCommandUtil.androidCommandUtil = null;
    }
}
