package javax.swing.table;

import javax.swing.DefaultListSelectionModel;
import java.beans.PropertyChangeEvent;
import java.util.EventListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelListener;
import java.util.Enumeration;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.ListSelectionModel;
import java.util.Vector;
import java.io.Serializable;
import javax.swing.event.ListSelectionListener;
import java.beans.PropertyChangeListener;

public class DefaultTableColumnModel implements TableColumnModel, PropertyChangeListener, ListSelectionListener, Serializable
{
    protected Vector<TableColumn> tableColumns;
    protected ListSelectionModel selectionModel;
    protected int columnMargin;
    protected EventListenerList listenerList;
    protected transient ChangeEvent changeEvent;
    protected boolean columnSelectionAllowed;
    protected int totalColumnWidth;
    
    public DefaultTableColumnModel() {
        this.listenerList = new EventListenerList();
        this.changeEvent = null;
        this.tableColumns = new Vector<TableColumn>();
        this.setSelectionModel(this.createSelectionModel());
        this.setColumnMargin(1);
        this.invalidateWidthCache();
        this.setColumnSelectionAllowed(false);
    }
    
    @Override
    public void addColumn(final TableColumn tableColumn) {
        if (tableColumn == null) {
            throw new IllegalArgumentException("Object is null");
        }
        this.tableColumns.addElement(tableColumn);
        tableColumn.addPropertyChangeListener(this);
        this.invalidateWidthCache();
        this.fireColumnAdded(new TableColumnModelEvent(this, 0, this.getColumnCount() - 1));
    }
    
    @Override
    public void removeColumn(final TableColumn tableColumn) {
        final int index = this.tableColumns.indexOf(tableColumn);
        if (index != -1) {
            if (this.selectionModel != null) {
                this.selectionModel.removeIndexInterval(index, index);
            }
            tableColumn.removePropertyChangeListener(this);
            this.tableColumns.removeElementAt(index);
            this.invalidateWidthCache();
            this.fireColumnRemoved(new TableColumnModelEvent(this, index, 0));
        }
    }
    
    @Override
    public void moveColumn(final int n, final int n2) {
        if (n < 0 || n >= this.getColumnCount() || n2 < 0 || n2 >= this.getColumnCount()) {
            throw new IllegalArgumentException("moveColumn() - Index out of range");
        }
        if (n == n2) {
            this.fireColumnMoved(new TableColumnModelEvent(this, n, n2));
            return;
        }
        final TableColumn tableColumn = this.tableColumns.elementAt(n);
        this.tableColumns.removeElementAt(n);
        final boolean selectedIndex = this.selectionModel.isSelectedIndex(n);
        this.selectionModel.removeIndexInterval(n, n);
        this.tableColumns.insertElementAt(tableColumn, n2);
        this.selectionModel.insertIndexInterval(n2, 1, true);
        if (selectedIndex) {
            this.selectionModel.addSelectionInterval(n2, n2);
        }
        else {
            this.selectionModel.removeSelectionInterval(n2, n2);
        }
        this.fireColumnMoved(new TableColumnModelEvent(this, n, n2));
    }
    
    @Override
    public void setColumnMargin(final int columnMargin) {
        if (columnMargin != this.columnMargin) {
            this.columnMargin = columnMargin;
            this.fireColumnMarginChanged();
        }
    }
    
    @Override
    public int getColumnCount() {
        return this.tableColumns.size();
    }
    
    @Override
    public Enumeration<TableColumn> getColumns() {
        return this.tableColumns.elements();
    }
    
