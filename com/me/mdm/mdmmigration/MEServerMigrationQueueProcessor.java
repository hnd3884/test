package com.me.mdm.mdmmigration;

import java.util.Map;
import java.util.HashMap;
import server.com.me.mdm.server.mdmmigration.MigrationServicesFacade;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class MEServerMigrationQueueProcessor extends DCQueueDataProcessor
{
    private static Logger logger;
    Logger queueLogger;
    String separator;
    
    public MEServerMigrationQueueProcessor() {
        this.queueLogger = Logger.getLogger("MDMQueueBriefLogger");
        this.separator = "\t";
    }
    
    public void processData(final DCQueueData qData) {
        final long sysTime = System.currentTimeMillis();
        this.queueLogger.log(Level.INFO, "Device tracking updates started{0}{1}{2}Time spent waiting in queue - {3}", new Object[] { this.separator, qData.fileName, this.separator, sysTime - qData.postTime });
        final MigrationServicesFacade migrationServicesFacade = new MigrationServicesFacade();
        final HashMap queueData = (HashMap)qData.queueData;
        final String type = queueData.get("type").toString();
        try {
            if (type.equalsIgnoreCase("FETCH_ALL")) {
                migrationServicesFacade.syncAllData(queueData);
            }
            else if (type.equalsIgnoreCase("FETCH_DEVICES")) {
                migrationServicesFacade.getAllDevices(queueData);
            }
            else if (type.equalsIgnoreCase("FETCH_USERS")) {
                migrationServicesFacade.getAllUsers(queueData);
            }
            else if (type.equalsIgnoreCase("FETCH_GROUPS")) {
                migrationServicesFacade.getAllGroups(queueData);
            }
            else if (type.equalsIgnoreCase("FETCH_APPS")) {
                migrationServicesFacade.getAllApps(queueData);
            }
            else if (type.equalsIgnoreCase("FETCH_PROFILES")) {
                migrationServicesFacade.getAllProfiles(queueData);
            }
        }
        catch (final Exception e) {
            MEServerMigrationQueueProcessor.logger.log(Level.SEVERE, "Exception in processing migration queue", e);
        }
    }
    
    static {
        MEServerMigrationQueueProcessor.logger = Logger.getLogger("MDMMigrationLogger");
    }
}
