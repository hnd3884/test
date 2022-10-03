package com.adventnet.db.summaryserver.summary.adapter.postgres;

import com.adventnet.db.summaryserver.summary.util.SQLGeneratorForSS;
import com.adventnet.db.summaryserver.summary.adapter.Synchronization;

public class SSPostgresSQLGenerator implements Synchronization
{
    @Override
    public void createStagingTable(final String table) throws Exception {
        SQLGeneratorForSS.getInstance().createPostgresStagingTable(table);
    }
    
    @Override
    public void copyToStagingTable(final String table, final String filePath) throws Exception {
        SQLGeneratorForSS.getInstance().copyPostgresStagingTable(table, filePath);
    }
    
    @Override
    public void upsertToOriginalTable(final String table, final String tempTable) throws Exception {
        SQLGeneratorForSS.getInstance().upsertPostgresOriginalTable(table, tempTable);
    }
}
