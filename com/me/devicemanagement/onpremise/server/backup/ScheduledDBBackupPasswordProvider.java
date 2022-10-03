package com.me.devicemanagement.onpremise.server.backup;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.db.api.RelationalAPI;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DMBackupPasswordProvider;

public class ScheduledDBBackupPasswordProvider implements DMBackupPasswordProvider
{
    private static final Logger LOG;
    
    public String getPassword() {
        return this.getDBBackupInfo("BACKUP_PASSWORD");
    }
    
    public String getPasswordHint() {
        return this.getDBBackupInfo("BACKUP_PASSWORD_HINT");
    }
    
    private String getDBBackupInfo(final String col) {
        String colValue = null;
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = RelationalAPI.getInstance().getConnection();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DBBackupInfo"));
            query.addSelectColumn(Column.getColumn("DBBackupInfo", col));
            ds = RelationalAPI.getInstance().executeQuery((Query)query, conn);
            if (ds.next()) {
                colValue = ds.getAsString(col);
            }
        }
        catch (final Exception ex) {
            ScheduledDBBackupPasswordProvider.LOG.log(Level.WARNING, "Caught exception while getting password ", ex);
            try {
                if (conn != null) {
                    conn.close();
                }
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final SQLException e) {
                ScheduledDBBackupPasswordProvider.LOG.log(Level.WARNING, "Caught exception while getting password finally ", e);
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final SQLException e2) {
                ScheduledDBBackupPasswordProvider.LOG.log(Level.WARNING, "Caught exception while getting password finally ", e2);
            }
        }
        return colValue;
    }
    
    static {
        LOG = Logger.getLogger("ScheduleDBBackup");
    }
}
