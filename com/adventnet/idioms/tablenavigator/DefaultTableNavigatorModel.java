package com.adventnet.idioms.tablenavigator;

import com.adventnet.beans.xtable.ModelException;
import java.util.Locale;
import javax.swing.table.TableModel;
import com.adventnet.beans.rangenavigator.events.NavigationEvent;
import java.util.EventListener;
import com.adventnet.beans.rangenavigator.events.NavigationListener;
import java.util.Vector;
import com.adventnet.beans.xtable.SortColumn;
import com.adventnet.beans.xtable.DefaultXTableModel;

public class DefaultTableNavigatorModel extends DefaultXTableModel implements TableNavigatorModel
{
    private String dummy;
    private long pageLength;
    private long from;
    private long to;
    private SortColumn[] modelSortedCols;
    private SortColumn[] viewSortedCols;
    private boolean superCall;
    
    public DefaultTableNavigatorModel() {
        this(20, 4);
    }
    
    public DefaultTableNavigatorModel(final int n, final int n2) {
        super(n, n2);
        this.pageLength = 10L;
        this.from = 1L;
        this.to = 20L;
        this.superCall = true;
        this.superCall = false;
        this.dummy = "";
        this.init();
    }
    
    public DefaultTableNavigatorModel(final Vector vector, final Vector vector2) {
        super(vector, vector2);
        this.pageLength = 10L;
        this.from = 1L;
        this.to = 20L;
        this.superCall = true;
        this.superCall = false;
        this.dummy = "";
        this.init();
    }
    
    public DefaultTableNavigatorModel(final Object[][] array, final Object[] array2) {
        super(array, array2);
        this.pageLength = 10L;
        this.from = 1L;
        this.to = 20L;
        this.superCall = true;
        this.superCall = false;
        this.dummy = "";
        this.init();
    }
    
    private void init() {
        if (this.getTotalRecordsCount() == 0L) {
            this.from = 0L;
            this.to = 0L;
        }
        else {
            this.from = 1L;
            this.to = this.from + this.pageLength - 1L;
            if (this.to > this.getTotalRecordsCount()) {
                this.to = this.getTotalRecordsCount();
            }
        }
        this.showRange(this.getStartIndex(), this.getEndIndex());
    }
    
    public long getPageLength() {
        return this.pageLength;
    }
    
    public long getStartIndex() {
        return this.from;
    }
    
    public long getEndIndex() {
        return this.to;
    }
    
    public long getTotalRecordsCount() {
        return super.getRowCount();
    }
    
    public void setPageLength(final long pageLength) {
        if (this.pageLength == pageLength) {
            return;
        }
        if (this.validate(this.from, this.to, this.getTotalRecordsCount(), pageLength)) {
            this.pageLength = pageLength;
            this.to = this.from + pageLength - 1L;
            if (this.to > this.getTotalRecordsCount()) {
                this.to = this.getTotalRecordsCount();
            }
            this.adjustValues();
        }
        this.fireTableDataChanged();
        this.fireNavigationEvent();
    }
    
    public void showRange(final long from, final long to) {
        if (this.from == from && this.to == to) {
            return;
        }
        if (this.validate(from, to, this.getTotalRecordsCount(), this.pageLength)) {
            this.from = from;
            this.to = to;
            if (this.to > this.getTotalRecordsCount()) {
                this.to = this.getTotalRecordsCount();
            }
            this.pageLength = this.to - this.from + 1L;
            this.adjustValues();
        }
        this.fireTableDataChanged();
        this.fireNavigationEvent();
    }
    
    public int getRowCount() {
        if (this.dummy == null || this.superCall || this.getTotalRecordsCount() == 0L) {
            return super.getRowCount();
        }
        return (int)this.getEndIndex() - (int)this.getStartIndex() + 1;
    }
    
    public Object getValueAt(final int n, final int n2) {
        if (this.dummy == null || this.superCall || this.getTotalRecordsCount() == 0L) {
            return super.getValueAt(n, n2);
        }
        return super.getValueAt(n + (int)this.getStartIndex() - 1, n2);
    }
    
    public void setValueAt(final Object o, final int n, final int n2) {
        if (this.dummy == null || this.superCall || this.getTotalRecordsCount() == 0L) {
            super.setValueAt(o, n, n2);
        }
        super.setValueAt(o, n + (int)this.getStartIndex() - 1, n2);
    }
    
    public void addNavigationListener(final NavigationListener navigationListener) {
        this.listenerList.add(NavigationListener.class, navigationListener);
    }
    
    public void removeNavigationListener(final NavigationListener navigationListener) {
        this.listenerList.remove(NavigationListener.class, navigationListener);
    }
    
    private void fireNavigationEvent() {
        final EventListener[] listeners = this.listenerList.getListeners((Class<EventListener>)NavigationListener.class);
        for (int i = 0; i < listeners.length; ++i) {
            ((NavigationListener)listeners[i]).navigationChanged(new NavigationEvent((Object)this));
        }
    }
    
    public void sortModel(final SortColumn[] modelSortedCols) throws ModelException {
        final DefaultXTableModel defaultXTableModel = new DefaultXTableModel(super.getDataVector(), super.columnIdentifiers);
        this.modelSortedCols = modelSortedCols;
        if (modelSortedCols != null) {
            final DefaultXTableModel defaultXTableModel2 = (DefaultXTableModel)this.getTableModelSorter().sortModel((TableModel)defaultXTableModel, modelSortedCols, (Locale)null);
            for (int i = 0; i < defaultXTableModel2.getRowCount(); ++i) {
                for (int j = 0; j < defaultXTableModel2.getColumnCount(); ++j) {
                    super.setValueAt(defaultXTableModel2.getValueAt(i, j), i, j);
                }
            }
        }
    }
    
    public void sortView(final SortColumn[] viewSortedCols) throws ModelException {
        this.viewSortedCols = viewSortedCols;
        if (viewSortedCols != null) {
            this.getTableModelSorter().sortView((TableModel)this, viewSortedCols, (Locale)null);
        }
    }
    
    public SortColumn[] getViewSortedColumns() {
        return this.viewSortedCols;
    }
    
    public SortColumn[] getModelSortedColumns() {
        return this.modelSortedCols;
    }
    
    public void addRow(final Object[] array) {
        this.superCall = true;
        super.addRow(array);
        this.superCall = false;
        this.adjustValues();
        this.fireNavigationEvent();
    }
    
    public void removeRow(final int n) {
        this.superCall = true;
        super.removeRow(n);
        this.superCall = false;
        this.adjustValues();
        this.fireNavigationEvent();
    }
    
    private void adjustValues() {
        if (this.getTotalRecordsCount() == 0L) {
            this.from = 0L;
            this.to = 0L;
            return;
        }
        if (this.from <= 0L && this.getTotalRecordsCount() > 0L) {
            this.from = 1L;
            this.to = this.from + this.pageLength - 1L;
        }
        if (this.to > this.getTotalRecordsCount()) {
            this.to = this.getTotalRecordsCount();
            this.from = this.to - this.pageLength;
            if (this.from < 0L) {
                this.from = ((this.getTotalRecordsCount() > 0L) ? 1 : 0);
            }
        }
        if (this.to - this.from + 1L < this.pageLength && this.getTotalRecordsCount() - this.pageLength < this.from) {
            this.to = this.getTotalRecordsCount();
        }
    }
    
    public boolean validate(final long n, final long n2, final long n3, final long n4) {
        return n >= 0L && n2 >= 0L && n4 > 0L && n3 >= 0L && ((n != 0L && n2 != 0L) || n3 == 0L) && n <= n2 && n3 >= n;
    }
}