    @Override
    public int getColumnIndex(final Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Identifier is null");
        }
        final Enumeration<TableColumn> columns = this.getColumns();
        int n = 0;
        while (columns.hasMoreElements()) {
            if (o.equals(columns.nextElement().getIdentifier())) {
                return n;
            }
            ++n;
        }
        throw new IllegalArgumentException("Identifier not found");
    }
    
    @Override
    public TableColumn getColumn(final int n) {
        return this.tableColumns.elementAt(n);
    }
    
    @Override
    public int getColumnMargin() {
        return this.columnMargin;
    }
    
    @Override
    public int getColumnIndexAtX(int n) {
        if (n < 0) {
            return -1;
        }
        for (int columnCount = this.getColumnCount(), i = 0; i < columnCount; ++i) {
            n -= this.getColumn(i).getWidth();
            if (n < 0) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public int getTotalColumnWidth() {
        if (this.totalColumnWidth == -1) {
            this.recalcWidthCache();
        }
        return this.totalColumnWidth;
    }
    
    @Override
    public void setSelectionModel(final ListSelectionModel selectionModel) {
        if (selectionModel == null) {
            throw new IllegalArgumentException("Cannot set a null SelectionModel");
        }
        final ListSelectionModel selectionModel2 = this.selectionModel;
        if (selectionModel != selectionModel2) {
            if (selectionModel2 != null) {
                selectionModel2.removeListSelectionListener(this);
            }
            (this.selectionModel = selectionModel).addListSelectionListener(this);
        }
    }
    
    @Override
    public ListSelectionModel getSelectionModel() {
        return this.selectionModel;
    }
    
    @Override
    public void setColumnSelectionAllowed(final boolean columnSelectionAllowed) {
        this.columnSelectionAllowed = columnSelectionAllowed;
    }
    
    @Override
    public boolean getColumnSelectionAllowed() {
        return this.columnSelectionAllowed;
    }
    
    @Override
    public int[] getSelectedColumns() {
        if (this.selectionModel == null) {
            return new int[0];
        }
        final int minSelectionIndex = this.selectionModel.getMinSelectionIndex();
        final int maxSelectionIndex = this.selectionModel.getMaxSelectionIndex();
        if (minSelectionIndex == -1 || maxSelectionIndex == -1) {
            return new int[0];
        }
        final int[] array = new int[1 + (maxSelectionIndex - minSelectionIndex)];
        int n = 0;
        for (int i = minSelectionIndex; i <= maxSelectionIndex; ++i) {
            if (this.selectionModel.isSelectedIndex(i)) {
                array[n++] = i;
            }
        }
        final int[] array2 = new int[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    @Override
    public int getSelectedColumnCount() {
        if (this.selectionModel != null) {
            final int minSelectionIndex = this.selectionModel.getMinSelectionIndex();
            final int maxSelectionIndex = this.selectionModel.getMaxSelectionIndex();
            int n = 0;
            for (int i = minSelectionIndex; i <= maxSelectionIndex; ++i) {
                if (this.selectionModel.isSelectedIndex(i)) {
                    ++n;
                }
            }
            return n;
        }
        return 0;
    }
    
    @Override
    public void addColumnModelListener(final TableColumnModelListener tableColumnModelListener) {
        this.listenerList.add(TableColumnModelListener.class, tableColumnModelListener);
    }
    
    @Override
    public void removeColumnModelListener(final TableColumnModelListener tableColumnModelListener) {
        this.listenerList.remove(TableColumnModelListener.class, tableColumnModelListener);
    }
    
    public TableColumnModelListener[] getColumnModelListeners() {
        return this.listenerList.getListeners(TableColumnModelListener.class);
    }
    
    protected void fireColumnAdded(final TableColumnModelEvent tableColumnModelEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TableColumnModelListener.class) {
                ((TableColumnModelListener)listenerList[i + 1]).columnAdded(tableColumnModelEvent);
            }
        }
    }
    
    protected void fireColumnRemoved(final TableColumnModelEvent tableColumnModelEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TableColumnModelListener.class) {
                ((TableColumnModelListener)listenerList[i + 1]).columnRemoved(tableColumnModelEvent);
            }
        }
    }
    
    protected void fireColumnMoved(final TableColumnModelEvent tableColumnModelEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TableColumnModelListener.class) {
                ((TableColumnModelListener)listenerList[i + 1]).columnMoved(tableColumnModelEvent);
            }
        }
    }
    
    protected void fireColumnSelectionChanged(final ListSelectionEvent listSelectionEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TableColumnModelListener.class) {
                ((TableColumnModelListener)listenerList[i + 1]).columnSelectionChanged(listSelectionEvent);
            }
        }
    }
    
    protected void fireColumnMarginChanged() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TableColumnModelListener.class) {
                if (this.changeEvent == null) {
                    this.changeEvent = new ChangeEvent(this);
                }
                ((TableColumnModelListener)listenerList[i + 1]).columnMarginChanged(this.changeEvent);
            }
        }
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        return this.listenerList.getListeners(clazz);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        final String propertyName = propertyChangeEvent.getPropertyName();
        if (propertyName == "width" || propertyName == "preferredWidth") {
            this.invalidateWidthCache();
            this.fireColumnMarginChanged();
        }
    }
    
    @Override
    public void valueChanged(final ListSelectionEvent listSelectionEvent) {
        this.fireColumnSelectionChanged(listSelectionEvent);
    }
    
    protected ListSelectionModel createSelectionModel() {
        return new DefaultListSelectionModel();
    }
    
    protected void recalcWidthCache() {
        final Enumeration<TableColumn> columns = this.getColumns();
        this.totalColumnWidth = 0;
        while (columns.hasMoreElements()) {
            this.totalColumnWidth += columns.nextElement().getWidth();
        }
    }
    
    private void invalidateWidthCache() {
        this.totalColumnWidth = -1;
    }
}
