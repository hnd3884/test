package sun.swing;

import java.beans.PropertyChangeListener;
import javax.swing.Action;

public abstract class UIAction implements Action
{
    private String name;
    
    public UIAction(final String name) {
        this.name = name;
    }
    
    public final String getName() {
        return this.name;
    }
    
    @Override
    public Object getValue(final String s) {
        if (s == "Name") {
            return this.name;
        }
        return null;
    }
    
    @Override
    public void putValue(final String s, final Object o) {
    }
    
    @Override
    public void setEnabled(final boolean b) {
    }
    
    @Override
    public final boolean isEnabled() {
        return this.isEnabled(null);
    }
    
    public boolean isEnabled(final Object o) {
        return true;
    }
    
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
    }
    
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
    }
}
