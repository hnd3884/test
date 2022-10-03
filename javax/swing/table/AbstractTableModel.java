package javax.swing.table;

import java.util.EventListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.EventListenerList;
import java.io.Serializable;

public abstract class AbstractTableModel implements TableModel, Serializable
{
    protected EventListenerList listenerList;
    
    public AbstractTableModel() {
        this.listenerList = new EventListenerList();
    }
    
    @Override
    public String getColumnName(int i) {
        String string = "";
        while (i >= 0) {
            string = (char)((char)(i % 26) + 'A') + string;
            i = i / 26 - 1;
        }
        return string;
    }
    
    public int findColumn(final String s) {
        for (int i = 0; i < this.getColumnCount(); ++i) {
            if (s.equals(this.getColumnName(i))) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public Class<?> getColumnClass(final int n) {
        return Object.class;
    }
    
    @Override
    public boolean isCellEditable(final int n, final int n2) {
        return false;
    }
    
    @Override
    public void setValueAt(final Object o, final int n, final int n2) {
    }
    
    @Override
    public void addTableModelListener(final TableModelListener tableModelListener) {
        this.listenerList.add(TableModelListener.class, tableModelListener);
    }
    
    @Override
    public void removeTableModelListener(final TableModelListener tableModelListener) {
        this.listenerList.remove(TableModelListener.class, tableModelListener);
    }
    
    public TableModelListener[] getTableModelListeners() {
        return this.listenerList.getListeners(TableModelListener.class);
    }
    
    public void fireTableDataChanged() {
        this.fireTableChanged(new TableModelEvent(this));
    }
    
    public void fireTableStructureChanged() {
        this.fireTableChanged(new TableModelEvent(this, -1));
    }
    
    public void fireTableRowsInserted(final int n, final int n2) {
        this.fireTableChanged(new TableModelEvent(this, n, n2, -1, 1));
    }
    
    public void fireTableRowsUpdated(final int n, final int n2) {
        this.fireTableChanged(new TableModelEvent(this, n, n2, -1, 0));
    }
    
    public void fireTableRowsDeleted(final int n, final int n2) {
        this.fireTableChanged(new TableModelEvent(this, n, n2, -1, -1));
    }
    
    public void fireTableCellUpdated(final int n, final int n2) {
        this.fireTableChanged(new TableModelEvent(this, n, n, n2));
    }
    
    public void fireTableChanged(final TableModelEvent tableModelEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == TableModelListener.class) {
                ((TableModelListener)listenerList[i + 1]).tableChanged(tableModelEvent);
            }
        }
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        return this.listenerList.getListeners(clazz);
    }
}
