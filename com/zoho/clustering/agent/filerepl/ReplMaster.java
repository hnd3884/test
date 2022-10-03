package com.zoho.clustering.agent.filerepl;

import com.zoho.clustering.util.FileUtil;
import com.zoho.clustering.filerepl.SnapshotUtil;
import com.zoho.clustering.filerepl.event.EventLogger;
import com.zoho.clustering.filerepl.DirectoryList;
import java.util.logging.Logger;

public class ReplMaster
{
    private static Logger logger;
    private DirectoryList directoryList;
    private EventLogger eventLogger;
    private SnapshotUtil snapshotUtil;
    
    public static Logger logger() {
        return ReplMaster.logger;
    }
    
    public ReplMaster(final DirectoryList directoryList, final String snapshotDir, final EventLogger eventLogger) {
        this.directoryList = directoryList;
        this.eventLogger = eventLogger;
        this.snapshotUtil = new SnapshotUtil(directoryList, snapshotDir);
        FileUtil.assertOrCreateDir(snapshotDir);
    }
    
    public DirectoryList getDirectoryList() {
        return this.directoryList;
    }
    
    public SnapshotUtil getSnapshotUtil() {
        return this.snapshotUtil;
    }
    
    public EventLogger getEventLogger() {
        return this.eventLogger;
    }
    
    public String takeSnapshot(final boolean resetLog) {
        if (resetLog) {
            this.eventLogger.resetLog();
        }
        return this.snapshotUtil.takeSnapshot();
    }
    
    static {
        ReplMaster.logger = Logger.getLogger(ReplMaster.class.getName());
    }
}
