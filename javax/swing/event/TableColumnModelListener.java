package javax.swing.event;

import java.util.EventListener;

public interface TableColumnModelListener extends EventListener
{
    void columnAdded(final TableColumnModelEvent p0);
    
    void columnRemoved(final TableColumnModelEvent p0);
    
    void columnMoved(final TableColumnModelEvent p0);
    
    void columnMarginChanged(final ChangeEvent p0);
    
    void columnSelectionChanged(final ListSelectionEvent p0);
}
