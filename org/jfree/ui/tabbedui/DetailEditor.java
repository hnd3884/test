package org.jfree.ui.tabbedui;

import javax.swing.JComponent;

public abstract class DetailEditor extends JComponent
{
    private Object object;
    private boolean confirmed;
    
    public abstract void clear();
    
    protected abstract void fillObject();
    
    public Object getObject() {
        return this.object;
    }
    
    public boolean isConfirmed() {
        return this.confirmed;
    }
    
    protected static int parseInt(final String text, final int def) {
        try {
            return Integer.parseInt(text);
        }
        catch (final NumberFormatException ex) {
            return def;
        }
    }
    
    protected void setConfirmed(final boolean confirmed) {
        final boolean oldConfirmed = this.confirmed;
        this.firePropertyChange("confirmed", oldConfirmed, this.confirmed = confirmed);
    }
    
    public void setObject(final Object object) {
        if (object == null) {
            throw new NullPointerException();
        }
        this.object = object;
        this.setConfirmed(false);
        this.fillObject();
    }
    
    public void update() {
        if (this.object == null) {
            throw new IllegalStateException();
        }
        this.updateObject(this.object);
        this.setConfirmed(false);
    }
    
    protected abstract void updateObject(final Object p0);
}
