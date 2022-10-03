package com.adventnet.db.summaryserver.summary.adapter.mssql;

import com.adventnet.db.summaryserver.summary.util.SQLGeneratorForSS;
import com.adventnet.db.summaryserver.summary.adapter.Synchronization;

public class SSMssqlSQLGenerator implements Synchronization
{
    @Override
    public void createStagingTable(final String table) throws Exception {
        SQLGeneratorForSS.getInstance().createMssqlStagingTable(table);
    }
    
    @Override
    public void copyToStagingTable(final String table, final String filePath) throws Exception {
    }
    
    @Override
    public void upsertToOriginalTable(final String table, final String tempTable) throws Exception {
        SQLGeneratorForSS.getInstance().upsertMssqlOriginalTable(table, tempTable);
    }
}
