package org.jfree.data.general;

import java.beans.PropertyChangeListener;
import org.jfree.util.ObjectUtilities;
import java.beans.PropertyChangeSupport;
import javax.swing.event.EventListenerList;
import java.io.Serializable;

public abstract class Series implements Cloneable, Serializable
{
    private static final long serialVersionUID = -6906561437538683581L;
    private Comparable key;
    private String description;
    private EventListenerList listeners;
    private PropertyChangeSupport propertyChangeSupport;
    private boolean notify;
    
    protected Series(final Comparable key) {
        this(key, null);
    }
    
    protected Series(final Comparable key, final String description) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        this.key = key;
        this.description = description;
        this.listeners = new EventListenerList();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.notify = true;
    }
    
    public Comparable getKey() {
        return this.key;
    }
    
    public void setKey(final Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        final Comparable old = this.key;
        this.key = key;
        this.propertyChangeSupport.firePropertyChange("Key", old, key);
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        final String old = this.description;
        this.description = description;
        this.propertyChangeSupport.firePropertyChange("Description", old, description);
    }
    
    public boolean getNotify() {
        return this.notify;
    }
    
    public void setNotify(final boolean notify) {
        if (this.notify != notify) {
            this.notify = notify;
            this.fireSeriesChanged();
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        final Series clone = (Series)super.clone();
        clone.listeners = new EventListenerList();
        clone.propertyChangeSupport = new PropertyChangeSupport(clone);
        return clone;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Series)) {
            return false;
        }
        final Series that = (Series)obj;
        return this.getKey().equals(that.getKey()) && ObjectUtilities.equal((Object)this.getDescription(), (Object)that.getDescription());
    }
    
    public int hashCode() {
        int result = this.key.hashCode();
        result = 29 * result + ((this.description != null) ? this.description.hashCode() : 0);
        return result;
    }
    
    public void addChangeListener(final SeriesChangeListener listener) {
        this.listeners.add(SeriesChangeListener.class, listener);
    }
    
    public void removeChangeListener(final SeriesChangeListener listener) {
        this.listeners.remove(SeriesChangeListener.class, listener);
    }
    
    public void fireSeriesChanged() {
        if (this.notify) {
            this.notifyListeners(new SeriesChangeEvent(this));
        }
    }
    
    protected void notifyListeners(final SeriesChangeEvent event) {
        final Object[] listenerList = this.listeners.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == SeriesChangeListener.class) {
                ((SeriesChangeListener)listenerList[i + 1]).seriesChanged(event);
            }
        }
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    protected void firePropertyChange(final String property, final Object oldValue, final Object newValue) {
        this.propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
    }
}
