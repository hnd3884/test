package com.adventnet.db.archive;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.ArchiveTable;
import java.util.Map;
import java.util.List;

public interface ArchiveNotificationHandler
{
    void currentRunningPolicy(final ArchivePolicyInfo p0);
    
    void tableNamesToBeArchived(final List<String> p0);
    
    void archiveMap(final Map<String, ArchiveTable> p0);
    
    void startedArchiving(final boolean p0);
    
    void processTable(final String p0);
    
    void finishedTable(final String p0);
    
    void completedArchiving(final boolean p0, final int p1, final DataObject p2);
}
