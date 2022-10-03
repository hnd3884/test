package com.adventnet.persistence.interceptor;

import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import java.util.List;
import com.adventnet.ds.query.Table;
import java.util.HashSet;
import java.util.Set;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import java.util.Map;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.Criteria;

public class DeletePersistenceRequest extends PersistenceRequest
{
    Criteria criteria;
    DeleteQuery query;
    private Map contextInfo;
    
    public DeletePersistenceRequest(final Criteria criteria) {
        this.criteria = criteria;
    }
    
    public DeletePersistenceRequest(final DeleteQuery query) {
        this.query = query;
    }
    
    @Override
    public int getOperationType() {
        return 602;
    }
    
    public Criteria getCriteria() {
        return this.criteria;
    }
    
    public DeleteQuery getQuery() {
        return this.query;
    }
    
    public void setContextInfo(final Object key, final Object value) {
        if (this.contextInfo == null) {
            this.contextInfo = new HashMap();
        }
        this.contextInfo.put(key, value);
    }
    
    public Object getContextInfo(final Object key) {
        return (this.contextInfo == null) ? null : this.contextInfo.get(key);
    }
    
    public Map getAllContextInfo() {
        return this.contextInfo;
    }
    
    private static String getTableName(final Criteria cr) {
        String tabName = null;
        if (cr.getLeftCriteria() != null && cr.getRightCriteria() != null) {
            tabName = getTableName(cr.getLeftCriteria());
            final String rightTabName = getTableName(cr.getRightCriteria());
            if (!tabName.equals(rightTabName)) {
                throw new IllegalArgumentException("The Criteria specified has conditions from different tables including " + tabName + " and " + rightTabName);
            }
        }
        else {
            final Column col = cr.getColumn();
            if (col == null) {
                throw new IllegalArgumentException("Column is null in criteria : " + cr);
            }
            tabName = getTableName(col);
        }
        return tabName;
    }
    
    private static String getTableName(Column column) {
        String tabName = null;
        while (column.getColumn() != null) {
            column = column.getColumn();
        }
        tabName = column.getTableAlias();
        return tabName;
    }
    
    @Override
    public Set<String> getTableList() throws DataAccessException {
        final Set<String> tables = new HashSet<String>();
        if (this.criteria != null) {
            tables.add(getTableName(this.criteria));
        }
        else if (this.query != null) {
            final List<Table> tablelist = this.query.getTableList();
            for (final Table table : tablelist) {
                tables.add(table.getTableName());
            }
        }
        return tables;
    }
}
