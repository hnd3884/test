package org.jfree.ui;

import javax.swing.table.AbstractTableModel;

public abstract class SortableTableModel extends AbstractTableModel
{
    private int sortingColumn;
    private boolean ascending;
    
    public SortableTableModel() {
        this.sortingColumn = -1;
        this.ascending = true;
    }
    
    public int getSortingColumn() {
        return this.sortingColumn;
    }
    
    public boolean isAscending() {
        return this.ascending;
    }
    
    public boolean isSortable(final int column) {
        return false;
    }
    
    public void setAscending(final boolean flag) {
        this.ascending = flag;
    }
    
    public void sortByColumn(final int column, final boolean ascending) {
        if (this.isSortable(column)) {
            this.sortingColumn = column;
        }
    }
}
