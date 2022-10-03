package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import com.adventnet.sym.server.mdm.android.payload.AndroidWebClipsPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;

public class DO2AndroidWebClipsPayload implements DO2AndroidPayload
{
    private String tableName;
    
    public DO2AndroidWebClipsPayload() {
        this.tableName = null;
        this.tableName = "WebClipPolicies";
    }
    
    public DO2AndroidWebClipsPayload(final String tableName) {
        this.tableName = null;
        this.tableName = tableName;
    }
    
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidWebClipsPayload payload = null;
        try {
            if (dataObject != null) {
                final Iterator iterator = dataObject.getRows(this.tableName);
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String labelName = (String)payloadRow.get("WEBCLIP_LABEL");
                    final String url = (String)payloadRow.get("WEBCLIP_URL");
                    String iconFileName = (String)payloadRow.get("ICON_FILE_NAME");
                    final Boolean isFullScreen = (Boolean)payloadRow.get("ALLOW_FULL_SCREEN");
                    final Integer refreshMode = (Integer)payloadRow.get("REFRESH_MODE");
                    final Boolean createHomescreenShortcut = (Boolean)payloadRow.get("CREATE_HOMESCREEN_SHORTCUT");
                    final Boolean allowClearCookie = (Boolean)payloadRow.get("ALLOW_CLEAR_COOKIE");
                    final Integer screenOrientation = (Integer)payloadRow.get("SCREEN_ORIENTATION_OPTION");
                    final Boolean isSitePermissionAllowed = (Boolean)payloadRow.get("ALLOW_SITE_PERMISSION");
                    payload = new AndroidWebClipsPayload("1.0", "com.mdm.mobiledevice.webclips", "WebClips Configuration");
                    if (labelName != null && !labelName.equalsIgnoreCase("--")) {
                        payload.setLabel(labelName);
                    }
                    if (url != null && !url.equalsIgnoreCase("--")) {
                        payload.setURL(url);
                    }
                    if (iconFileName != null && !iconFileName.equalsIgnoreCase("")) {
                        iconFileName = iconFileName.replaceAll("\\\\", "/");
                        payload.setIcon(iconFileName);
                    }
                    payload.setIsFullScreen(isFullScreen);
                    payload.setRefreshMode(refreshMode);
                    payload.createHomescreenShortcut(createHomescreenShortcut);
                    payload.setIsSitePermissionAllowed(isSitePermissionAllowed);
                    if (isFullScreen) {
                        payload.setScreenOrientationOption(screenOrientation);
                        payload.allowClearCookie(allowClearCookie);
                        payload.setWebClipSettings();
                    }
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return payload;
    }
}
