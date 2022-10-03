package com.me.mdm.server.migration;

import com.adventnet.sym.server.mdm.chrome.payload.ChromeCommandPayload;
import com.adventnet.sym.server.mdm.chrome.payload.ChromePayloadHandler;
import com.me.mdm.server.windows.profile.payload.WindowsPayloadHandler;
import com.adventnet.sym.server.mdm.android.payload.AndroidCommandPayload;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayloadHandler;
import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.regex.Matcher;

public class RemoveProfileRegeneration extends MigrationFileRegeneration
{
    @Override
    public String generateFile(final Matcher matcher) throws Exception {
        String payloadData = null;
        final Long customerId = Long.parseLong(matcher.group(1));
        final Long collectionId = Long.parseLong(matcher.group(2));
        final DataObject dataObject = this.getProfileDetailsQuery(collectionId);
        if (dataObject != null) {
            final Row profileRow = dataObject.getFirstRow("Profile");
            final Integer platformType = (Integer)profileRow.get("PLATFORM_TYPE");
            final Integer profileType = (Integer)profileRow.get("PROFILE_TYPE");
            final String payloadIdentifier = (String)profileRow.get("PROFILE_PAYLOAD_IDENTIFIER");
            RemoveProfileRegeneration.LOGGER.log(Level.INFO, "Remove profile regeneration Collection Id : {0} Platform Type: {1} profile type: {2}", new Object[] { collectionId, platformType, profileType });
            if (platformType == 1 || platformType == 6 || platformType == 7) {
                String appIdentifier = null;
                if (profileType == 2 || profileType == 7) {
                    final Row appRow = dataObject.getFirstRow("MdAppDetails");
                    appIdentifier = (String)appRow.get("IDENTIFIER");
                }
                payloadData = this.getIOSRemoveProfileGeneration(profileType, collectionId, payloadIdentifier, appIdentifier);
            }
            else if (platformType == 2) {
                Long appId = null;
                if (profileType == 2) {
                    final Row appRow = dataObject.getFirstRow("MdAppDetails");
                    appId = (Long)appRow.get("APP_ID");
                }
                payloadData = this.getAndroidRemoveProfileGeneration(profileType, collectionId, appId, payloadIdentifier);
            }
            else if (platformType == 3) {
                payloadData = this.getWindowsRemoveProfileGeneration(profileType, collectionId);
            }
            else if (platformType == 4) {
                Long appId = null;
                if (profileType == 2) {
                    final Row appRow = dataObject.getFirstRow("MdAppDetails");
                    appId = (Long)appRow.get("APP_ID");
                }
                payloadData = this.getChromeRemoveProfileGeneration(profileType, collectionId, appId, payloadIdentifier);
            }
        }
        return payloadData;
    }
    
    private String getIOSRemoveProfileGeneration(final int profileType, final Long collectionId, final String payloadIdentifier, final String appIdentifier) throws Exception {
        String payloadData = null;
        final PayloadHandler payloadHdlr = PayloadHandler.getInstance();
        IOSCommandPayload commandPayload = null;
        if (profileType == 2 || profileType == 7) {
            commandPayload = payloadHdlr.createRemoveApplicationCommand(appIdentifier, collectionId);
        }
        else if (profileType == 10) {
            commandPayload = payloadHdlr.generateRemoveIosMultiAppConfigCommand(collectionId);
        }
        else {
            commandPayload = payloadHdlr.generateIOSRemoveCommonPayload(payloadIdentifier, collectionId);
        }
        if (commandPayload != null) {
            payloadData = commandPayload.getPayloadDict().toXMLPropertyList();
        }
        return payloadData;
    }
    
    private String getAndroidRemoveProfileGeneration(final int profileType, final Long collectionId, final Long appId, final String payloadIdentifier) throws Exception {
        String payloadData = null;
        final AndroidPayloadHandler payloadHdlr = AndroidPayloadHandler.getInstance();
        AndroidCommandPayload commandPayload = null;
        if (profileType == 10) {
            commandPayload = payloadHdlr.generateAppConfigPayload(collectionId, "RemoveApplicationConfiguration");
        }
        else if (profileType == 2) {
            commandPayload = payloadHdlr.createApplicationCommand(collectionId, appId, "RemoveApplication");
            commandPayload.setScope(-1);
        }
        else {
            commandPayload = payloadHdlr.generateRemoveProfilePayload(payloadIdentifier, collectionId);
        }
        payloadData = commandPayload.getPayloadJSON().toString();
        return payloadData;
    }
    
    private String getWindowsRemoveProfileGeneration(final int profileType, final Long collectionId) throws Exception {
        String payloadData = null;
        final WindowsPayloadHandler payloadHandler = WindowsPayloadHandler.getInstance();
        if (profileType == 2) {
            payloadData = this.getWindowsAppPayloads(collectionId, "RemoveApplication");
        }
        else {
            payloadData = payloadHandler.getWindowsRemoveProfilePayloadData(collectionId);
        }
        return payloadData;
    }
    
    private String getChromeRemoveProfileGeneration(final int profileType, final Long collectionId, final Long appId, final String payloadIdentifier) throws Exception {
        String payloadData = null;
        final ChromePayloadHandler payloadHandler = ChromePayloadHandler.getInstance();
        ChromeCommandPayload commandPayload = null;
        if (profileType == 2) {
            commandPayload = payloadHandler.createApplicationCommand(collectionId, appId, "RemoveApplication");
            commandPayload.setScope(-1);
        }
        else {
            commandPayload = payloadHandler.generateRemoveProfilePayload(payloadIdentifier, collectionId);
        }
        payloadData = commandPayload.getPayloadJSON().toString();
        return payloadData;
    }
}
