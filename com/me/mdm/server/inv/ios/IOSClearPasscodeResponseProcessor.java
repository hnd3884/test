package com.me.mdm.server.inv.ios;

import org.json.JSONException;
import com.me.mdm.server.profiles.ios.IOSPasscodeRestrictionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSClearPasscodeResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        Logger.getLogger("MDMLogger").log(Level.INFO, "Inside clear passcode handling:{0}", new Object[] { params });
        try {
            final String commandStatus = params.optString("strStatus");
            final Long resourceId = params.getLong("resourceId");
            if (commandStatus.equalsIgnoreCase("Acknowledged")) {
                new IOSPasscodeRestrictionHandler().handleClearPasscodeForPasscodeRestriction(resourceId);
            }
        }
        catch (final JSONException e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in clear passcode response processor", (Throwable)e);
        }
        return null;
    }
}
