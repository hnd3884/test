package javax.swing;

import java.awt.event.ActionListener;
import java.awt.Component;

public interface ComboBoxEditor
{
    Component getEditorComponent();
    
    void setItem(final Object p0);
    
    Object getItem();
    
    void selectAll();
    
    void addActionListener(final ActionListener p0);
    
    void removeActionListener(final ActionListener p0);
}
