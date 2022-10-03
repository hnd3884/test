package com.adventnet.db.archive;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Table;
import java.util.Collection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ArchiveAdapter
{
    void moveArchivedTable(final List<String> p0) throws Exception;
    
    void cleanUpArchiveTables(final List<String> p0) throws Exception;
    
    void setStorageAdapter(final StorageAdapter p0) throws Exception;
    
    boolean isArchiveNotificationEnabled(final Long p0) throws Exception;
    
    int startArchive(final ArchivePolicyInfo p0) throws Exception;
    
    boolean isArchiveRunning(final String p0);
    
    boolean isArchiveFailedTable(final SQLException p0, final String p1);
    
    void restoreUnArchivedInvisibleTable(final String p0, final Connection p1, final SQLException p2);
    
    void restoreUnArchivedInvisibleTable(final Collection<Table> p0, final Connection p1, final SQLException p2);
    
    void restoreFromArchive(final String p0, final Criteria p1) throws Exception;
    
    void deleteArchiveTable(final List<String> p0) throws Exception;
    
    DataSet getArchiveData(final String p0, final Criteria p1, final Connection p2) throws Exception;
    
    DataSet getArchiveData(final SelectQuery p0, final Connection p1) throws Exception;
}
