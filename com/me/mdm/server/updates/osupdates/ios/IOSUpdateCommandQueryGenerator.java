package com.me.mdm.server.updates.osupdates.ios;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.regex.Pattern;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.io.File;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.me.mdm.server.updates.osupdates.ExtendedOSDetailsDataHandler;
import com.me.mdm.server.updates.osupdates.OSUpdatesDataHandler;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.mdm.server.updates.osupdates.ResourceOSUpdateDataHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class IOSUpdateCommandQueryGenerator implements CommandQueryCreator
{
    Logger logger;
    
    public IOSUpdateCommandQueryGenerator() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        String query = null;
        try {
            final Long detectedUpdateID = new ResourceOSUpdateDataHandler().getLatestOSVersionUpdateIDForResource(new VersionChecker(), resourceID);
            if (detectedUpdateID != null) {
                final DataObject updateDetailsDO = new OSUpdatesDataHandler().getOSUpdateDetails(detectedUpdateID, new IOSUpdatesDetailsHandler());
                if (!updateDetailsDO.isEmpty()) {
                    final String prodKey = updateDetailsDO.getFirstRow("IOSUpdates").get("PRODUCT_KEY").toString();
                    final String clientDataParentDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
                    final String profileFullPath = clientDataParentDir + File.separator + deviceCommand.commandFilePath;
                    query = PayloadHandler.getInstance().readProfileFromFile(profileFullPath);
                    query = Pattern.compile("%os_prodkey%", 2).matcher(query).replaceAll(prodKey);
                }
            }
            if (query == null) {
                final IOSCommandPayload payload = PayloadHandler.getInstance().createCommandPayload("ScheduleOSUpdate");
                payload.setCommandUUID(deviceCommand.commandUUID, false);
                query = payload.toString();
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error  ", e);
        }
        return query;
    }
}
