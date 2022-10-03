package javax.swing.event;

import java.util.EventListener;

public interface CellEditorListener extends EventListener
{
    void editingStopped(final ChangeEvent p0);
    
    void editingCanceled(final ChangeEvent p0);
}
