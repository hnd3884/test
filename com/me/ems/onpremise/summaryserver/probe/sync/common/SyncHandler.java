package com.me.ems.onpremise.summaryserver.probe.sync.common;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncHandler
{
    private static Logger logger;
    
    public static void callDataSync(final int syncType, final int moduleId) {
        switch (syncType) {
            case 1: {
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
            default: {
                SyncHandler.logger.log(Level.INFO, "Inappropriate sync type called...");
                break;
            }
        }
    }
    
    static {
        SyncHandler.logger = Logger.getLogger("ProbeSyncLogger");
    }
}
