package com.me.mdm.server.migration;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.regex.Matcher;

public class IOSAppConfigurationRegeneration extends MigrationFileRegeneration
{
    @Override
    public String generateFile(final Matcher appConfigMatcher) throws Exception {
        String payloadData = null;
        final Long customerId = Long.parseLong(appConfigMatcher.group(1));
        final Long collectionId = Long.parseLong(appConfigMatcher.group(2));
        final DataObject dataObject = this.getProfileDetailsQuery(collectionId);
        if (dataObject != null) {
            final Row profileRow = dataObject.getFirstRow("Profile");
            final Integer platformType = (Integer)profileRow.get("PLATFORM_TYPE");
            final Integer profileType = (Integer)profileRow.get("PROFILE_TYPE");
            final Row appRow = dataObject.getFirstRow("MdAppDetails");
            final String appIdentifier = (String)appRow.get("IDENTIFIER");
            final Long appId = (Long)appRow.get("APP_ID");
            final Long configDataItemId = (Long)DBUtil.getValueFromDB("InstallAppPolicy", "APP_ID", (Object)appId, "CONFIG_DATA_ITEM_ID");
            IOSAppConfigurationRegeneration.LOGGER.log(Level.INFO, "IOS App Configuration regeneration Collection Id : {0} Platform Type: {1} profile Type : {2} ", new Object[] { collectionId, platformType, profileType });
            if ((platformType == 1 || platformType == 6 || platformType == 7) && (profileType == 2 || profileType == 7)) {
                final PayloadHandler payloadHdlr = PayloadHandler.getInstance();
                final IOSCommandPayload commandPayload = payloadHdlr.generateAppConfigCommand(collectionId, appIdentifier, configDataItemId, appId);
                if (commandPayload != null) {
                    payloadData = commandPayload.getPayloadDict().toXMLPropertyList();
                }
            }
        }
        return payloadData;
    }
}
