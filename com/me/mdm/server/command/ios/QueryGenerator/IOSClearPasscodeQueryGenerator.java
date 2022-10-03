package com.me.mdm.server.command.ios.QueryGenerator;

import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import java.util.logging.Level;
import com.me.mdm.server.notification.PushNotificationHandler;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.command.CommandQueryCreator;

public class IOSClearPasscodeQueryGenerator implements CommandQueryCreator
{
    private final Logger logger;
    
    public IOSClearPasscodeQueryGenerator() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public String createCmdQuery(final DeviceCommand deviceCommand, final String strUDID, final Long resourceID, final HashMap requestMap) {
        String strQuery = null;
        try {
            final JSONObject json = new PrivacySettingsHandler().getPrivacySettingsJSON(resourceID);
            if ((int)json.get("disable_clear_passcode") == 0) {
                final JSONObject notifictionDetail = PushNotificationHandler.getInstance().getNotificationDetails(resourceID, 1);
                this.logger.log(Level.FINE, " device communication details {0}", notifictionDetail);
                final String unlock_token = (String)notifictionDetail.get("UNLOCK_TOKEN_ENCRYPTED");
                final IOSCommandPayload createRestrictionsCommand = PayloadHandler.getInstance().createClearPasscodeCommand(unlock_token);
                createRestrictionsCommand.setCommandUUID(deviceCommand.commandUUID, false);
                strQuery = createRestrictionsCommand.toString();
            }
            else {
                this.logger.log(Level.WARNING, "GenerateIOSQuery: No access to perform {0} on Resource {1}.. ", new Object[] { deviceCommand.commandUUID, resourceID });
                DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand.commandUUID, resourceID);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in clear passcode query generation.. ", ex);
        }
        return strQuery;
    }
}
