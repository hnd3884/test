package com.adventnet.beans.xtable;

public class SortColumn
{
    private int colIndex;
    private boolean asc;
    
    public SortColumn(final int colIndex, final boolean asc) {
        this.colIndex = colIndex;
        this.asc = asc;
    }
    
    public void setColumnIndex(final int colIndex) {
        this.colIndex = colIndex;
    }
    
    public int getColumnIndex() {
        return this.colIndex;
    }
    
    public boolean isAscending() {
        return this.asc;
    }
    
    public void setAscending(final boolean asc) {
        this.asc = asc;
    }
}
