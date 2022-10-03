package org.jfree.ui.tabbedui;

import javax.swing.JMenu;
import javax.swing.JComponent;
import java.beans.PropertyChangeListener;

public interface RootEditor
{
    void addPropertyChangeListener(final PropertyChangeListener p0);
    
    void addPropertyChangeListener(final String p0, final PropertyChangeListener p1);
    
    String getEditorName();
    
    JComponent getMainPanel();
    
    JMenu[] getMenus();
    
    JComponent getToolbar();
    
    boolean isActive();
    
    boolean isEnabled();
    
    void removePropertyChangeListener(final PropertyChangeListener p0);
    
    void removePropertyChangeListener(final String p0, final PropertyChangeListener p1);
    
    void setActive(final boolean p0);
}
