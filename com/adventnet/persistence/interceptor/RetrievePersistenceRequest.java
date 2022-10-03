package com.adventnet.persistence.interceptor;

import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.ds.query.Table;
import java.util.HashSet;
import java.util.Set;
import com.adventnet.ds.query.SelectQuery;

public class RetrievePersistenceRequest extends PersistenceRequest
{
    SelectQuery query;
    
    public RetrievePersistenceRequest(final SelectQuery query) {
        this.query = query;
    }
    
    @Override
    public int getOperationType() {
        return 603;
    }
    
    public SelectQuery getQuery() {
        return this.query;
    }
    
    @Override
    public Set<String> getTableList() throws DataAccessException {
        final Set<String> tableList = new HashSet<String>();
        for (final Table table : this.query.getTableList()) {
            tableList.add(table.getTableName());
        }
        return tableList;
    }
}
