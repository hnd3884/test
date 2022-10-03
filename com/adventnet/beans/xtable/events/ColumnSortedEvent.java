package com.adventnet.beans.xtable.events;

import com.adventnet.beans.xtable.SortColumn;
import java.util.EventObject;

public class ColumnSortedEvent extends EventObject
{
    public static final int VIEW_SORT = 0;
    public static final int MODEL_SORT = 1;
    private SortColumn[] modelSortedCols;
    private SortColumn[] viewSortedCols;
    private int columnViewIndex;
    private int sortType;
    private boolean asc;
    
    public ColumnSortedEvent(final Object o, final SortColumn[] modelSortedCols, final SortColumn[] viewSortedCols, final int sortType) {
        super(o);
        this.modelSortedCols = modelSortedCols;
        this.viewSortedCols = viewSortedCols;
        if (sortType < 0 || sortType > 1) {
            throw new IllegalArgumentException("Illegal value for sort type");
        }
        this.sortType = sortType;
    }
    
    public SortColumn[] getModelSortedColumns() {
        return this.modelSortedCols;
    }
    
    public SortColumn[] getViewSortedColumns() {
        return this.viewSortedCols;
    }
    
    public int getColumnViewIndex() {
        return this.columnViewIndex;
    }
    
    public int getSortType() {
        return this.sortType;
    }
    
    public boolean isAscending() {
        return this.asc;
    }
}
