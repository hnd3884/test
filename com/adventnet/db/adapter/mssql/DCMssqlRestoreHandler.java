package com.adventnet.db.adapter.mssql;

import java.sql.Connection;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.ScheduleDBBackupUtil;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import com.adventnet.db.adapter.RestoreDBParams;
import java.util.logging.Logger;

public class DCMssqlRestoreHandler extends MssqlRestoreHandler
{
    private static final Logger LOGGER;
    
    protected void postRestoreDB(final RestoreDBParams rdbp) {
        super.postRestoreDB(rdbp);
        rdbp.setRestoreBackupContentType((BackupRestoreConfigurations.BACKUP_CONTENT_TYPE)null);
    }
    
    protected boolean hasPermissionForRestore() {
        boolean restorePermission = true;
        try {
            if (!this.dbAdapter.isBundledDB()) {
                try (final Connection conn = this.dataSource.getConnection()) {
                    restorePermission = ScheduleDBBackupUtil.isMssqlDBPermissionsAvailableToTakeBakBackup(this.dbAdapter.getDBName(conn), conn);
                }
            }
        }
        catch (final Exception e) {
            DCMssqlRestoreHandler.LOGGER.log(Level.SEVERE, "Could not check for the restore permission. Assuming there is no permission for restore.");
            return false;
        }
        DCMssqlRestoreHandler.LOGGER.log(Level.INFO, "Has permission for restoration : " + restorePermission);
        return restorePermission;
    }
    
    static {
        LOGGER = Logger.getLogger(DCMssqlRestoreHandler.class.getName());
    }
}
