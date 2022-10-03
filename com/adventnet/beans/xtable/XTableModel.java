package com.adventnet.beans.xtable;

import javax.swing.table.TableModel;

public interface XTableModel extends TableModel
{
    void sortModel(final SortColumn[] p0) throws ModelException;
    
    SortColumn[] getModelSortedColumns();
    
    void sortView(final SortColumn[] p0) throws ModelException;
    
    SortColumn[] getViewSortedColumns();
}
