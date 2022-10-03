package javax.swing.colorchooser;

import javax.swing.event.ChangeListener;
import java.awt.Color;
import javax.swing.event.EventListenerList;
import javax.swing.event.ChangeEvent;
import java.io.Serializable;

public class DefaultColorSelectionModel implements ColorSelectionModel, Serializable
{
    protected transient ChangeEvent changeEvent;
    protected EventListenerList listenerList;
    private Color selectedColor;
    
    public DefaultColorSelectionModel() {
        this.changeEvent = null;
        this.listenerList = new EventListenerList();
        this.selectedColor = Color.white;
    }
    
    public DefaultColorSelectionModel(final Color selectedColor) {
        this.changeEvent = null;
        this.listenerList = new EventListenerList();
        this.selectedColor = selectedColor;
    }
    
    @Override
    public Color getSelectedColor() {
        return this.selectedColor;
    }
    
    @Override
    public void setSelectedColor(final Color selectedColor) {
        if (selectedColor != null && !this.selectedColor.equals(selectedColor)) {
            this.selectedColor = selectedColor;
            this.fireStateChanged();
        }
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
}
