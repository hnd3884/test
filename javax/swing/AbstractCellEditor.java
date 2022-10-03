package javax.swing;

import javax.swing.event.CellEditorListener;
import java.util.EventObject;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import java.io.Serializable;

public abstract class AbstractCellEditor implements CellEditor, Serializable
{
    protected EventListenerList listenerList;
    protected transient ChangeEvent changeEvent;
    
    public AbstractCellEditor() {
        this.listenerList = new EventListenerList();
        this.changeEvent = null;
    }
    
    @Override
    public boolean isCellEditable(final EventObject eventObject) {
        return true;
    }
    
    @Override
    public boolean shouldSelectCell(final EventObject eventObject) {
        return true;
    }
    
    @Override
    public boolean stopCellEditing() {
        this.fireEditingStopped();
        return true;
    }
    
    @Override
    public void cancelCellEditing() {
        this.fireEditingCanceled();
    }
    
    @Override
    public void addCellEditorListener(final CellEditorListener cellEditorListener) {
        this.listenerList.add(CellEditorListener.class, cellEditorListener);
    }
    
    @Override
    public void removeCellEditorListener(final CellEditorListener cellEditorListener) {
        this.listenerList.remove(CellEditorListener.class, cellEditorListener);
    }
    
    public CellEditorListener[] getCellEditorListeners() {
        return this.listenerList.getListeners(CellEditorListener.class);
    }
    
    protected void fireEditingStopped() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == CellEditorListener.class) {
                if (this.changeEvent == null) {
                    this.changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener)listenerList[i + 1]).editingStopped(this.changeEvent);
            }
        }
    }
    
    protected void fireEditingCanceled() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == CellEditorListener.class) {
                if (this.changeEvent == null) {
                    this.changeEvent = new ChangeEvent(this);
                }
                ((CellEditorListener)listenerList[i + 1]).editingCanceled(this.changeEvent);
            }
        }
    }
}
