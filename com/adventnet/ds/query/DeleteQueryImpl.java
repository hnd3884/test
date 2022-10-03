package com.adventnet.ds.query;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.ArrayList;

public class DeleteQueryImpl implements DeleteQuery
{
    private int limit;
    private String tableName;
    private Criteria criteria;
    private ArrayList<SortColumn> sortColumns;
    private ArrayList<Table> tableList;
    private ArrayList<Join> joins;
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER;
    
    public DeleteQueryImpl(final String tableName) {
        this.limit = -1;
        this.sortColumns = new ArrayList<SortColumn>();
        this.tableList = new ArrayList<Table>();
        this.joins = new ArrayList<Join>();
        this.tableName = tableName;
        this.tableList.add(new Table(tableName));
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
    public void addSortColumn(final SortColumn sortColumn) {
        this.sortColumns.add(sortColumn);
    }
    
    @Override
    public void addSortColumn(final SortColumn sortColumn, final int index) {
        this.sortColumns.add(index, sortColumn);
    }
    
    @Override
    public boolean removeSortColumn(final SortColumn sortColumn) {
        return this.sortColumns.remove(sortColumn);
    }
    
    @Override
    public SortColumn removeSortColumn(final int sortIndex) {
        return this.sortColumns.remove(sortIndex);
    }
    
    @Override
    public void addSortColumns(final List<SortColumn> sortColumns) {
        this.sortColumns.addAll(sortColumns);
    }
    
    @Override
    public void addSortColumns(final List<SortColumn> sortColumns, final int index) {
        sortColumns.addAll(index, sortColumns);
    }
    
    @Override
    public List<SortColumn> getSortColumns() {
        return this.sortColumns;
    }
    
    @Override
    public void setLimit(final int numOfObjs) {
        this.limit = numOfObjs;
    }
    
    @Override
    public int getLimit() {
        return this.limit;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    @Override
    public String getTableName() {
        return this.tableName;
    }
    
    public Object clone() {
        DeleteQueryImpl query = null;
        try {
            query = (DeleteQueryImpl)super.clone();
        }
        catch (final CloneNotSupportedException excp) {
            return null;
        }
        query.tableName = this.tableName;
        query.tableList = (ArrayList)this.tableList.clone();
        if (this.criteria != null) {
            query.criteria = (Criteria)this.criteria.clone();
        }
        query.limit = this.limit;
        query.sortColumns = (ArrayList)this.sortColumns.clone();
        query.joins = (ArrayList)this.joins.clone();
        return query;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("DeleteQuery Object:");
        buf.append("\n\tTable=");
        buf.append(this.tableName);
        buf.append("\n\tTable List=");
        buf.append(String.valueOf(this.tableList));
        buf.append("\n\tCriteria=");
        buf.append(String.valueOf(this.criteria));
        buf.append("\n\tJoins= " + this.getJoinString());
        buf.append("\n\tLimit=");
        buf.append(this.limit);
        buf.append("\n\tOrder by columnNames=");
        buf.append(String.valueOf(this.sortColumns) + "\n");
        return buf.toString();
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
            final Table toAddTable = new Table(join.getReferencedTableName(), join.getReferencedTableAlias());
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
    
    static {
        LOGGER = Logger.getLogger(DeleteQueryImpl.class.getName());
    }
}
