package com.adventnet.db.summaryserver.summary.sync;

import com.adventnet.db.summaryserver.summary.adapter.mssql.SSMssqlSQLGenerator;
import com.adventnet.db.summaryserver.summary.adapter.postgres.SSPostgresSQLGenerator;
import com.adventnet.db.summaryserver.summary.adapter.Synchronization;

public class SSSyncHandler
{
    private static SSSyncHandler syncImpl;
    private static Synchronization synchronization;
    
    public static SSSyncHandler getInstance() {
        if (SSSyncHandler.syncImpl == null) {
            SSSyncHandler.syncImpl = new SSSyncHandler();
        }
        return SSSyncHandler.syncImpl;
    }
    
    public Synchronization getSyncImpl(final String dbName) {
        if (SSSyncHandler.synchronization == null) {
            if (dbName.equalsIgnoreCase("postgres")) {
                SSSyncHandler.synchronization = new SSPostgresSQLGenerator();
            }
            else if (dbName.equalsIgnoreCase("mssql")) {
                SSSyncHandler.synchronization = new SSMssqlSQLGenerator();
            }
        }
        return SSSyncHandler.synchronization;
    }
    
    static {
        SSSyncHandler.syncImpl = null;
        SSSyncHandler.synchronization = null;
    }
}
