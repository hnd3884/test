package com.adventnet.ds.query;

import java.util.List;

public interface DeleteQuery
{
    void setCriteria(final Criteria p0);
    
    Criteria getCriteria();
    
    void addSortColumn(final SortColumn p0);
    
    void addSortColumn(final SortColumn p0, final int p1);
    
    boolean removeSortColumn(final SortColumn p0);
    
    SortColumn removeSortColumn(final int p0);
    
    void addSortColumns(final List<SortColumn> p0);
    
    void addSortColumns(final List<SortColumn> p0, final int p1);
    
    List<SortColumn> getSortColumns();
    
    void setLimit(final int p0);
    
    int getLimit();
    
    String getTableName();
    
    void addJoin(final Join p0);
    
    List<Join> getJoins();
    
    List<Table> getTableList();
}
