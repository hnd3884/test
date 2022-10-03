package com.me.mdm.server.migration;

import com.adventnet.sym.server.mdm.chrome.payload.ChromeCommandPayload;
import com.adventnet.sym.server.mdm.chrome.payload.ChromePayloadHandler;
import com.me.mdm.server.windows.profile.payload.WindowsPayloadHandler;
import com.adventnet.sym.server.mdm.android.payload.AndroidCommandPayload;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayloadHandler;
import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.adventnet.sym.server.mdm.ios.payload.AppsPayload;
import com.adventnet.sym.server.mdm.ios.payload.MacAccountConfigPayload;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.regex.Matcher;

public class InstallProfileRegeneration extends MigrationFileRegeneration
{
    @Override
    public String generateFile(final Matcher installProfileMatcher) throws Exception {
        String payloadData = null;
        final Long customerId = Long.parseLong(installProfileMatcher.group(1));
        final Long collectionId = Long.parseLong(installProfileMatcher.group(2));
        final DataObject dataObject = this.getProfileDetailsQuery(collectionId);
        if (dataObject != null) {
            final Row profileRow = dataObject.getFirstRow("Profile");
            final Integer platformType = (Integer)profileRow.get("PLATFORM_TYPE");
            final Integer profileType = (Integer)profileRow.get("PROFILE_TYPE");
            InstallProfileRegeneration.LOGGER.log(Level.INFO, "Install profile regeneration Collection Id : {0} Platform Type: {1} profile type: {2}", new Object[] { collectionId, platformType, profileType });
            if (platformType == 1 || platformType == 6 || platformType == 7) {
                payloadData = this.getIOSInstallProfileGeneration(profileType, collectionId);
            }
            else if (platformType == 2) {
                Long appId = null;
                if (profileType == 2) {
                    final Row appRow = dataObject.getFirstRow("MdAppDetails");
                    appId = (Long)appRow.get("APP_ID");
                }
                payloadData = this.getAndroidInstallProfileGeneration(profileType, collectionId, appId);
            }
            else if (platformType == 3) {
                payloadData = this.getWindowsInstallProfileGeneration(profileType, collectionId);
            }
            else if (platformType == 4) {
                Long appId = null;
                if (profileType == 2) {
                    final Row appRow = dataObject.getFirstRow("MdAppDetails");
                    appId = (Long)appRow.get("APP_ID");
                }
                payloadData = this.getChromeInstallProfileGeneration(profileType, collectionId, appId);
            }
        }
        return payloadData;
    }
    
    private String getIOSInstallProfileGeneration(final int profileType, final Long collectionId) throws Exception {
        String payloadData = null;
        final PayloadHandler payloadHdlr = PayloadHandler.getInstance();
        if (profileType == 6) {
            final MacAccountConfigPayload macAccountConfigPayload = payloadHdlr.generateMacAccountConfigPayload(collectionId);
            payloadData = macAccountConfigPayload.toString();
        }
        else if (profileType == 2 || profileType == 7) {
            final AppsPayload commandPayload = payloadHdlr.generateInstallAppPayload(collectionId);
            payloadData = commandPayload.toString();
        }
        else if (profileType == 10) {
            final IOSCommandPayload commandPayload2 = payloadHdlr.generateInstallIosMultiAppConfigCommand(collectionId);
            payloadData = commandPayload2.getPayloadDict().toXMLPropertyList();
        }
        else {
            final IOSCommandPayload commandPayload2 = payloadHdlr.generateIOSInstallCommandPayload(collectionId);
            payloadData = commandPayload2.getPayloadDict().toXMLPropertyList();
        }
        return payloadData;
    }
    
    private String getAndroidInstallProfileGeneration(final int profileType, final Long collectionId, final Long appId) throws Exception {
        String payloadData = null;
        final AndroidPayloadHandler payloadHdlr = AndroidPayloadHandler.getInstance();
        AndroidCommandPayload commandPayload = null;
        if (profileType == 10) {
            commandPayload = payloadHdlr.generateAppConfigPayload(collectionId, "InstallApplicationConfiguration");
        }
        else if (profileType == 2) {
            commandPayload = payloadHdlr.createApplicationCommand(collectionId, appId, "InstallApplication");
            commandPayload.setScope(-1);
        }
        else {
            commandPayload = payloadHdlr.generateInstallProfilePayload(collectionId);
        }
        payloadData = commandPayload.getPayloadJSON().toString();
        return payloadData;
    }
    
    private String getWindowsInstallProfileGeneration(final int profileType, final Long collectionId) throws Exception {
        String payloadData = null;
        final WindowsPayloadHandler payloadHandler = WindowsPayloadHandler.getInstance();
        if (profileType == 2) {
            payloadData = this.getWindowsAppPayloads(collectionId, "InstallApplication");
        }
        else {
            payloadData = payloadHandler.getWindowsInstallProfilePayloadData(collectionId);
        }
        return payloadData;
    }
    
    private String getChromeInstallProfileGeneration(final int profileType, final Long collectionId, final Long appId) throws Exception {
        String payloadData = null;
        final ChromePayloadHandler payloadHandler = ChromePayloadHandler.getInstance();
        ChromeCommandPayload commandPayload = null;
        if (profileType == 2) {
            commandPayload = payloadHandler.createApplicationCommand(collectionId, appId, "InstallApplication");
            commandPayload.setScope(-1);
        }
        else {
            commandPayload = payloadHandler.generateInstallProfilePayload(collectionId);
        }
        payloadData = commandPayload.getPayloadJSON().toString();
        return payloadData;
    }
}
