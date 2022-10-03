package com.adventnet.ds.query;

import java.util.List;
import java.io.Serializable;

public interface Query extends Serializable, Cloneable
{
    void setRange(final Range p0);
    
    Range getRange();
    
    Object clone();
    
    void addSortColumn(final SortColumn p0);
    
    void addSortColumn(final SortColumn p0, final int p1);
    
    List getSortColumns();
    
    List<Column> getSelectColumns();
    
    boolean removeSortColumn(final SortColumn p0);
    
    SortColumn removeSortColumn(final int p0);
}
