package javax.swing;

import java.util.EventListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ChangeEvent;
import java.io.Serializable;

public class DefaultSingleSelectionModel implements SingleSelectionModel, Serializable
{
    protected transient ChangeEvent changeEvent;
    protected EventListenerList listenerList;
    private int index;
    
    public DefaultSingleSelectionModel() {
        this.changeEvent = null;
        this.listenerList = new EventListenerList();
        this.index = -1;
    }
    
    @Override
    public int getSelectedIndex() {
        return this.index;
    }
    
    @Override
    public void setSelectedIndex(final int index) {
        if (this.index != index) {
            this.index = index;
            this.fireStateChanged();
        }
    }
    
    @Override
    public void clearSelection() {
        this.setSelectedIndex(-1);
    }
    
    @Override
    public boolean isSelected() {
        boolean b = false;
        if (this.getSelectedIndex() != -1) {
            b = true;
        }
        return b;
    }
    
    @Override
    public void addChangeListener(final ChangeListener changeListener) {
        this.listenerList.add(ChangeListener.class, changeListener);
    }
    
    @Override
    public void removeChangeListener(final ChangeListener changeListener) {
        this.listenerList.remove(ChangeListener.class, changeListener);
    }
    
    public ChangeListener[] getChangeListeners() {
        return this.listenerList.getListeners(ChangeListener.class);
    }
    
    protected void fireStateChanged() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ChangeListener.class) {
                if (this.changeEvent == null) {
                    this.changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listenerList[i + 1]).stateChanged(this.changeEvent);
            }
        }
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        return this.listenerList.getListeners(clazz);
    }
}
