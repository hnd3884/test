package com.adventnet.beans.treetable;

import com.adventnet.beans.xtable.ModelException;
import com.adventnet.beans.xtable.SortColumn;
import javax.swing.tree.TreeModel;

public interface TreeTableModel extends TreeModel
{
    int getColumnCount();
    
    String getColumnName(final int p0);
    
    Class getColumnClass(final int p0);
    
    Object getValueAt(final Object p0, final int p1);
    
    boolean isCellEditable(final Object p0, final int p1);
    
    void setValueAt(final Object p0, final Object p1, final int p2);
    
    void sortView(final Object p0, final SortColumn[] p1) throws ModelException;
    
    SortColumn[] getViewSortedColumns();
    
    void sortModel(final Object p0, final SortColumn[] p1) throws ModelException;
    
    SortColumn[] getModelSortedColumns();
}
