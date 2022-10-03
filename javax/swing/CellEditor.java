package javax.swing;

import javax.swing.event.CellEditorListener;
import java.util.EventObject;

public interface CellEditor
{
    Object getCellEditorValue();
    
    boolean isCellEditable(final EventObject p0);
    
    boolean shouldSelectCell(final EventObject p0);
    
    boolean stopCellEditing();
    
    void cancelCellEditing();
    
    void addCellEditorListener(final CellEditorListener p0);
    
    void removeCellEditorListener(final CellEditorListener p0);
}
