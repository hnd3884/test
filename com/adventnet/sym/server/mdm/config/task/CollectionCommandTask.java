package com.adventnet.sym.server.mdm.config.task;

import java.util.logging.Level;
import java.util.logging.Logger;

public interface CollectionCommandTask
{
    public static final Logger logger = Logger.getLogger("MDMProfileDistributionLog");
    
    void installProfile(final CollectionCommandTaskData p0) throws Exception;
    
    void uninstallProfile(final CollectionCommandTaskData p0) throws Exception;
    
    void installApp(final CollectionCommandTaskData p0) throws Exception;
    
    void uninstallApp(final CollectionCommandTaskData p0) throws Exception;
    
    void blackListApp(final CollectionCommandTaskData p0) throws Exception;
    
    void removeBlacklisting(final CollectionCommandTaskData p0) throws Exception;
    
    void installDataUsageProfile(final CollectionCommandTaskData p0) throws Exception;
    
    void removeDataUsageProfile(final CollectionCommandTaskData p0) throws Exception;
    
    void installAppConfiguration(final CollectionCommandTaskData p0) throws Exception;
    
    void removeAppConfiguration(final CollectionCommandTaskData p0) throws Exception;
    
    void installAnnouncement(final CollectionCommandTaskData p0) throws Exception;
    
    void installScheduleConfiguration(final CollectionCommandTaskData p0) throws Exception;
    
    void removeScheduleConfiguration(final CollectionCommandTaskData p0) throws Exception;
    
    default void installAppUpdatePolicy(final CollectionCommandTaskData collectionCommandTaskData) {
        CollectionCommandTask.logger.log(Level.INFO, "Install app update policy called with props {0}", new Object[] { collectionCommandTaskData.toString() });
    }
    
    default void removeAppUpdatePolicy(final CollectionCommandTaskData collectionCommandTaskData) {
        CollectionCommandTask.logger.log(Level.INFO, "Remove app update policy called with props {0}", new Object[] { collectionCommandTaskData.toString() });
    }
}
