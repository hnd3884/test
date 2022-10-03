package javax.swing;

import javax.swing.event.ChangeListener;

public interface SingleSelectionModel
{
    int getSelectedIndex();
    
    void setSelectedIndex(final int p0);
    
    void clearSelection();
    
    boolean isSelected();
    
    void addChangeListener(final ChangeListener p0);
    
    void removeChangeListener(final ChangeListener p0);
}
