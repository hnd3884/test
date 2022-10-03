package com.adventnet.ds.query;

import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

public class UpdateQueryImpl implements UpdateQuery
{
    private String tableName;
    private Criteria criteria;
    private Map<Column, Object> updateColumns;
    private ArrayList<Table> tableList;
    private ArrayList<Join> joins;
    
    public UpdateQueryImpl(final String tableName) {
        this.updateColumns = new HashMap<Column, Object>();
        this.tableList = new ArrayList<Table>();
        this.joins = new ArrayList<Join>();
        this.tableName = tableName;
        this.tableList.add(new Table(tableName));
    }
    
    @Override
    public String getTableName() {
        return this.tableName;
    }
    
    @Override
    public Criteria getCriteria() {
        return this.criteria;
    }
    
    @Override
    public void setCriteria(final Criteria criteria) {
        this.criteria = criteria;
    }
    
    @Override
    public Map getUpdateColumns() {
        return this.updateColumns;
    }
    
    @Override
    public void setUpdateColumn(final String columnName, final Object value) {
        final Column column = Column.getColumn(this.tableName, columnName);
        this.updateColumns.put(column, value);
    }
    
    private String getJoinString() {
        final int joinsSize = this.joins.size();
        final StringBuilder overAllJoinBuffer = new StringBuilder();
        for (int i = 0; i < joinsSize; ++i) {
            if (i != 0) {
                overAllJoinBuffer.append(" , ");
            }
            overAllJoinBuffer.append(this.joins.get(i).toString());
        }
        return overAllJoinBuffer.toString();
    }
    
    @Override
    public void addJoin(final Join join) {
        if (join != null) {
            if (!this.checkTable(join.getBaseTableAlias())) {
                throw new IllegalArgumentException("Base table " + join.getBaseTableAlias() + " specified in this join is not already added to this query");
            }
            if (this.checkTable(join.getReferencedTableAlias())) {
                throw new IllegalArgumentException("Referenced table " + join.getReferencedTableAlias() + " is already specified in this query. So it can't be added again");
            }
            final Table referencedTable = join.getReferencedTable();
            Table toAddTable = new Table(join.getReferencedTableName(), join.getReferencedTableAlias());
            if (referencedTable instanceof DerivedTable) {
                toAddTable = new DerivedTable(referencedTable.getTableAlias(), ((DerivedTable)referencedTable).getSubQuery());
            }
            this.joins.add(join);
            this.tableList.add(toAddTable);
        }
    }
    
    private boolean checkTable(final String tableAlias) {
        for (int size = this.tableList.size(), i = 0; i < size; ++i) {
            if (tableAlias.equals(this.tableList.get(i).getTableAlias())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<Join> getJoins() {
        return this.joins;
    }
    
    @Override
    public List<Table> getTableList() {
        return this.tableList;
    }
    
    @Override
    public Object clone() {
        UpdateQueryImpl query = null;
        try {
            query = (UpdateQueryImpl)super.clone();
        }
        catch (final CloneNotSupportedException excp) {
            return null;
        }
        query.tableName = this.tableName;
        query.updateColumns = new HashMap<Column, Object>();
        final Set<Map.Entry<Column, Object>> entryset = this.updateColumns.entrySet();
        for (final Map.Entry entry : entryset) {
            final Object clonedColumn = entry.getKey().clone();
            Object value = entry.getValue();
            if (value instanceof Column) {
                value = ((Column)value).clone();
            }
            if (value instanceof DerivedColumn) {
                value = ((DerivedColumn)value).clone();
            }
            query.updateColumns.put((Column)clonedColumn, value);
        }
        if (this.criteria != null) {
            query.criteria = (Criteria)this.criteria.clone();
        }
        query.tableList = (ArrayList)this.tableList.clone();
        query.joins = (ArrayList)this.joins.clone();
        return query;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("UpdateQuery Object:");
        buf.append("\n\tTableName=" + this.tableName);
        buf.append("\n\tColumnVsNewValues=");
        buf.append(this.updateColumns);
        buf.append("\n\tCriteria=");
        buf.append(String.valueOf(this.criteria));
        buf.append("\n\tTable List=");
        buf.append(String.valueOf(this.tableList));
        buf.append("\n\tJoins= " + this.getJoinString());
        return buf.toString();
    }
}
