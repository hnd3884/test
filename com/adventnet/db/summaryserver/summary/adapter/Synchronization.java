package com.adventnet.db.summaryserver.summary.adapter;

public interface Synchronization
{
    void createStagingTable(final String p0) throws Exception;
    
    void copyToStagingTable(final String p0, final String p1) throws Exception;
    
    void upsertToOriginalTable(final String p0, final String p1) throws Exception;
}
