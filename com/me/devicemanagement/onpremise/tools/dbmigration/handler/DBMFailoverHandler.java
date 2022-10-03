package com.me.devicemanagement.onpremise.tools.dbmigration.handler;

import java.util.logging.Level;
import com.adventnet.db.migration.util.DBMigrationUtil;
import java.util.Locale;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import com.adventnet.persistence.fos.FOS;
import java.util.logging.Logger;
import com.adventnet.db.migration.handler.DBMigrationPrePostHandler;

public class DBMFailoverHandler implements DBMigrationPrePostHandler
{
    private static final Logger LOGGER;
    
    public void preHandle() throws Exception {
        if (FOS.isEnabled()) {
            final FOS fos = new FOS();
            fos.initialize();
            final String ip = fos.getOtherNode();
            final Boolean isFailoverServerUp = FOS.standAloneMasterHealthCheck();
            if (isFailoverServerUp) {
                final String message = BackupRestoreUtil.getString("desktopcentral.tools.changedb.info.fos_enabled", new Object[] { ip }, (Locale)null);
                DBMigrationUtil.getHandlerFactory().getProgressNotifier().printMessage(message);
                DBMFailoverHandler.LOGGER.log(Level.SEVERE, "In Fos Check, Failover Server is Running");
                throw new Exception("Failover server seems to be running");
            }
            if (!new BackupRestoreUtil().isOtherServerInFosReachable()) {
                final String message = BackupRestoreUtil.getString("desktopcentral.tools.backup.error.failover_peer_not_reachable.title", new Object[] { ip }, (Locale)null);
                DBMigrationUtil.getHandlerFactory().getProgressNotifier().printMessage(message);
                DBMFailoverHandler.LOGGER.log(Level.SEVERE, "In Fos Check, Failover Server could not be reached");
                throw new Exception("Failover server is not reachable from Main server");
            }
        }
    }
    
    public void postHandle() throws Exception {
        if (FOS.isEnabled()) {
            new BackupRestoreUtil().copyToPeer();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(DBMFailoverHandler.class.getName());
    }
}
