package javax.swing;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.beans.PropertyChangeListener;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.beans.PropertyChangeEvent;
import javax.swing.event.SwingPropertyChangeSupport;
import java.io.Serializable;

public abstract class AbstractAction implements Action, Cloneable, Serializable
{
    private static Boolean RECONFIGURE_ON_NULL;
    protected boolean enabled;
    private transient ArrayTable arrayTable;
    protected SwingPropertyChangeSupport changeSupport;
    
    static boolean shouldReconfigure(final PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName() == null) {
            synchronized (AbstractAction.class) {
                if (AbstractAction.RECONFIGURE_ON_NULL == null) {
                    AbstractAction.RECONFIGURE_ON_NULL = Boolean.valueOf(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("swing.actions.reconfigureOnNull", "false")));
                }
                return AbstractAction.RECONFIGURE_ON_NULL;
            }
        }
        return false;
    }
    
    static void setEnabledFromAction(final JComponent component, final Action action) {
        component.setEnabled(action == null || action.isEnabled());
    }
    
    static void setToolTipTextFromAction(final JComponent component, final Action action) {
        component.setToolTipText((action != null) ? ((String)action.getValue("ShortDescription")) : null);
    }
    
    static boolean hasSelectedKey(final Action action) {
        return action != null && action.getValue("SwingSelectedKey") != null;
    }
    
    static boolean isSelected(final Action action) {
        return Boolean.TRUE.equals(action.getValue("SwingSelectedKey"));
    }
    
    public AbstractAction() {
        this.enabled = true;
    }
    
    public AbstractAction(final String s) {
        this.enabled = true;
        this.putValue("Name", s);
    }
    
    public AbstractAction(final String s, final Icon icon) {
        this(s);
        this.putValue("SmallIcon", icon);
    }
    
    @Override
    public Object getValue(final String s) {
        if (s == "enabled") {
            return this.enabled;
        }
        if (this.arrayTable == null) {
            return null;
        }
        return this.arrayTable.get(s);
    }
    
    @Override
    public void putValue(final String s, Object value) {
        Object o = null;
        if (s == "enabled") {
            if (value == null || !(value instanceof Boolean)) {
                value = false;
            }
            o = this.enabled;
            this.enabled = (boolean)value;
        }
        else {
            if (this.arrayTable == null) {
                this.arrayTable = new ArrayTable();
            }
            if (this.arrayTable.containsKey(s)) {
                o = this.arrayTable.get(s);
            }
            if (value == null) {
                this.arrayTable.remove(s);
            }
            else {
                this.arrayTable.put(s, value);
            }
        }
        this.firePropertyChange(s, o, value);
    }
    
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        final boolean enabled2 = this.enabled;
        if (enabled2 != enabled) {
            this.enabled = enabled;
            this.firePropertyChange("enabled", enabled2, enabled);
        }
    }
    
    public Object[] getKeys() {
        if (this.arrayTable == null) {
            return null;
        }
        final Object[] array = new Object[this.arrayTable.size()];
        this.arrayTable.getKeys(array);
        return array;
    }
    
    protected void firePropertyChange(final String s, final Object o, final Object o2) {
        if (this.changeSupport == null || (o != null && o2 != null && o.equals(o2))) {
            return;
        }
        this.changeSupport.firePropertyChange(s, o, o2);
    }
    
    @Override
    public synchronized void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport == null) {
            this.changeSupport = new SwingPropertyChangeSupport(this);
        }
        this.changeSupport.addPropertyChangeListener(propertyChangeListener);
    }
    
    @Override
    public synchronized void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.changeSupport == null) {
            return;
        }
        this.changeSupport.removePropertyChangeListener(propertyChangeListener);
    }
    
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        if (this.changeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return this.changeSupport.getPropertyChangeListeners();
    }
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        final AbstractAction abstractAction = (AbstractAction)super.clone();
        synchronized (this) {
            if (this.arrayTable != null) {
                abstractAction.arrayTable = (ArrayTable)this.arrayTable.clone();
            }
        }
        return abstractAction;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        ArrayTable.writeArrayTable(objectOutputStream, this.arrayTable);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        for (int i = objectInputStream.readInt() - 1; i >= 0; --i) {
            this.putValue((String)objectInputStream.readObject(), objectInputStream.readObject());
        }
    }
}
