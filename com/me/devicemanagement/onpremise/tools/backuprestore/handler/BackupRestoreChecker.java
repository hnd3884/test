package com.me.devicemanagement.onpremise.tools.backuprestore.handler;

import com.adventnet.db.adapter.RestoreResult;
import java.util.logging.Level;
import com.adventnet.db.adapter.BackupResult;
import java.util.logging.Logger;
import com.adventnet.db.adapter.BackupRestoreSanityChecker;

public class BackupRestoreChecker implements BackupRestoreSanityChecker
{
    private static final Logger LOGGER;
    
    public boolean checkBackup(final BackupResult backupResult) {
        BackupRestoreChecker.LOGGER.log(Level.INFO, "Inside checkBackup() method");
        return true;
    }
    
    public boolean checkRestore(final RestoreResult restoreResult) {
        BackupRestoreChecker.LOGGER.log(Level.INFO, "Inside checkRestore() method");
        return true;
    }
    
    static {
        LOGGER = Logger.getLogger("ScheduleDBBackup");
    }
}
