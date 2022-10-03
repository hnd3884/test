package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.WebClipsPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2WebClipsPayload implements DO2Payload
{
    private Logger logger;
    private String tableName;
    
    public DO2WebClipsPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.tableName = null;
        this.tableName = "WebClipPolicies";
    }
    
    public DO2WebClipsPayload(final String tableName) {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.tableName = null;
        this.tableName = tableName;
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        WebClipsPayload[] payloadArray = null;
        try {
            if (dataObject != null && !dataObject.isEmpty() && dataObject.containsTable(this.tableName)) {
                final Iterator iterator = dataObject.getRows(this.tableName);
                final int webClipsSize = dataObject.size(this.tableName);
                payloadArray = new WebClipsPayload[webClipsSize];
                int count = 0;
                while (iterator.hasNext()) {
                    final Row payloadRow = iterator.next();
                    final String labelName = (String)payloadRow.get("WEBCLIP_LABEL");
                    final String url = (String)payloadRow.get("WEBCLIP_URL");
                    String iconFileName = (String)payloadRow.get("ICON_FILE_NAME");
                    final Boolean isRemoval = (Boolean)payloadRow.get("IS_REMOVAL");
                    final Boolean isPreComposed = (Boolean)payloadRow.get("USE_PRECOMP_ICON");
                    final Boolean isFullScreen = (Boolean)payloadRow.get("ALLOW_FULL_SCREEN");
                    final WebClipsPayload webclipsPayload = new WebClipsPayload(1, "MDM", "com.mdm.mobiledevice.webclips", "WebClips Profile Configuration");
                    if (labelName != null && !labelName.equalsIgnoreCase("--")) {
                        webclipsPayload.setLabel(labelName);
                    }
                    if (url != null && !url.equalsIgnoreCase("--")) {
                        webclipsPayload.setURL(url);
                    }
                    if (iconFileName != null && !iconFileName.equalsIgnoreCase("")) {
                        iconFileName = MDMMetaDataUtil.getInstance().getClientDataParentDir() + iconFileName;
                        iconFileName = iconFileName.replaceAll("/", Matcher.quoteReplacement(File.separator));
                        webclipsPayload.setIcon(iconFileName);
                    }
                    webclipsPayload.setIsRemovable(isRemoval);
                    webclipsPayload.setIsPrecomposed(isPreComposed);
                    webclipsPayload.setIsFullScreen(isFullScreen);
                    payloadArray[count++] = webclipsPayload;
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in webclip payload", exp);
        }
        return payloadArray;
    }
}
