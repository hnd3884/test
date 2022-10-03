package com.me.mdm.onpremise.server.startup;

import com.me.tools.zcutil.METrack;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.zoho.mickey.startup.ServerStartupHooks;

public class MDMPServerStartupHook implements ServerStartupHooks
{
    private Logger logger;
    
    public MDMPServerStartupHook() {
        this.logger = Logger.getLogger("METrackLog");
    }
    
    public void preStartServer() {
    }
    
    public void postStartServer() {
        try {
            this.logger.log(Level.INFO, "==== START ME TRACK POSTING AFTER STARTUP   ===== ");
            METrack.ZCScheduler();
            this.logger.log(Level.INFO, "==== END ME TRACK POSTING AFTER STARTUP ===== ");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Invoking METrack post on start failed...!", e);
        }
    }
}
