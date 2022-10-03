package com.adventnet.ds.query;

import java.util.List;

public interface UnionQuery extends Query
{
    UnionQuery union(final Query p0, final boolean p1);
    
    Query getLeftQuery();
    
    Query getRightQuery();
    
    boolean isRetainDuplicateRows();
    
    List<Table> getTableList();
    
    void addSortColumn(final SortColumn p0);
    
    void addSortColumn(final SortColumn p0, final int p1);
    
    List getSortColumns();
    
    List<Column> getSelectColumns();
}
