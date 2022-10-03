package com.adventnet.db.archive;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.Criteria;
import java.util.List;

public interface StorageAdapter
{
    void moveArchiveTables(final List<String> p0);
    
    void cleanUpTables(final List<String> p0);
    
    DataSet getArchiveData(final String p0, final Criteria p1) throws Exception;
    
    DataSet getArchiveData(final SelectQuery p0) throws Exception;
}
