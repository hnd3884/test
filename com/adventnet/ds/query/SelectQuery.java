package com.adventnet.ds.query;

import java.util.Map;
import java.util.List;

public interface SelectQuery extends Query
{
    List<Column> getSelectColumns();
    
    void addSelectColumn(final Column p0);
    
    void addSelectColumn(final Column p0, final int p1);
    
    void addSelectColumns(final List p0);
    
    void addSelectColumns(final List p0, final int p1);
    
    boolean removeSelectColumn(final Column p0);
    
    Column removeSelectColumn(final int p0);
    
    Criteria getCriteria();
    
    void setCriteria(final Criteria p0);
    
    @Deprecated
    List getGroupByColumns();
    
    @Deprecated
    void addGroupByColumn(final Column p0);
    
    @Deprecated
    void addGroupByColumn(final Column p0, final int p1);
    
    @Deprecated
    boolean removeGroupByColumn(final Column p0);
    
    @Deprecated
    Column removeGroupByColumn(final int p0);
    
    @Deprecated
    void addGroupByColumns(final List p0);
    
    @Deprecated
    void addGroupByColumns(final List p0, final int p1);
    
    GroupByClause getGroupByClause();
    
    void setGroupByClause(final GroupByClause p0);
    
    void addSortColumn(final SortColumn p0);
    
    void addSortColumn(final SortColumn p0, final int p1);
    
    boolean removeSortColumn(final SortColumn p0);
    
    SortColumn removeSortColumn(final int p0);
    
    void addSortColumns(final List<SortColumn> p0);
    
    void addSortColumns(final List<SortColumn> p0, final int p1);
    
    List<SortColumn> getSortColumns();
    
    List<Table> getTableList();
    
    String getTableNameForTableAlias(final String p0);
    
    void addJoin(final Join p0);
    
    List<Join> getJoins();
    
    Object clone();
    
    boolean getLockStatus();
    
    void setLock(final boolean p0);
    
    boolean containsSubQuery();
    
    List getDerivedTables();
    
    List<DerivedColumn> getDerivedColumns();
    
    boolean isDistinct();
    
    void setDistinct(final boolean p0);
    
    void setParent(final SelectQuery p0);
    
    SelectQuery getParent();
    
    void setParallelSelect(final boolean p0, final int p1);
    
    boolean isParallelSelect();
    
    int getParallelWorkers();
    
    void addIndexHint(final Table p0, final IndexHintClause p1);
    
    void addIndexHint(final Table p0, final List<IndexHintClause> p1);
    
    Map<Table, List<IndexHintClause>> getIndexHintMap();
    
    List<IndexHintClause> getIndexHint(final Table p0);
    
    List<IndexHintClause> removeIndexHint(final Table p0);
    
    boolean removeIndexHint(final Table p0, final IndexHintClause p1);
    
    void setCached(final boolean p0);
    
    boolean isCached();
    
    void processRemoveColumns();
    
    public enum Clause
    {
        SELECT("select"), 
        JOIN("join"), 
        WHERE("where"), 
        GROUPBY("groupby"), 
        ORDERBY("orderby");
        
        String clause;
        
        private Clause(final String cls) {
            this.clause = null;
            this.clause = cls;
        }
        
        public boolean equals(final String cls) {
            return this.clause.equalsIgnoreCase(cls);
        }
    }
}
