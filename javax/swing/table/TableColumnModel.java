package javax.swing.table;

import javax.swing.event.TableColumnModelListener;
import javax.swing.ListSelectionModel;
import java.util.Enumeration;

public interface TableColumnModel
{
    void addColumn(final TableColumn p0);
    
    void removeColumn(final TableColumn p0);
    
    void moveColumn(final int p0, final int p1);
    
    void setColumnMargin(final int p0);
    
    int getColumnCount();
    
    Enumeration<TableColumn> getColumns();
    
    int getColumnIndex(final Object p0);
    
    TableColumn getColumn(final int p0);
    
    int getColumnMargin();
    
    int getColumnIndexAtX(final int p0);
    
    int getTotalColumnWidth();
    
    void setColumnSelectionAllowed(final boolean p0);
    
    boolean getColumnSelectionAllowed();
    
    int[] getSelectedColumns();
    
    int getSelectedColumnCount();
    
    void setSelectionModel(final ListSelectionModel p0);
    
    ListSelectionModel getSelectionModel();
    
    void addColumnModelListener(final TableColumnModelListener p0);
    
    void removeColumnModelListener(final TableColumnModelListener p0);
}
