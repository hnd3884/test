package com.adventnet.beans.xtable;

import java.util.Locale;
import javax.swing.table.TableModel;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class DefaultXTableModel extends DefaultTableModel implements XTableModel
{
    private DefaultTableModelSorter ds;
    private SortColumn[] modelSortedCols;
    private SortColumn[] viewSortedCols;
    private TableModelSorter tableModelSorter;
    private boolean editable;
    
    public DefaultXTableModel() {
        this(4, 4);
    }
    
    public DefaultXTableModel(final int n, final int n2) {
        super(n, n2);
        this.ds = new DefaultTableModelSorter();
        this.editable = true;
        this.setTableModelSorter(this.ds);
    }
    
    public DefaultXTableModel(final Vector vector, final Vector vector2) {
        super(vector, vector2);
        this.ds = new DefaultTableModelSorter();
        this.editable = true;
        this.setTableModelSorter(this.ds);
    }
    
    public DefaultXTableModel(final Object[][] array, final Object[] array2) {
        super(array, array2);
        this.ds = new DefaultTableModelSorter();
        this.editable = true;
        this.setTableModelSorter(this.ds);
    }
    
    public void setTableModelSorter(final TableModelSorter tableModelSorter) {
        if (tableModelSorter != null && tableModelSorter != this.getTableModelSorter()) {
            this.tableModelSorter = tableModelSorter;
        }
    }
    
    public TableModelSorter getTableModelSorter() {
        return this.tableModelSorter;
    }
    
    public SortColumn[] getModelSortedColumns() {
        return this.modelSortedCols;
    }
    
    public void sortModel(final SortColumn[] modelSortedCols) throws ModelException {
        this.modelSortedCols = modelSortedCols;
        if (modelSortedCols != null) {
            this.getTableModelSorter().sortModel(this, modelSortedCols, null);
        }
    }
    
    public void sortView(final SortColumn[] viewSortedCols) throws ModelException {
        this.viewSortedCols = viewSortedCols;
        if (viewSortedCols != null) {
            this.getTableModelSorter().sortView(this, viewSortedCols, null);
        }
    }
    
    public SortColumn[] getViewSortedColumns() {
        return this.viewSortedCols;
    }
}
