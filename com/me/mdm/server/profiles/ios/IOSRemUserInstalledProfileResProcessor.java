package com.me.mdm.server.profiles.ios;

import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSRemUserInstalledProfileResProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    private static final Logger LOGGER;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        IOSRemUserInstalledProfileResProcessor.LOGGER.log(Level.INFO, "Remove user Installed profile response processor.Params:{0}", new Object[] { params });
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
