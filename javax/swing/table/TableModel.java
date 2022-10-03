package javax.swing.table;

import javax.swing.event.TableModelListener;

public interface TableModel
{
    int getRowCount();
    
    int getColumnCount();
    
    String getColumnName(final int p0);
    
    Class<?> getColumnClass(final int p0);
    
    boolean isCellEditable(final int p0, final int p1);
    
    Object getValueAt(final int p0, final int p1);
    
    void setValueAt(final Object p0, final int p1, final int p2);
    
    void addTableModelListener(final TableModelListener p0);
    
    void removeTableModelListener(final TableModelListener p0);
}
