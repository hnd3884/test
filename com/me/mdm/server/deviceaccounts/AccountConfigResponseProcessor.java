package com.me.mdm.server.deviceaccounts;

import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.mdm.server.adep.DeviceConfiguredCommandHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class AccountConfigResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    private static final Logger LOGGER;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        try {
            final String strData = (String)params.get("strData");
            final Long resourceID = JSONUtil.optLongForUVH(params, "resourceId", Long.valueOf(-1L));
            DeviceConfiguredCommandHandler.getInstance().addDeviceConfiguredCommand(resourceID);
            MDMDBUtil.addOrUpdateAndPersist("AppleDeviceConfigStatus", new Object[][] { { "RESOURCE_ID", resourceID }, { "STATUS", 1 } });
            final AccountDetailsHandler handler = new AccountDetailsHandler();
            if (!strData.contains("Error")) {
                handler.addOrUpdateAccountConfigToResource(null, resourceID, 1);
            }
            else {
                handler.addOrUpdateAccountConfigToResource(null, resourceID, 2);
            }
        }
        catch (final JSONException e) {
            AccountConfigResponseProcessor.LOGGER.log(Level.SEVERE, "Exception in parsing the response", (Throwable)e);
        }
        catch (final Exception e2) {
            AccountConfigResponseProcessor.LOGGER.log(Level.SEVERE, "Exception while processing ios restriction", e2);
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
